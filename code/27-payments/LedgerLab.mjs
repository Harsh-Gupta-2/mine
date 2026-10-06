import assert from 'node:assert/strict';
import { writeFile } from 'node:fs/promises';

const TRANSITIONS = {
  AUTHORIZED: ['CAPTURED', 'VOIDED', 'EXPIRED'],
  CAPTURED: ['REFUNDED', 'PARTIALLY_REFUNDED', 'CHARGEBACK'],
  PARTIALLY_REFUNDED: ['REFUNDED', 'PARTIALLY_REFUNDED', 'CHARGEBACK'],
  REFUNDED: [], VOIDED: [], EXPIRED: [], CHARGEBACK: []
};

export const canTransition = (from, to) => (TRANSITIONS[from] ?? []).includes(to);

export class Ledger {
  entries = [];
  post(reference, lines) {
    if (!reference || this.entries.some(entry => entry.reference === reference)) throw new RangeError('unique posting reference required');
    if (!Array.isArray(lines) || lines.length < 2) throw new RangeError('a posting needs at least two lines');
    let balance = 0n;
    for (const line of lines) {
      if (!line.account || !line.currency || !Number.isSafeInteger(line.amountMinor)) throw new RangeError('account, currency and integer minor units required');
      if (line.currency !== lines[0].currency) throw new RangeError('single-currency posting required');
      balance += BigInt(line.amountMinor);
    }
    if (balance !== 0n) throw new RangeError(`unbalanced posting: ${balance}`);
    this.entries.push({ reference, lines: lines.map(line => Object.freeze({ ...line })) });
    return true;
  }
  balance(account) {
    return this.entries.flatMap(entry => entry.lines).filter(line => line.account === account).reduce((sum, line) => sum + line.amountMinor, 0);
  }
}

export class PaymentService {
  #ledger;
  #results = new Map();
  #payments = new Map();
  constructor(ledger) { this.#ledger = ledger; }

  authorize(id, amountMinor, currency) {
    if (!id || !currency || this.#payments.has(id) || !Number.isSafeInteger(amountMinor) || amountMinor <= 0) throw new RangeError('new identity and positive integer amount required');
    this.#payments.set(id, { status: 'AUTHORIZED', authorized: amountMinor, captured: 0, refunded: 0, currency });
  }

  capture(id, idempotencyKey, amountMinor) {
    if (!idempotencyKey || !Number.isSafeInteger(amountMinor) || amountMinor <= 0) throw new RangeError('key and positive integer amount required');
    const scopedKey = JSON.stringify([id, 'capture', idempotencyKey]);
    if (this.#results.has(scopedKey)) {
      const stored = this.#results.get(scopedKey);
      if (stored.amountMinor !== amountMinor) throw new RangeError('idempotency content conflict');
      return { ...stored, replayed: true };
    }
    const payment = this.#payments.get(id);
    if (!payment) throw new RangeError('unknown payment');
    if (!canTransition(payment.status, 'CAPTURED')) throw new RangeError(`illegal transition ${payment.status} -> CAPTURED`);
    if (amountMinor > payment.authorized) throw new RangeError('capture exceeds authorized amount');
    this.#ledger.post(scopedKey, [
      { account: `clearing:${payment.currency}`, amountMinor: -amountMinor, currency: payment.currency },
      { account: `merchant_payable:${payment.currency}`, amountMinor, currency: payment.currency }
    ]);
    payment.status = 'CAPTURED';
    payment.captured = amountMinor;
    const result = { status: 'CAPTURED', amountMinor, replayed: false };
    this.#results.set(scopedKey, { status: 'CAPTURED', amountMinor });
    return result;
  }

