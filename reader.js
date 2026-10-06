(() => {
  const search = document.getElementById('guide-search');
  const results = document.getElementById('search-results');
  const theme = document.getElementById('theme-toggle');
  let preferred = matchMedia('(prefers-color-scheme: dark)').matches;
  try { const saved = localStorage.getItem('java-guide-dark'); if (saved !== null) preferred = saved === 'true'; } catch {}
  theme.checked = preferred;
  document.documentElement.dataset.theme = preferred ? 'dark' : 'light';
  theme.addEventListener('change', () => {
    document.documentElement.dataset.theme = theme.checked ? 'dark' : 'light';
    try { localStorage.setItem('java-guide-dark', String(theme.checked)); } catch {}
  });
  document.getElementById('print-button').addEventListener('click', () => window.print());
  const searchable = [...document.querySelectorAll('main article, main .planned-chapter')].map(section => ({
    section,
    title: section.querySelector('h1,h2').textContent.replace(/^\d{2}\s*\/\s*/, ''),
    id: section.id || section.querySelector('[id]').id,
    text: section.textContent.replace(/\s+/g, ' ').trim(),
  }));
  search.addEventListener('input', () => {
    const query = search.value.trim().toLocaleLowerCase();
    results.replaceChildren();
    results.hidden = !query;
    if (!query) return;
    const matches = searchable.filter(entry => entry.text.toLocaleLowerCase().includes(query));
    const heading = document.createElement('h2');
    heading.textContent = `${matches.length} chapter${matches.length === 1 ? '' : 's'} found`;
    results.append(heading);
    for (const match of matches) {
      const link = document.createElement('a');
      link.href = `#${match.id}`;
      link.textContent = `${match.section.dataset.chapter} / ${match.title}`;
      const context = document.createElement('p');
      const position = match.text.toLocaleLowerCase().indexOf(query);
      context.textContent = match.text.slice(Math.max(0, position - 65), position + query.length + 130);
      results.append(link, context);
    }
    if (!matches.length) {
      const empty = document.createElement('p');
      empty.textContent = `No matches for "${search.value.trim()}".`;
      results.append(empty);
    }
  });
  search.addEventListener('keydown', event => {
    if (event.key === 'Escape') { search.value = ''; search.dispatchEvent(new Event('input')); }
    if (event.key === 'Enter') results.querySelector('a')?.focus();
  });
  const diagramDialog = document.createElement('dialog');
  diagramDialog.className = 'diagram-dialog';
  diagramDialog.setAttribute('aria-label', 'Expanded diagram');
  diagramDialog.innerHTML = '<header><span>Diagram</span><button class="diagram-dialog-close" type="button" aria-label="Close expanded diagram">Close</button></header><div class="diagram-full"></div>';
  document.body.append(diagramDialog);
  diagramDialog.querySelector('button').addEventListener('click', () => diagramDialog.close());
  diagramDialog.addEventListener('keydown', event => {
    if (event.key === 'Escape') { event.preventDefault(); diagramDialog.close(); }
  });
  diagramDialog.addEventListener('click', event => { if (event.target === diagramDialog) diagramDialog.close(); });
  document.querySelectorAll('.diagram-open').forEach(button => button.addEventListener('click', () => {
    const figure = button.closest('.mermaid-figure');
    const image = document.createElement('img');
    image.alt = figure.querySelector('svg').getAttribute('aria-label');
    image.src = 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(figure.querySelector('svg').outerHTML);
    image.style.width = `${Math.round(Number(figure.dataset.width))}px`;
    diagramDialog.querySelector('header span').textContent = figure.querySelector('figcaption span').textContent;
    diagramDialog.querySelector('.diagram-full').replaceChildren(image);
    diagramDialog.showModal();
  }));
  let statusTimer;
  document.querySelectorAll('.copy-button').forEach(button => button.addEventListener('click', async () => {
    const source = button.closest('figure').querySelector('code').textContent;
    const status = document.getElementById('copy-status');
    try {
      if (navigator.clipboard?.writeText) await navigator.clipboard.writeText(source);
      else {
        const textarea = document.createElement('textarea');
        textarea.value = source;
        textarea.className = 'clipboard-buffer';
        document.body.append(textarea);
        textarea.select();
        const copied = document.execCommand('copy');
        textarea.remove();
        button.focus();
        if (!copied) throw new Error('Clipboard unavailable');
      }
      status.textContent = 'Source copied';
    } catch { status.textContent = 'Clipboard unavailable. Select the source to copy it.'; }
    clearTimeout(statusTimer);
    statusTimer = setTimeout(() => { status.textContent = ''; }, 4000);
  }));
  const navigationShell = document.querySelector('.navigation-shell');
  if (matchMedia('(max-width: 850px)').matches) navigationShell.open = false;
  document.querySelectorAll('.sidebar a').forEach(link => link.addEventListener('click', () => {
    if (matchMedia('(max-width: 850px)').matches) navigationShell.open = false;
  }));
})();