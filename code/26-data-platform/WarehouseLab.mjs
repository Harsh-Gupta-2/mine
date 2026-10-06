import assert from 'node:assert/strict';
import { writeFile } from 'node:fs/promises';

export class DimensionTable {
  rows = [];
  #surrogate = 0;
  #appliedPositions = new Map();

  apply(change) {
    const { naturalKey, position, attributes, deleted = false, observedAt } = change;
    if (!naturalKey || !Number.isSafeInteger(position) || position <= 0) throw new RangeError('natural key and positive position required');
    const lastPosition = this.#appliedPositions.get(naturalKey);
    if (lastPosition !== undefined && position <= lastPosition) return 'ignored';
    this.#appliedPositions.set(naturalKey, position);
    const current = this.rows.find(row => row.naturalKey === naturalKey && row.validTo === null);
    if (current) {
      if (!deleted && JSON.stringify(current.attributes) === JSON.stringify(attributes)) return 'unchanged';
      current.validTo = observedAt;
    }
    if (deleted) return 'closed';
    this.rows.push({ surrogateKey: ++this.#surrogate, naturalKey, attributes, validFrom: observedAt, validTo: null, deleted: false });
    return current ? 'versioned' : 'inserted';
  }

  current(naturalKey) { return this.rows.find(row => row.naturalKey === naturalKey && row.validTo === null) ?? null; }
}

export class FactTable {
  #rows = new Map();
  add(grainKeys, measures) {
    const key = grainKeys.join('|');
    if (this.#rows.has(key)) return false;
    if (Object.values(measures).some(value => !Number.isSafeInteger(value))) throw new RangeError('integer measures required at this grain');
    this.#rows.set(key, measures);
    return true;
  }
  total(measure) { return [...this.#rows.values()].reduce((sum, row) => sum + row[measure], 0); }
  get count() { return this.#rows.size; }
}

try {
  const dimension = new DimensionTable();
  assert.equal(dimension.apply({ naturalKey: 'connector-1', position: 10, attributes: { tier: 'standard' }, observedAt: '2026-01-01' }), 'inserted');
  assert.equal(dimension.apply({ naturalKey: 'connector-1', position: 10, attributes: { tier: 'premium' }, observedAt: '2026-01-02' }), 'ignored');
  assert.equal(dimension.apply({ naturalKey: 'connector-1', position: 7, attributes: { tier: 'legacy' }, observedAt: '2026-01-03' }), 'ignored');
  assert.equal(dimension.current('connector-1').attributes.tier, 'standard');

  assert.equal(dimension.apply({ naturalKey: 'connector-1', position: 20, attributes: { tier: 'premium' }, observedAt: '2026-02-01' }), 'versioned');
  assert.equal(dimension.rows.filter(row => row.naturalKey === 'connector-1' && row.validTo === null).length, 1);
  assert.equal(dimension.rows[0].validTo, '2026-02-01');
  assert.notEqual(dimension.rows[0].surrogateKey, dimension.rows[1].surrogateKey);
  assert.equal(dimension.current('connector-1').attributes.tier, 'premium');

  assert.equal(dimension.apply({ naturalKey: 'connector-1', position: 30, deleted: true, observedAt: '2026-03-01' }), 'closed');
  assert.equal(dimension.current('connector-1'), null);
  assert.equal(dimension.rows.length, 2);

  const facts = new FactTable();
  assert.equal(facts.add(['2026-02-01', 'connector-1', 'tenant-a'], { recordsLoaded: 1200, failures: 3 }), true);
  assert.equal(facts.add(['2026-02-01', 'connector-1', 'tenant-a'], { recordsLoaded: 1200, failures: 3 }), false);
  assert.equal(facts.add(['2026-02-02', 'connector-1', 'tenant-a'], { recordsLoaded: 800, failures: 0 }), true);
  assert.equal(facts.count, 2);
  assert.equal(facts.total('recordsLoaded'), 2000);
  assert.throws(() => facts.add(['2026-02-03', 'connector-1', 'tenant-a'], { recordsLoaded: 10.5 }), RangeError);

  const report = { status: 'PASS', runtime: process.version,
    checks: ['replayed and out-of-order change positions ignored', 'attribute change opens a new version and closes exactly one open row', 'delete closes history without erasing prior versions', 'fact grain key rejects duplicate load', 'non-integer measure rejected at declared grain'],
    scope: 'In-memory dimension/fact model, not a warehouse engine, CDC connector, orchestration tool or query optimizer', timestamp: new Date().toISOString() };
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify(report, null, 2));
  console.log(JSON.stringify(report, null, 2));
} catch (error) {
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify({ status: 'FAIL', reason: error.message }, null, 2));
  throw error;
}