  refund(id, idempotencyKey, amountMinor) {
    if (!idempotencyKey || !Number.isSafeInteger(amountMinor) || amountMinor <= 0) throw new RangeError('key and positive integer amount required');
    const scopedKey = JSON.stringify([id, 'refund', idempotencyKey]);
    if (this.#results.has(scopedKey)) {
      const stored = this.#results.get(scopedKey);
      if (stored.amountMinor !== amountMinor) throw new RangeError('idempotency content conflict');
      return stored.status;
    }
    const payment = this.#payments.get(id);
    if (!payment) throw new RangeError('unknown payment');
    const remaining = payment.captured - payment.refunded;
    if (amountMinor > remaining) throw new RangeError('refund exceeds captured amount');
    const next = amountMinor === remaining ? 'REFUNDED' : 'PARTIALLY_REFUNDED';
    if (!canTransition(payment.status, next)) throw new RangeError(`illegal transition ${payment.status} -> ${next}`);
    this.#ledger.post(scopedKey, [
      { account: `merchant_payable:${payment.currency}`, amountMinor: -amountMinor, currency: payment.currency },
      { account: `clearing:${payment.currency}`, amountMinor, currency: payment.currency }
    ]);
    payment.refunded += amountMinor;
    payment.status = next;
    this.#results.set(scopedKey, { status: next, amountMinor });
    return next;
  }

  recordUnknownOutcome(id) {
    const payment = this.#payments.get(id);
    payment.reconciliation = 'PENDING_PROVIDER_CONFIRMATION';
    return payment.status;
  }

  status(id) { return { ...this.#payments.get(id) }; }
}

try {
  const ledger = new Ledger();
  assert.throws(() => ledger.post('bad', [{ account: 'a', amountMinor: 100, currency: 'EUR' }, { account: 'b', amountMinor: -90, currency: 'EUR' }]), RangeError);
  assert.throws(() => ledger.post('bad', [{ account: 'a', amountMinor: 10.5, currency: 'EUR' }, { account: 'b', amountMinor: -10.5, currency: 'EUR' }]), RangeError);
  assert.equal(ledger.entries.length, 0);

  const payments = new PaymentService(ledger);
  payments.authorize('pay-1', 5000, 'EUR');
  assert.throws(() => payments.capture('pay-1', 'key-1', 6000), RangeError);

  const first = payments.capture('pay-1', 'key-1', 5000);
  assert.deepEqual(first, { status: 'CAPTURED', amountMinor: 5000, replayed: false });
  const replay = payments.capture('pay-1', 'key-1', 5000);
  assert.equal(replay.replayed, true);
  assert.equal(ledger.entries.length, 1);
  assert.equal(ledger.balance('merchant_payable:EUR'), 5000);
  assert.equal(ledger.balance('clearing:EUR'), -5000);
  assert.throws(() => payments.capture('pay-1', 'key-1', 4000), /content conflict/);
  assert.throws(() => payments.authorize('pay-1', 5000, 'EUR'), RangeError);

  assert.throws(() => payments.refund('pay-1', 'refund-1', 6000), RangeError);
  assert.equal(payments.refund('pay-1', 'refund-1', 2000), 'PARTIALLY_REFUNDED');
  assert.equal(payments.refund('pay-1', 'refund-1', 2000), 'PARTIALLY_REFUNDED');
  assert.equal(ledger.entries.length, 2);
  assert.throws(() => payments.refund('pay-1', 'refund-1', 1000), /content conflict/);
  assert.equal(payments.refund('pay-1', 'refund-2', 3000), 'REFUNDED');
  assert.equal(payments.refund('pay-1', 'refund-1', 2000), 'PARTIALLY_REFUNDED');
  assert.equal(ledger.balance('merchant_payable:EUR'), 0);
  assert.throws(() => payments.refund('pay-1', 'refund-3', 1000), RangeError);

  payments.authorize('pay-2', 1000, 'EUR');
  assert.equal(payments.recordUnknownOutcome('pay-2'), 'AUTHORIZED');
  assert.equal(payments.status('pay-2').reconciliation, 'PENDING_PROVIDER_CONFIRMATION');
  assert.equal(ledger.entries.length, 3);
  for (const invalidAmount of [0, -1, 1.5, Number.MAX_SAFE_INTEGER + 1]) {
    assert.throws(() => payments.capture('pay-2', 'invalid', invalidAmount), RangeError);
    assert.throws(() => payments.refund('pay-1', 'invalid', invalidAmount), RangeError);
  }
  assert.equal(ledger.entries.length, 3);

  const report = { status: 'PASS', runtime: process.version,
    checks: ['unbalanced and non-integer postings rejected before any entry exists', 'capture above authorization rejected', 'replayed capture key returns stored result without a second posting', 'refunds cannot exceed captured amount and close the payable balance', 'unknown provider outcome stays pending reconciliation rather than succeeding', 'capture content conflict and payment identity overwrite rejected', 'refund replay preserves original result without a second posting and rejects changed content', 'zero negative fractional and unsafe operation amounts rejected'],
    scope: 'In-memory single-currency ledger and state machine, not a payment processor, settlement file, scheme integration or compliance control', timestamp: new Date().toISOString() };
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify(report, null, 2));
  console.log(JSON.stringify(report, null, 2));
} catch (error) {
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify({ status: 'FAIL', reason: error.message }, null, 2));
  throw error;
}
