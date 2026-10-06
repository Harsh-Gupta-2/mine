import assert from 'node:assert/strict';
import { writeFile } from 'node:fs/promises';

export function summarize(good, total, target) {
  if (!Number.isSafeInteger(good) || !Number.isSafeInteger(total) || good < 0 || total < good
      || !Number.isFinite(target) || target <= 0 || target >= 1) throw new RangeError('invalid SLI inputs');
  if (total === 0) return { availability: null, burnRate: null, remainingBadEvents: 0 };
  const bad = total - good;
  const allowance = total * (1 - target);
  return { availability: good / total, burnRate: bad / allowance, remainingBadEvents: allowance - bad };
}

export function shouldPage(shortWindow, longWindow, threshold) {
  if (!Number.isFinite(threshold) || threshold <= 0) throw new RangeError('positive threshold required');
  return shortWindow.burnRate !== null && longWindow.burnRate !== null
    && shortWindow.burnRate >= threshold && longWindow.burnRate >= threshold;
}

export function classicHistogramSeries(labelCardinalities, bucketSeries) {
  if (!Number.isSafeInteger(bucketSeries) || bucketSeries < 1
      || !labelCardinalities.every(value => Number.isSafeInteger(value) && value > 0)) throw new RangeError('invalid series dimensions');
  const result = labelCardinalities.reduce((total, value) => total * value, 1) * (bucketSeries + 2);
  if (!Number.isSafeInteger(result)) throw new RangeError('series estimate overflow');
  return result;
}

try {
  const summary = summarize(998000, 1000000, 0.999);
  assert.ok(Math.abs(summary.burnRate - 2) < 1e-10);
  assert.ok(Math.abs(summary.remainingBadEvents + 1000) < 1e-8);
  assert.equal(summarize(0, 0, 0.999).availability, null);
  assert.equal(shouldPage(summarize(0, 0, 0.999), summary, 1), false);
  assert.equal(shouldPage(summary, summarize(999900, 1000000, 0.999), 1), false);
  assert.equal(shouldPage(summary, summary, 1), true);
  assert.equal(classicHistogramSeries([10, 6, 5], 12), 4200);
  assert.throws(() => summarize(2, 1, 0.999), RangeError);
  assert.throws(() => classicHistogramSeries([0], 12), RangeError);
  const report = { status: 'PASS', runtime: process.version,
    checks: ['request error-budget arithmetic', 'no traffic is unknown rather than healthy', 'both burn windows required', 'classic histogram cardinality estimate', 'invalid inputs rejected'],
    scope: 'Synthetic arithmetic, not production SLIs, PromQL evaluation or an alerting system', timestamp: new Date().toISOString() };
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify(report, null, 2));
  console.log(JSON.stringify(report, null, 2));
} catch (error) {
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify({ status: 'FAIL', reason: error.message }, null, 2));
  throw error;
}