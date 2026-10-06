import assert from 'node:assert/strict';
import { writeFile } from 'node:fs/promises';

const ALLOWED_DEPENDENCIES = { domain: [], application: ['domain'], adapter: ['application', 'domain'], infrastructure: ['adapter', 'application', 'domain'] };

export function illegalDependencies(modules) {
  const violations = [];
  for (const [name, module] of Object.entries(modules)) {
    const allowed = ALLOWED_DEPENDENCIES[module.layer];
    if (!allowed) throw new RangeError(`unknown layer ${module.layer}`);
    for (const target of module.dependsOn) {
      const dependency = modules[target];
      if (!dependency) throw new RangeError(`unknown module ${target}`);
      if (dependency.layer !== module.layer && !allowed.includes(dependency.layer)) violations.push(`${name} -> ${target}`);
    }
  }
  return violations;
}

export function contextCycles(contextMap) {
  const visiting = new Set();
  const settled = new Set();
  const cycles = [];
  const walk = (node, path) => {
    if (visiting.has(node)) { cycles.push([...path.slice(path.indexOf(node)), node].join(' -> ')); return; }
    if (settled.has(node)) return;
    visiting.add(node);
    for (const next of contextMap[node] ?? []) walk(next, [...path, node]);
    visiting.delete(node);
    settled.add(node);
  };
  for (const node of Object.keys(contextMap)) walk(node, []);
  return cycles;
}

export class ConnectorCatalog {
  #tenant;
  #limit;
  #connectors = new Set();
  #events = [];
  constructor(tenant, limit) {
    if (!tenant || !Number.isSafeInteger(limit) || limit <= 0) throw new RangeError('tenant and positive limit required');
    this.#tenant = tenant;
    this.#limit = limit;
  }
  add(name) {
    if (this.#connectors.has(name)) return false;
    if (this.#connectors.size >= this.#limit) throw new RangeError('aggregate invariant: connector limit reached');
    this.#connectors.add(name);
    this.#events.push({ type: 'ConnectorAdded', tenant: this.#tenant, name });
    return true;
  }
  get size() { return this.#connectors.size; }
  get events() { return this.#events.map(event => ({ ...event })); }
}

export const sameValue = (left, right) => left.currency === right.currency && left.amountMinor === right.amountMinor;
export const sameEntity = (left, right) => left.id === right.id;

try {
  const modules = {
    syncDomain: { layer: 'domain', dependsOn: [] },
    syncApplication: { layer: 'application', dependsOn: ['syncDomain'] },
    postgresAdapter: { layer: 'adapter', dependsOn: ['syncApplication', 'syncDomain'] },
    leakyDomain: { layer: 'domain', dependsOn: ['postgresAdapter'] }
  };
  assert.deepEqual(illegalDependencies(modules), ['leakyDomain -> postgresAdapter']);
  assert.deepEqual(illegalDependencies({ syncDomain: modules.syncDomain, syncApplication: modules.syncApplication }), []);

  assert.deepEqual(contextCycles({ identity: [], connectors: ['identity'], billing: ['connectors'] }), []);
  assert.equal(contextCycles({ connectors: ['billing'], billing: ['connectors'] }).length, 1);

  const catalog = new ConnectorCatalog('tenant-a', 2);
  assert.equal(catalog.add('salesforce'), true);
  assert.equal(catalog.add('salesforce'), false);
  assert.equal(catalog.events.length, 1);
  assert.equal(catalog.add('netsuite'), true);
  assert.throws(() => catalog.add('workday'), RangeError);
  assert.equal(catalog.size, 2);
  assert.equal(catalog.events.length, 2);

  assert.equal(sameValue({ currency: 'EUR', amountMinor: 500 }, { currency: 'EUR', amountMinor: 500 }), true);
  assert.equal(sameEntity({ id: 'job-1', status: 'RUNNING' }, { id: 'job-1', status: 'DONE' }), true);
  assert.equal(sameEntity({ id: 'job-1' }, { id: 'job-2' }), false);

  const report = { status: 'PASS', runtime: process.version,
    checks: ['inward dependency rule detects domain-to-adapter leak', 'context map cycle detected', 'aggregate invariant rejects command and records no event', 'duplicate command is idempotent without a second event', 'value equality and entity identity differ'],
    scope: 'Declared module/context graphs and one in-memory aggregate, not a Java package scan, Spring wiring or deployed architecture', timestamp: new Date().toISOString() };
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify(report, null, 2));
  console.log(JSON.stringify(report, null, 2));
} catch (error) {
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify({ status: 'FAIL', reason: error.message }, null, 2));
  throw error;
}
