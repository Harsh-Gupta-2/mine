import assert from 'node:assert/strict';
import { writeFile } from 'node:fs/promises';

export function receiveLamport(local, received) {
  if (![local, received].every(value => Number.isSafeInteger(value) && value >= 0)) throw new RangeError('invalid clock');
  const next = Math.max(local, received) + 1;
  if (!Number.isSafeInteger(next)) throw new RangeError('clock overflow');
  return next;
}

export function before(left, right) {
  if (![...Object.values(left), ...Object.values(right)].every(value => Number.isSafeInteger(value) && value >= 0)) throw new RangeError('invalid vector');
  const nodes = new Set([...Object.keys(left), ...Object.keys(right)]);
  let strictlySmaller = false;
  for (const node of nodes) {
    const first = left[node] ?? 0;
    const second = right[node] ?? 0;
    if (first > second) return false;
    if (first < second) strictlySmaller = true;
  }
  return strictlySmaller;
}

export class FencedRegister {
  epoch = 0;
  value = null;
  apply(epoch, value) {
    if (!Number.isSafeInteger(epoch) || epoch <= 0) throw new RangeError('positive epoch required');
    if (epoch < this.epoch) return false;
    this.epoch = epoch;
    this.value = value;
    return true;
  }
}

try {
  assert.equal(receiveLamport(2, 7), 8);
  assert.throws(() => receiveLamport(Number.MAX_SAFE_INTEGER, 0), RangeError);
  assert.equal(before({ a: 1 }, { a: 1, b: 1 }), true);
  assert.equal(before({ a: 1 }, { a: 1 }), false);
  assert.equal(before({ a: 2, b: 1 }, { a: 1, b: 2 }), false);
  assert.equal(before({ a: 1, b: 2 }, { a: 2, b: 1 }), false);
  const store = new FencedRegister();
  assert.equal(store.apply(1, 'first owner'), true);
  const newlyIssuedEpoch = 2;
  assert.equal(store.apply(1, 'old owner before higher epoch reaches resource'), true);
  assert.equal(store.epoch, 1);
  assert.equal(store.apply(newlyIssuedEpoch, 'new owner'), true);
  assert.equal(store.apply(1, 'delayed stale write'), false);
  assert.equal(store.value, 'new owner');
  const report = { status: 'PASS', runtime: process.version,
    checks: ['Lamport receive rule and overflow guard', 'vector order and concurrent histories', 'higher epoch must reach the resource', 'stale write rejected after resource advances'],
    scope: 'Single-process logical-clock and effect-side fencing model, not leases, consensus, persistence or Redlock validation', timestamp: new Date().toISOString() };
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify(report, null, 2));
  console.log(JSON.stringify(report, null, 2));
} catch (error) {
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify({ status: 'FAIL', reason: error.message }, null, 2));
  throw error;
}