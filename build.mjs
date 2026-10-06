import { readFile, writeFile, mkdir, access } from 'node:fs/promises';
import { fileURLToPath, pathToFileURL } from 'node:url';
import path from 'node:path';
import { spawnSync } from 'node:child_process';
import { renderDiagrams } from './render-mermaid.mjs';

export const root = path.dirname(fileURLToPath(import.meta.url));
export const escapeHtml = value => String(value).replace(/[&<>"']/g, character => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[character]);
export const slug = value => value.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/^-|-$/g, '');
export const anchor = chapter => `ch${chapter.id}-${chapter.slug}`;
const exists = async filename => access(filename).then(() => true, () => false);

export function inline(source) {
  const protectedText = [];
  const protect = html => `\u0000${protectedText.push(html) - 1}\u0000`;
  let text = source.replace(/`([^`]+)`/g, (_, code) => protect(`<code>${escapeHtml(code)}</code>`));
  text = text.replace(/\[([^\]]+)\]\(([^\s)]+)\)/g, (_, label, destination) => {
    const target = destination.replace(/^\d\d-[^#]+\.md#/, '#');
    if (!/^(#|https:\/\/)/.test(target)) throw new Error(`Unsupported link destination: ${destination}`);
    return protect(`<a href="${escapeHtml(target)}"${target.startsWith('https:') ? ' rel="noreferrer"' : ''}>${escapeHtml(label)}</a>`);
  });
  text = escapeHtml(text).replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>');
  return text.replace(/\u0000(\d+)\u0000/g, (_, index) => protectedText[Number(index)]);
}

export function renderMarkdown(source, chapterId, diagramImages = new Map()) {
  const lines = source.replace(/\r\n/g, '\n').split('\n');
  const output = [];
  const diagrams = [];
  const headings = new Map();
  let cursor = 0;
  while (cursor < lines.length) {
    const line = lines[cursor];
    if (!line.trim()) { cursor++; continue; }
    if (/^<a id="[a-z0-9-]+"><\/a>$/.test(line)) { output.push(line); cursor++; continue; }
    const fence = line.match(/^```([\w-]*)$/);
    if (fence) {
      const body = [];
      cursor++;
      while (cursor < lines.length && lines[cursor] !== '```') body.push(lines[cursor++]);
      if (cursor === lines.length) throw new Error('Unclosed code fence');
      cursor++;
      const language = fence[1] || 'text';
      if (language === 'mermaid') {
        diagrams.push(body.join('\n'));
        const diagramId = `diagram-${chapterId}-${diagrams.length}`;
        const image = diagramImages.get(diagramId);
        if (image) {
          const minimumWidth = Math.round(image.width * 0.85);
          output.push(`<figure class="mermaid-figure" data-diagram="${diagramId}" data-width="${image.width}" data-height="${image.height}"><figcaption><span>Diagram ${chapterId}.${diagrams.length}</span><button class="diagram-open" type="button" title="Expand diagram" aria-label="Expand diagram ${chapterId}.${diagrams.length}">Expand</button></figcaption><div class="diagram-viewport" tabindex="0" role="region" aria-label="Diagram ${chapterId}.${diagrams.length}"><div class="diagram-canvas" style="--diagram-min-width:${minimumWidth}px;--diagram-natural-width:${Math.round(image.width)}px">${image.svg}</div></div><details class="diagram-details"><summary>Mermaid source</summary><div class="code-block"><figcaption>Mermaid<button class="copy-button" type="button" aria-label="Copy Mermaid source">Copy</button></figcaption><pre><code class="language-mermaid">${escapeHtml(body.join('\n'))}</code></pre></div></details></figure>`);
          continue;
        }
      }
      output.push(`<figure class="code-block${language === 'mermaid' ? ' diagram-source' : ''}"><figcaption>${language === 'mermaid' ? 'Mermaid source / renderer unavailable in no-download edition' : escapeHtml(language)}<button class="copy-button" type="button" aria-label="Copy ${escapeHtml(language)} source">Copy</button></figcaption><pre><code class="language-${language}">${escapeHtml(body.join('\n'))}</code></pre></figure>`);
      continue;
    }
    const heading = line.match(/^(#{1,6}) (.+)$/);
    if (heading) {
      const base = `ch${chapterId}-${slug(heading[2])}`;
      const occurrence = (headings.get(base) || 0) + 1;
      headings.set(base, occurrence);
      const id = occurrence > 1 ? `${base}-${occurrence}` : base;
      const level = heading[1].length;
      output.push(`<h${level} id="${id}">${inline(heading[2])}</h${level}>`);
      cursor++;
      continue;
    }
    if (line.startsWith('>')) {
      const quote = [];
      while (cursor < lines.length && lines[cursor].startsWith('>')) quote.push(lines[cursor++].replace(/^> ?/, ''));
      const typeMatch = quote[0].match(/^\[!(MECHANISM|TRAP|INTERVIEW|PRODUCTION|DECISION)\]$/);
      const type = typeMatch ? typeMatch[1].toLowerCase() : 'note';
      if (typeMatch) quote.shift();
      output.push(`<aside class="callout ${type}"><p class="callout-label"><span aria-hidden="true">${({mechanism:'[M]',trap:'[!]',interview:'[?]',production:'[P]',decision:'[D]'})[type] || '[i]'}</span> ${type}</p><p>${inline(quote.join(' '))}</p></aside>`);
      continue;
    }
    if (line.startsWith('|') && /^\|[\s:|-]+\|$/.test(lines[cursor + 1] || '')) {
      const cells = row => row.replace(/^\||\|$/g, '').split('|').map(cell => cell.trim());
      const headers = cells(line);
      cursor += 2;
      const rows = [];
      while (cursor < lines.length && lines[cursor].startsWith('|')) {
        const values = cells(lines[cursor++]);
        if (values.length !== headers.length) throw new Error(`Malformed table in chapter ${chapterId}`);
        rows.push(`<tr>${values.map(value => `<td>${inline(value)}</td>`).join('')}</tr>`);
      }
      output.push(`<div class="table-scroll" tabindex="0" role="region" aria-label="Reference table"><table><thead><tr>${headers.map(value => `<th scope="col">${inline(value)}</th>`).join('')}</tr></thead><tbody>${rows.join('')}</tbody></table></div>`);
      continue;
    }
    if (/^- /.test(line)) {
      const items = [];
      while (cursor < lines.length && /^- /.test(lines[cursor])) items.push(`<li>${inline(lines[cursor++].slice(2))}</li>`);
      output.push(`<ul>${items.join('')}</ul>`);
      continue;
    }
    const paragraph = [line];
    cursor++;
    while (cursor < lines.length && lines[cursor].trim() && !/^(#|>|```|<a |\||- )/.test(lines[cursor])) paragraph.push(lines[cursor++]);
    output.push(`<p>${inline(paragraph.join(' '))}</p>`);
  }
  return { html: output.join('\n'), diagrams };
}

export function validateHtml(html) {
  const identifiers = [...html.matchAll(/\sid="([^"]+)"/g)].map(match => match[1]);
  const seen = new Set();
  for (const identifier of identifiers) {
    if (seen.has(identifier)) throw new Error(`Duplicate anchor: ${identifier}`);
    seen.add(identifier);
  }
  const references = [...html.matchAll(/href="#([^"]+)"/g)].map(match => match[1]);
  const broken = references.filter(reference => !seen.has(reference));
  if (broken.length) throw new Error(`Missing anchors: ${broken.join(', ')}`);
  if (/<(?:script|link|img)[^>]+(?:src|href)="https?:/i.test(html)) throw new Error('Remote reading dependency found');
  return { anchors: identifiers.length, internalLinks: references.length, brokenLinks: broken.length };
}

export function globalConceptIndex(catalog) {
  const terms = new Map();
  for (const chapter of catalog.chapters) for (const term of chapter.terms) {
    const key = term.toLocaleLowerCase('en');
    if (!terms.has(key)) terms.set(key, { term, owners: [] });
    terms.get(key).owners.push({ chapter, term });
  }
  return '| Concept and explanation | Owning chapter |\n|---|---|\n' + [...terms.values()]
    .sort((left, right) => left.term.localeCompare(right.term))
    .map(entry => `| ${entry.owners.map(({ chapter, term }) => `[${term}](#${chapter.termAnchors?.[term] || anchor(chapter)})`).join(' / ')} | ${entry.owners.map(({ chapter }) => `[${chapter.id} ${chapter.title}](${chapter.file}#${anchor(chapter)})`).join(' / ')} |`).join('\n');
}

export async function build({ pdf = true } = {}) {
  const catalog = JSON.parse(await readFile(path.join(root, 'catalog.json'), 'utf8'));
  if (catalog.chapters.length !== 28 || new Set(catalog.chapters.map(chapter => chapter.id)).size !== 28) throw new Error('Expected 28 unique chapters');
  if (new Set(catalog.readingOrder).size !== 28 || catalog.readingOrder.some(id => !catalog.chapters.some(chapter => chapter.id === id))) throw new Error('Invalid reading order');
  const ordered = catalog.readingOrder.map(id => catalog.chapters.find(chapter => chapter.id === id));
  const completed = [];
  const sources = new Map();
  for (const chapter of ordered) {
    const sourcePath = path.join(root, 'src', chapter.file);
    if (await exists(sourcePath)) {
      completed.push(chapter.id);
      const source = await readFile(sourcePath, 'utf8');
      sources.set(chapter.id, chapter.id === '20' ? source.replace('{{GLOBAL_CONCEPT_INDEX}}', globalConceptIndex(catalog)) : source);
    }
  }
  const relations = new Map(ordered.map(chapter => [chapter.id, new Set(chapter.id === '00' ? ordered.filter(item => item.id !== '00').map(item => item.id) : ['00'])]));
  for (const [chapterId, source] of sources) {
    for (const match of source.matchAll(/\]\((\d\d)-[^#)]+\.md#(ch\d\d-[^)]+)\)/g)) {
      const target = catalog.chapters.find(chapter => chapter.id === match[1]);
      if (!target || anchor(target) !== match[2]) throw new Error(`Unregistered source link: ${match[0]}`);
      if (target.id !== chapterId) { relations.get(chapterId).add(target.id); relations.get(target.id).add(chapterId); }
    }
  }
  const chapterLink = (chapter, destination = anchor(chapter)) => `<a href="#${escapeHtml(destination)}"><span class="chapter-number">${chapter.id}</span> ${escapeHtml(chapter.title)}</a>`;
  const navigation = catalog.parts.map((part, partId) => `<details open><summary>${escapeHtml(part)}</summary><ol>${ordered.filter(chapter => chapter.part === partId).map(chapter => `<li>${chapterLink(chapter)}<small>${completed.includes(chapter.id) ? 'Available' : 'Planned'}</small></li>`).join('')}</ol></details>`).join('');
  const directory = `<section id="guide-directory" class="directory"><p class="eyebrow">Reading order</p><h2>Chapter Directory</h2><p>${completed.length} of 28 chapters available. Planned destinations reserve stable links; they contain no completed explanations.</p><ol>${ordered.map(chapter => `<li>${chapterLink(chapter)}<span class="status ${completed.includes(chapter.id) ? 'ready' : ''}">${completed.includes(chapter.id) ? 'Available' : 'Planned'}</span></li>`).join('')}</ol></section>`;
  const fragments = [];
  const diagramInputs = [];
  for (const chapter of ordered.filter(item => sources.has(item.id))) {
    let index = 0;
    for (const match of sources.get(chapter.id).matchAll(/```mermaid\r?\n([\s\S]*?)\r?\n```/g)) {
      index++;
      diagramInputs.push({ id: `diagram-${chapter.id}-${index}`, label: `${chapter.title}, diagram ${index}`, source: match[1] });
    }
  }
  const diagramImages = await renderDiagrams(diagramInputs);
  let diagramCount = 0;
  for (const chapter of ordered) {
    const related = [...relations.get(chapter.id)].map(id => catalog.chapters.find(item => item.id === id));
    const relatedHtml = `<nav class="related" aria-label="Related chapters for ${chapter.id}"><strong>Related chapters / reciprocal links</strong><ul>${related.map(item => `<li>${chapterLink(item)}</li>`).join('')}</ul></nav>`;
    if (sources.has(chapter.id)) {
      const rendered = renderMarkdown(sources.get(chapter.id), chapter.id, diagramImages);
      diagramCount += rendered.diagrams.length;
      const start = rendered.html.indexOf(`<a id="ch${chapter.id}-cheat-sheet"`);
      const content = start === -1 ? rendered.html + relatedHtml : rendered.html.slice(0, start) + relatedHtml + rendered.html.slice(start);
      fragments.push(`<article class="chapter" data-chapter="${chapter.id}">${content}</article>`);
    } else {
      fragments.push(`<section class="planned-chapter" data-chapter="${chapter.id}" id="${anchor(chapter)}"><p class="eyebrow">${chapter.id} / Planned, not written</p><h2>${escapeHtml(chapter.title)}</h2><p class="coverage">Planned coverage: ${chapter.terms.map(escapeHtml).join('; ')}.</p>${relatedHtml}</section>`);
    }
  }
  const termRows = new Map();
  for (const chapter of ordered) for (const term of chapter.terms) {
    const key = term.toLocaleLowerCase('en');
    if (!termRows.has(key)) termRows.set(key, { term, chapters: [] });
    termRows.get(key).chapters.push(chapter);
  }
  const conceptIndex = `<section id="guide-concept-index" class="concept-index"><p class="eyebrow">Find a concept</p><h2>Coverage Index</h2><p>Chapters ${completed.join(', ')} are available in this edition. ${completed.includes('20') ? 'The complete section-level global index is also available in Chapter 20. Coverage does not close outstanding verification or editorial gaps.' : 'Other links identify future owning chapters. The final section-level global index will be reviewed in Chapter 20.'}</p><dl>${[...termRows.values()].sort((left,right) => left.term.localeCompare(right.term)).map(entry => `<div><dt>${escapeHtml(entry.term)}</dt><dd>${entry.chapters.map(chapter => `${chapterLink(chapter, completed.includes(chapter.id) ? chapter.termAnchors?.[entry.term] || anchor(chapter) : anchor(chapter))} <small>${completed.includes(chapter.id) ? 'Available' : 'Planned'}</small>`).join(' / ')}</dd></div>`).join('')}</dl></section>`;
  const css = await readFile(path.join(root, 'reader.css'), 'utf8');
  const client = await readFile(path.join(root, 'reader.js'), 'utf8');
  const html = `<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><meta name="color-scheme" content="light dark"><title>The Java Full-Stack Guide</title><style>${css}</style></head><body>
<a class="skip-link" href="#main">Skip to guide</a>
<header class="toolbar"><a class="brand" href="#cover">JAVA / FULL STACK</a><label class="search-label" for="guide-search">Search<input id="guide-search" type="search" placeholder="Find a concept" autocomplete="off"></label><label class="theme-label"><input id="theme-toggle" type="checkbox"> Dark</label><button id="print-button" type="button">Print</button></header>
<aside class="sidebar"><details class="navigation-shell" open><summary>Contents</summary><nav aria-label="Chapter navigation">${navigation}<a class="index-link" href="#guide-concept-index">Concept index</a></nav></details></aside>
<main id="main"><section id="search-results" aria-live="polite" hidden></section>
<section class="cover" id="cover"><p class="eyebrow">Engineering reference / Chapters ${completed.join(', ')}</p><h1>The Java<br>Full-Stack Guide</h1><p class="cover-subtitle">One system. Every boundary.</p><p class="cover-description">Java, Spring, data, identity, distributed systems and delivery, connected through the fictional IntegrationHub.</p><div class="cover-facts"><div><strong>${String(completed.length).padStart(2, '0')} / 28</strong><span>Chapters available</span></div><div><strong>Java 21+</strong><span>Example baseline</span></div><div><strong>00</strong><span>Start with the system</span></div></div><div class="system-strip" aria-label="Request path"><span>Browser</span><b aria-hidden="true">/</b><span>Edge</span><b aria-hidden="true">/</b><span>Service</span><b aria-hidden="true">/</b><span>Data</span><b aria-hidden="true">/</b><span>Events</span><b aria-hidden="true">/</b><span>UI</span></div><p class="edition-note">Offline edition. Diagrams are embedded as vector graphics. Java lab execution status is stated in each chapter. Code is copyable; syntax highlighting is unavailable. IntegrationHub is fictional; no interview outcome is guaranteed.</p><a class="start-link" href="#ch00-master-map">Read Chapter 00</a></section>
${directory}${fragments.join('\n')}${conceptIndex}<footer class="endnote">The Java Full-Stack Guide / IntegrationHub is fictional / Chapters ${completed.join(', ')}, 2026-10-05</footer></main><div id="copy-status" role="status" aria-live="polite"></div><script>${client}</script></body></html>`;
  const checks = validateHtml(html);
  const dist = path.join(root, 'dist');
  await mkdir(dist, { recursive: true });
  await writeFile(path.join(dist, 'java-fs-guide.html'), html);
  let pdfResult = 'not executed';
  if (pdf) {
    const candidates = [process.env.GUIDE_CHROME, 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe', 'C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe'].filter(Boolean);
    let browser;
    for (const candidate of candidates) if (await exists(candidate)) { browser = candidate; break; }
    if (browser) {
      const profile = path.join(root, '.build', `chrome-${Date.now()}`);
      await mkdir(profile, { recursive: true });
      const pdfPath = path.join(dist, 'java-fs-guide.pdf');
      const result = spawnSync(browser, ['--headless=new', '--disable-gpu', '--no-first-run', '--no-default-browser-check', '--disable-extensions', '--disable-background-networking', `--user-data-dir=${profile}`, '--no-pdf-header-footer', `--print-to-pdf=${pdfPath}`, pathToFileURL(path.join(dist, 'java-fs-guide.html')).href], { encoding: 'utf8', timeout: 90000 });
      await writeFile(path.join(dist, 'chrome-print.log'), `${result.stdout || ''}\n${result.stderr || ''}\nExit: ${result.status}\n${result.error || ''}`);
      if (result.status === 0 && await exists(pdfPath)) {
        const bytes = await readFile(pdfPath);
        if (bytes.subarray(0, 5).toString() !== '%PDF-') throw new Error('Invalid PDF header');
        pdfResult = `generated, ${bytes.length} bytes`;
      } else pdfResult = `failed; see chrome-print.log (${result.error?.message || result.status})`;
    } else pdfResult = 'not executed: installed Chrome/Edge not found';
  }
  const chapterMetrics = Object.fromEntries([...sources].map(([chapterId, source]) => [chapterId, { words: source.split(/\s+/).filter(Boolean).length, proseWords: source.replace(/```[\s\S]*?```/g, '').split(/\s+/).filter(Boolean).length, verifyItems: (source.match(/\[VERIFY:/g) || []).length, diagrams: (source.match(/```mermaid/g) || []).length }]));
  const report = { builtAt: new Date().toISOString(), runtime: process.version, completed, planned: 28 - completed.length, chapterMetrics, ...checks, diagramSources: diagramCount, renderedDiagrams: diagramImages.size, diagramRenderer: 'Mermaid 11.12.0 browser bundle in local headless Chrome, network blocked', registeredTerms: termRows.size, pdf: pdfResult, degradation: ['Strict subset Markdown renderer retained; Pandoc and Mermaid CLI not installed.', 'Syntax highlighting unavailable.', 'Only diagram dependencies authorized for download; Java/Maven labs remain subject to existing tool limits.'] };
  await writeFile(path.join(dist, 'build-report.json'), JSON.stringify(report, null, 2) + '\n');
  console.log(JSON.stringify(report, null, 2));
  return report;
}

if (process.argv[1] && path.resolve(process.argv[1]) === fileURLToPath(import.meta.url)) {
  build().catch(error => { console.error(error); process.exitCode = 1; });
}