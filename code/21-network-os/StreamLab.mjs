import assert from 'node:assert/strict';
import { writeFile } from 'node:fs/promises';
import { StringDecoder } from 'node:string_decoder';
import { Readable } from 'node:stream';
import { createInterface } from 'node:readline';
import { createServer } from 'node:http';
import { once } from 'node:events';

try {
  const bytes = Buffer.from('f09f9982', 'hex');
  const decoder = new StringDecoder('utf8');
  assert.equal(decoder.write(bytes.subarray(0, 2)) + decoder.end(bytes.subarray(2)), '\u{1f642}');
  assert.notEqual(bytes.subarray(0, 2).toString() + bytes.subarray(2).toString(), '\u{1f642}');
  const lines = [];
  const input = createInterface({ input: Readable.from([Buffer.from('first\nsec'), Buffer.from('ond\n')]), crlfDelay: Infinity });
  try { for await (const line of input) lines.push(line); } finally { input.close(); }
  assert.deepEqual(lines, ['first', 'second']);

  const server = createServer((request, response) => {
    response.setHeader('Content-Type', 'application/json');
    response.write('{"job');
    response.end('":"one"}');
  });
  try {
    server.listen(0, '127.0.0.1');
    await once(server, 'listening');
    const response = await fetch(`http://127.0.0.1:${server.address().port}/`, { signal: AbortSignal.timeout(5000) });
    assert.equal(response.status, 200);
    assert.deepEqual(await response.json(), { job: 'one' });
  } finally {
    server.closeAllConnections();
    if (server.listening) await new Promise((resolve, reject) => server.close(error => error ? reject(error) : resolve()));
  }
  const report = { status: 'PASS', runtime: process.version,
    checks: ['UTF-8 decoder preserves split character', 'line framing spans input chunks', 'loopback HTTP assembles split application writes'],
    scope: 'Standard-library stream and loopback HTTP checks, not packet capture, DNS, TLS, Java NIO or a network benchmark', timestamp: new Date().toISOString() };
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify(report, null, 2));
  console.log(JSON.stringify(report, null, 2));
} catch (error) {
  await writeFile(new URL('./execution.json', import.meta.url), JSON.stringify({ status: 'FAIL', reason: error.message }, null, 2));
  throw error;
}