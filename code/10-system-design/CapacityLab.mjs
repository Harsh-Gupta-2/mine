import assert from 'node:assert/strict';
import { writeFile } from 'node:fs/promises';

export function estimate({ rate, residenceSeconds, bytes, days, copies }) {
  for (const value of [rate, residenceSeconds, bytes, days, copies]) {
    if (!Number.isFinite(value) || value <= 0) throw new RangeError('inputs must be finite and positive');
  }
  return {
    meanInFlight: rate * residenceSeconds,
    recordsPerDay: rate * 86400,
    storedBytes: rate * 86400 * bytes * days * copies,
  };
}

export function drainSeconds(backlog, incoming, completed) {
  if (![backlog, incoming, completed].every(value => Number.isFinite(value) && value >= 0)) throw new RangeError('invalid rates');
  if (backlog === 0) return 0;
  return completed > incoming ? backlog / (completed - incoming) : Infinity;
}

export function attemptBudget(totalMillis, attemptMillis, gapMillis, reserveMillis) {
  if (![totalMillis, attemptMillis, gapMillis, reserveMillis].every(Number.isFinite)
      || totalMillis <= 0 || attemptMillis <= 0 || gapMillis < 0 || reserveMillis < 0) throw new RangeError('invalid budget');
  return Math.max(0, Math.floor((totalMillis - reserveMillis + gapMillis) / (attemptMillis + gapMillis)));
}

try {
  const assumed = estimate({ rate: 2000, residenceSeconds: 0.125, bytes: 1000, days: 7, copies: 3 });
  assert.deepEqual(assumed, { meanInFlight: 250, recordsPerDay: 172800000, storedBytes: 3628800000000 });
  assert.equal(drainSeconds(120000, 1800, 2000), 600);
  assert.equal(drainSeconds(120000, 2000, 2000), Infinity);
  assert.equal(attemptBudget(500, 150, 25, 25), 2);
  assert.throws(() => estimate({ rate: -1, residenceSeconds: 1, bytes: 1, days: 1, copies: 1 }), RangeError);
  assert.throws(() => attemptBudget(500, 0, 25, 25), RangeError);
  const report = { status: 'PASS', runtime: process.version,
    checks: ['unit-consistent capacity arithmetic', 'backlog drain requires surplus capacity', 'attempt count fits parent budget', 'invalid inputs rejected'],
    assumptions: { rate: 2000, residenceSeconds: 0.125, bytes: 1000, days: 7, copies: 3 },
    calculated: assumed, scope: 'Hypothetical arithmetic only; not measured traffic, capacity or latency', timestamp: new Date().toISOString() };
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify(report, null, 2));
  console.log(JSON.stringify(report, null, 2));
} catch (error) {
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify({ status: 'FAIL', reason: error.message }, null, 2));
  throw error;
}