import { readFile, writeFile, mkdir, access } from 'node:fs/promises';
import { createHash } from 'node:crypto';
import { spawn } from 'node:child_process';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const root = path.dirname(fileURLToPath(import.meta.url));
const config = {
  startOnLoad: false,
  securityLevel: 'strict',
  theme: 'base',
  fontFamily: 'Segoe UI, sans-serif',
  fontSize: 16,
  themeVariables: {
    primaryColor: '#dcfce7', primaryTextColor: '#166534', primaryBorderColor: '#166534',
    secondaryColor: '#e0f2fe', tertiaryColor: '#fef3c7', lineColor: '#526159',
    actorBkg: '#dcfce7', actorBorder: '#166534', actorTextColor: '#166534',
    signalColor: '#334155', signalTextColor: '#202824', labelBoxBkgColor: '#e2e8f0',
    labelBoxBorderColor: '#64748b', labelTextColor: '#334155',
    noteBkgColor: '#fef3c7', noteTextColor: '#713f12', noteBorderColor: '#ca8a04',
    edgeLabelBackground: '#ffffff', background: '#ffffff',
  },
  flowchart: { htmlLabels: false, curve: 'linear', padding: 18, nodeSpacing: 35, rankSpacing: 50, useMaxWidth: true },
  sequence: { useMaxWidth: true, wrap: true, width: 150, actorMargin: 30, messageMargin: 35, noteMargin: 15, mirrorActors: false },
};

async function browserSession() {
  const candidates = [process.env.GUIDE_CHROME, 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe', 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe'].filter(Boolean);
  let executable;
  for (const candidate of candidates) if (await access(candidate).then(() => true, () => false)) { executable = candidate; break; }
  if (!executable) throw new Error('An installed Chrome or Edge is required for Mermaid rendering');
  const profile = path.join(root, '.build', `mermaid-chrome-${Date.now()}`);
  await mkdir(profile, { recursive: true });
  const child = spawn(executable, ['--headless=new', '--disable-gpu', '--no-first-run', '--no-default-browser-check', '--disable-extensions', '--disable-background-networking', '--remote-debugging-port=0', `--user-data-dir=${profile}`, 'about:blank'], { stdio: ['ignore', 'ignore', 'pipe'] });
  let socket;
  try {
    const endpoint = await new Promise((resolve, reject) => {
      const timer = setTimeout(() => reject(new Error('Chrome did not expose a debugging endpoint')), 20000);
      let buffer = '';
      child.stderr.on('data', data => {
        buffer += data.toString();
        const match = buffer.match(/DevTools listening on (ws:\/\/[^\s]+)/);
        if (match) { clearTimeout(timer); resolve(match[1]); }
      });
      child.once('error', error => { clearTimeout(timer); reject(error); });
      child.once('exit', code => { clearTimeout(timer); reject(new Error(`Chrome exited during setup: ${code}`)); });
    });
    socket = new WebSocket(endpoint);
    await new Promise((resolve, reject) => { socket.addEventListener('open', resolve, { once: true }); socket.addEventListener('error', reject, { once: true }); });
    const pending = new Map();
    let requestId = 0;
    socket.addEventListener('message', event => {
      const response = JSON.parse(event.data);
      const waiter = pending.get(response.id);
      if (!waiter) return;
      clearTimeout(waiter.timer);
      pending.delete(response.id);
      if (response.error) waiter.reject(new Error(JSON.stringify(response.error)));
      else waiter.resolve(response.result);
    });
    const send = (method, params = {}, sessionId) => new Promise((resolve, reject) => {
      const id = ++requestId;
      const timer = setTimeout(() => { pending.delete(id); reject(new Error(`Chrome command timed out: ${method}`)); }, 45000);
      pending.set(id, { resolve, reject, timer });
      socket.send(JSON.stringify({ id, method, params, ...(sessionId ? { sessionId } : {}) }));
    });
    const target = await send('Target.createTarget', { url: 'about:blank' });
    const { sessionId } = await send('Target.attachToTarget', { targetId: target.targetId, flatten: true });
    const evaluate = async expression => {
      const response = await send('Runtime.evaluate', { expression, awaitPromise: true, returnByValue: true }, sessionId);
      if (response.exceptionDetails) throw new Error(response.exceptionDetails.exception?.description || response.exceptionDetails.text);
      return response.result.value;
    };
    await send('Network.enable', {}, sessionId);
    await send('Network.setBlockedURLs', { urls: ['http://*', 'https://*'] }, sessionId);
    await send('Emulation.setDeviceMetricsOverride', { width: 1600, height: 1200, deviceScaleFactor: 1, mobile: false }, sessionId);
    return { evaluate, close: async () => { await send('Browser.close').catch(() => {}); socket.close(); child.kill(); } };
  } catch (error) {
    socket?.close();
    child.kill();
    throw error;
  }
}

