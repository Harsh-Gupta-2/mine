import assert from 'node:assert/strict';
import { writeFile } from 'node:fs/promises';

export function initialState(tenant, generation = 0) {
  return { tenant, generation, request: 0, status: 'idle', jobs: [], progress: {} };
}

export function reduce(state, action) {
  if (action.type === 'tenant') return initialState(action.tenant, state.generation + 1);
  if (action.tenant !== state.tenant || action.generation !== state.generation) return state;
  if (action.type === 'loading') {
    if (action.request <= state.request) return state;
    return { ...state, request: action.request, status: 'loading' };
  }
  if (action.type === 'resolved' || action.type === 'failed') {
    if (action.request !== state.request) return state;
    return action.type === 'resolved'
      ? { ...state, status: 'ready', jobs: structuredClone(action.jobs) }
      : { ...state, status: 'error' };
  }
  if (action.type === 'progress') {
    if (!Number.isSafeInteger(action.version) || action.version < 0) return state;
    const previous = state.progress[action.jobId];
    if (previous && previous.version >= action.version) return state;
    return { ...state, progress: { ...state.progress, [action.jobId]: {
      version: action.version, processed: action.processed, status: action.status,
    } } };
  }
  return state;
}

export function createLoader(fetchJobs) {
  let state = initialState('tenant-a');
  let nextRequest = 0;
  let controller;
  let disposed = false;
  return {
    state: () => structuredClone(state),
    switchTenant(tenant) {
      controller?.abort();
      state = reduce(state, { type: 'tenant', tenant });
    },
    async load() {
      if (disposed) throw new Error('loader disposed');
      controller?.abort();
      controller = new AbortController();
      const identity = { tenant: state.tenant, generation: state.generation, request: ++nextRequest };
      state = reduce(state, { type: 'loading', ...identity });
      try {
        const jobs = await fetchJobs(identity.tenant, controller.signal);
        if (!disposed) state = reduce(state, { type: 'resolved', ...identity, jobs });
      } catch (error) {
        if (!disposed && error.name !== 'AbortError') state = reduce(state, { type: 'failed', ...identity });
      }
    },
    dispose() { disposed = true; controller?.abort(); },
  };
}

export async function runChecks() {
  const checks = [];
  let state = initialState('tenant-a');
  state = reduce(state, { type: 'loading', tenant: 'tenant-a', generation: 0, request: 1 });
  state = reduce(state, { type: 'loading', tenant: 'tenant-a', generation: 0, request: 2 });
  const before = state;
  assert.equal(reduce(state, { type: 'resolved', tenant: 'tenant-a', generation: 0, request: 1, jobs: ['old'] }), before);
  checks.push('older request cannot overwrite current request');
  state = reduce(state, { type: 'tenant', tenant: 'tenant-b' });
  assert.equal(reduce(state, { type: 'resolved', tenant: 'tenant-a', generation: 0, request: 2, jobs: ['private'] }), state);
  state = reduce(state, { type: 'tenant', tenant: 'tenant-a' });
  assert.equal(reduce(state, { type: 'resolved', tenant: 'tenant-a', generation: 0, request: 2, jobs: ['old session'] }), state);
  checks.push('tenant switch and switch-back invalidate earlier generation');
  const progress = { type: 'progress', tenant: 'tenant-a', generation: 2, jobId: 'job-1', version: 4, processed: 40, status: 'RUNNING' };
  state = reduce(state, progress);
  assert.equal(reduce(state, progress), state);
  assert.equal(reduce(state, { ...progress, version: 3 }), state);
  assert.equal(reduce(state, { ...progress, version: NaN }), state);
  assert.equal(state.progress['job-1'].processed, 40);
  checks.push('duplicate, older and invalid progress versions cannot regress snapshot');
  const pending = [];
  const loader = createLoader((tenant, signal) => new Promise(resolve => pending.push({ tenant, signal, resolve })));
  const first = loader.load();
  loader.switchTenant('tenant-b');
  const second = loader.load();
  assert.ok(pending[0].signal.aborted);
  pending[1].resolve(['new tenant']);
  await second;
  pending[0].resolve(['old tenant']);
  await first;
  assert.deepEqual(loader.state().jobs, ['new tenant']);
  checks.push('out-of-order completion remains safe even when transport ignores abort');
  const third = loader.load();
  loader.dispose();
  pending[2].resolve(['after disposal']);
  await third;
  assert.deepEqual(loader.state().jobs, ['new tenant']);
  checks.push('disposed loader ignores completion');
  return checks;
}

try {
  const checks = await runChecks();
  const report = { status: 'PASS', runtime: process.version, checks, scope: 'Pure view-state model, not React, DOM, HTTP or SSE integration', timestamp: new Date().toISOString() };
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify(report, null, 2));
  console.log(JSON.stringify(report, null, 2));
} catch (error) {
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify({ status: 'FAIL', reason: error.message }, null, 2));
  throw error;
}