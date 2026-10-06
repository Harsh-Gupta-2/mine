import assert from 'node:assert/strict';
import { writeFile } from 'node:fs/promises';

export function promote(current, artifact, approval) {
  if (!/^sha256:[a-f0-9]{64}$/.test(artifact.digest)) throw new Error('immutable digest required');
  if (artifact.testsPassed !== true || artifact.policyPassed !== true) throw new Error('required evidence missing');
  if (approval.digest !== artifact.digest || approval.generation !== current.generation) throw new Error('approval does not match current intent');
  if (!artifact.readableSchemas.includes(current.schema)) throw new Error('artifact cannot read current schema');
  return { ...current, digest: artifact.digest, generation: current.generation + 1 };
}

try {
  const current = { digest: 'sha256:' + 'a'.repeat(64), schema: 2, generation: 7 };
  const next = { digest: 'sha256:' + 'b'.repeat(64), readableSchemas: [1, 2], testsPassed: true, policyPassed: true };
  const approval = { digest: next.digest, generation: 7 };
  const updated = promote(current, next, approval);
  assert.equal(updated.generation, 8);
  assert.equal(updated.digest, next.digest);
  assert.equal(current.generation, 7);
  assert.throws(() => promote(updated, next, approval), /approval/);
  assert.throws(() => promote(current, { ...next, testsPassed: false }, approval), /evidence/);
  assert.throws(() => promote(current, { ...next, digest: 'service:latest' }, approval), /digest/);
  const previous = { ...next, digest: current.digest, readableSchemas: [1] };
  assert.throws(() => promote(updated, previous, { digest: previous.digest, generation: 8 }), /schema/);
  const report = { status: 'PASS', runtime: process.version,
    checks: ['matching reviewed intent promotes without mutating input', 'stale approval rejected', 'failed evidence or mutable tag rejected', 'schema-incompatible rollback rejected'],
    scope: 'Synthetic release-policy model; booleans and digest shapes are not actual attestations or images',
    jenkinsExecution: 'NOT EXECUTED', timestamp: new Date().toISOString() };
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify(report, null, 2));
  console.log(JSON.stringify(report, null, 2));
} catch (error) {
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify({ status: 'FAIL', reason: error.message }, null, 2));
  throw error;
}