export async function renderDiagrams(diagrams) {
  if (!diagrams.length) return new Map();
  const bundlePath = path.join(root, 'vendor', 'mermaid', 'mermaid.min.js');
  const bundle = await readFile(bundlePath, 'utf8');
  const bundleHash = createHash('sha256').update(bundle).digest('hex');
  const cache = path.join(root, '.build', 'diagram-cache');
  await mkdir(cache, { recursive: true });
  const rendered = new Map();
  let browser;
  try {
    for (const diagram of diagrams) {
      const fingerprint = createHash('sha256').update(JSON.stringify({ renderer: 3, config, bundleHash, ...diagram })).digest('hex');
      const cacheFile = path.join(cache, `${fingerprint}.json`);
      const saved = await readFile(cacheFile, 'utf8').then(JSON.parse, () => null);
      if (saved) { rendered.set(diagram.id, saved); continue; }
      if (!browser) {
        browser = await browserSession();
        await browser.evaluate(bundle + '\n;typeof mermaid;');
        await browser.evaluate(`mermaid.initialize(${JSON.stringify(config)}); document.body.style.cssText='margin:0;background:white;font-family:Segoe UI,sans-serif'; true;`);
      }
      const result = await browser.evaluate(`(async () => {
        const source = ${JSON.stringify(diagram.source)};
        const prefix = ${JSON.stringify(diagram.id)};
        const result = await mermaid.render(prefix, source);
        const host = document.createElement('div');
        host.innerHTML = result.svg;
        document.body.append(host);
        const svg = host.querySelector('svg');
        if (!svg || svg.querySelector('.error-icon')) throw new Error('Mermaid produced no valid SVG');
        const viewBox = svg.viewBox.baseVal;
        if (!(viewBox.width > 0 && viewBox.height > 0)) throw new Error('Mermaid produced an empty diagram');
        const dimensions = { width: viewBox.width, height: viewBox.height };
        if (/^\s*erDiagram/.test(source)) {
          for (const background of svg.querySelectorAll('rect.background')) background.style.fill = '#ffffff';
        }
        const palette = { CLIENT: ['#e0f2fe','#075985'], EDGE: ['#ffedd5','#9a3412'], SERVICE: ['#dcfce7','#166534'], DATA: ['#fef3c7','#854d0e'], INFRA: ['#e2e8f0','#334155'], SECURITY: ['#ffe4e6','#9f1239'] };
        const roles = new Map([...source.matchAll(/participant\\s+(\\w+)\\s+as\\s+(CLIENT|EDGE|SERVICE|DATA|INFRA|SECURITY)/g)].map(match => [match[1], match[2]]));
        for (const actor of svg.querySelectorAll('rect.actor')) {
          const colors = palette[roles.get(actor.getAttribute('name'))];
          if (!colors) continue;
          actor.style.fill = colors[0];
          actor.style.stroke = colors[1];
          const bounds = actor.getBBox();
          for (const label of svg.querySelectorAll('text.actor')) {
            const center = Number(label.getAttribute('x'));
            const vertical = Number(label.getAttribute('y'));
            if (center >= bounds.x && center <= bounds.x + bounds.width && vertical >= bounds.y && vertical <= bounds.y + bounds.height) label.style.fill = colors[1];
          }
        }
        const identifiers = new Map([...host.querySelectorAll('[id]')].map(element => [element.id, prefix + '-' + element.id]));
        for (const element of host.querySelectorAll('*')) {
          for (const attribute of [...element.attributes]) {
            if (attribute.name === 'id') { element.id = identifiers.get(attribute.value); continue; }
            let value = attribute.value.replace(/url\\(["']?#([^\\)"']+)["']?\\)/g, (whole, name) => identifiers.has(name) ? 'url(#' + identifiers.get(name) + ')' : whole);
            if ((attribute.name === 'href' || attribute.name === 'xlink:href') && value.startsWith('#') && identifiers.has(value.slice(1))) value = '#' + identifiers.get(value.slice(1));
            if (attribute.name === 'aria-labelledby' || attribute.name === 'aria-describedby') value = value.split(' ').map(name => identifiers.get(name) || name).join(' ');
            element.setAttribute(attribute.name, value);
          }
          if (element.tagName.toLowerCase() === 'style') element.textContent = element.textContent.replace(/#([a-zA-Z_][\\w:.-]*)/g, (whole, name) => identifiers.has(name) ? '#' + identifiers.get(name) : whole);
        }
        svg.setAttribute('role', 'img');
        svg.setAttribute('aria-label', ${JSON.stringify(diagram.label)});
        svg.setAttribute('preserveAspectRatio', 'xMidYMid meet');
        svg.style.maxWidth = 'none';
        svg.style.background = '#ffffff';
        const output = { svg: svg.outerHTML, ...dimensions };
        host.remove();
        return output;
      })()`);
      await writeFile(cacheFile, JSON.stringify(result));
      rendered.set(diagram.id, result);
      console.log(`Rendered ${diagram.id} (${Math.round(result.width)} x ${Math.round(result.height)})`);
    }
  } finally {
    await browser?.close();
  }
  return rendered;
}

if (process.argv.includes('--smoke')) {
  const result = await renderDiagrams([{ id: 'diagram-smoke', label: 'Mermaid render smoke test', source: 'flowchart LR\n  Browser[Browser] --> Service[Connector service]\n  Service --> Store[(PostgreSQL)]' }]);
  await mkdir(path.join(root, '.build'), { recursive: true });
  await writeFile(path.join(root, '.build', 'mermaid-smoke.svg'), result.get('diagram-smoke').svg);
  console.log('Mermaid smoke test PASS');
}