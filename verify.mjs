import assert from 'node:assert/strict';
import { readFile } from 'node:fs/promises';
import path from 'node:path';
import { root, renderMarkdown, inline, validateHtml, globalConceptIndex } from './build.mjs';

assert.equal(inline('[Java](01-java-jvm.md#ch01-java-jvm)'), '<a href="#ch01-java-jvm">Java</a>');
assert.equal(inline('`<token>`'), '<code>&lt;token&gt;</code>');
assert.equal(inline('**Commit**'), '<strong>Commit</strong>');
assert.throws(() => inline('[unsafe](javascript:alert)'), /Unsupported/);
const example = renderMarkdown('# Title\n\n> [!TRAP]\n> **Careful** with `state`.\n\n| A | B |\n|---|---|\n| first | second |\n\n```mermaid\nflowchart LR\n A-->B\n```', '00');
assert.match(example.html, /class="callout trap"/);
assert.match(example.html, /<strong>Careful<\/strong>/);
assert.match(example.html, /<th scope="col">A<\/th>/);
assert.match(example.html, /A--&gt;B/);
assert.equal(example.diagrams.length, 1);
assert.throws(() => renderMarkdown('```java\nmissing end', '01'), /Unclosed/);
assert.throws(() => validateHtml('<a href="#missing">x</a>'), /Missing anchors/);
assert.throws(() => validateHtml('<p id="same"></p><p id="same"></p>'), /Duplicate anchor/);
assert.throws(() => validateHtml('<script src="https://example.test/script.js"></script>'), /Remote/);
const catalog = JSON.parse(await readFile(path.join(root, 'catalog.json'), 'utf8'));
assert.equal(catalog.chapters.length, 28);
assert.equal(new Set(catalog.readingOrder).size, 28);
assert.equal(catalog.readingOrder.at(-1), '20');
assert.equal(catalog.readingOrder.at(-2), '24');
const appendixIndex = globalConceptIndex(catalog);
assert.equal(appendixIndex.split('\n').length - 2, new Set(catalog.chapters.flatMap(chapter => chapter.terms.map(term => term.toLocaleLowerCase('en')))).size);
for (const chapter of catalog.chapters) {
  assert.ok(appendixIndex.includes(`](${chapter.file}#ch${chapter.id}-${chapter.slug})`));
  for (const term of chapter.terms) assert.ok(appendixIndex.includes(`[${term}](#${chapter.termAnchors?.[term] || `ch${chapter.id}-${chapter.slug}`})`));
}
const source = await readFile(path.join(root, 'src', '00-master-map.md'), 'utf8');
const chapter = renderMarkdown(source, '00');
assert.equal(chapter.diagrams.length, 3);
for (const required of ['Big Picture', 'What You Will Be Able to Explain', 'Related Chapters', 'One-Page Cheat Sheet', 'Interview Corner']) assert.ok(source.includes(required), required);
assert.ok(!/```(?:java|sql|sh|powershell)/.test(source), 'No unexecuted application snippets in Chapter 00');
const html = await readFile(path.join(root, 'dist', 'java-fs-guide.html'), 'utf8');
const checks = validateHtml(html);
const sections = new Map([...html.matchAll(/<(article|section) class="(?:chapter|planned-chapter)" data-chapter="(\d{2})"[\s\S]*?<\/\1>/g)].map(match => [match[2], match[0]]));
assert.equal(sections.size, 28);
if (sections.get('20')?.startsWith('<article')) {
  assert.ok(!html.includes('{{GLOBAL_CONCEPT_INDEX}}'));
  for (const chapter of catalog.chapters) for (const term of chapter.terms) {
    assert.ok(sections.get('20').includes(`href="#${chapter.termAnchors?.[term] || `ch${chapter.id}-${chapter.slug}`}"`), `Appendix publishes ${chapter.id}: ${term}`);
  }
}
const completed = [];
for (const destination of catalog.chapters) {
  const section = sections.get(destination.id);
  assert.ok(section, `Chapter section ${destination.id}`);
  if (destination.id !== '00') assert.ok(section.includes('href="#ch00-master-map"'), `Backlink ${destination.id}`);
  assert.ok(html.includes(`href="#ch${destination.id}-${destination.slug}"`));
  if (section.startsWith('<article')) {
    completed.push(destination.id);
    const chapterSource = await readFile(path.join(root, 'src', destination.file), 'utf8');
    for (const [term, target] of Object.entries(destination.termAnchors || {})) {
      assert.ok(destination.terms.includes(term), `Registered term ${term}`);
      assert.ok(chapterSource.includes(`<a id="${target}"></a>`), `Topic anchor ${term}`);
      assert.ok(html.includes(`href="#${target}"`), `Published topic link ${term}`);
    }
    for (const required of ['Big Picture', 'What You Will Be Able to Explain', 'Related Chapters', 'One-Page Cheat Sheet', 'Interview Corner']) assert.ok(chapterSource.includes(required), `${destination.id}: ${required}`);
    const relatedPosition = section.indexOf('<nav class="related"');
    const cheatPosition = section.indexOf(`<a id="ch${destination.id}-cheat-sheet"`);
    assert.ok(relatedPosition >= 0 && relatedPosition < cheatPosition, `Related links precede closing sections in ${destination.id}`);
    for (const link of chapterSource.matchAll(/\]\((\d\d)-[^#)]+\.md#ch\d\d-[^)]+\)/g)) {
      if (link[1] !== destination.id) assert.ok(sections.get(link[1]).includes(`href="#ch${destination.id}-${destination.slug}"`), `Reciprocal link ${destination.id} -> ${link[1]}`);
    }
  }
}
assert.ok(completed.includes('00'));
assert.ok(html.includes(`${String(completed.length).padStart(2, '0')} / 28`));
assert.match(html, /Offline edition/);
const buildReport = JSON.parse(await readFile(path.join(root, 'dist', 'build-report.json'), 'utf8'));
assert.equal(buildReport.renderedDiagrams, buildReport.diagramSources, 'Every Mermaid source must render');
assert.equal((html.match(/class="mermaid-figure"/g) || []).length, buildReport.diagramSources);
assert.equal((html.match(/<svg\b/g) || []).length, buildReport.diagramSources);
assert.ok(!html.includes('renderer unavailable in no-download edition'), 'Published diagrams must not degrade to raw source');
const labChapters = [
  { id: '01', directory: '01-java-jvm', listing: 'LoadingLab.java' },
  { id: '02', directory: '02-concurrency', listing: 'SnapshotLab.java' },
  { id: '03', directory: '03-spring', listing: 'src/main/java/guide/spring/ProxyBoundaryLab.java' },
  { id: '04', directory: '04-jpa', listing: 'src/main/java/guide/jpa/SyncJob.java' },
  { id: '05', directory: '05-apis-realtime', listing: 'ReplayCursorLab.java' },
  { id: '06', directory: '06-databases', listing: 'MvccLab.java' },
  { id: '07', directory: '07-messaging', sources: ['DeliveryLab.java'] },
  { id: '11', directory: '11-lld', sources: ['LldLab.java'] },
  { id: '19', directory: '19-testing', sources: ['src/main/java/guide/testing/JobService.java', 'src/test/java/guide/testing/JobServiceTest.java'] },
  { id: '23', directory: '23-jvm-performance', sources: ['ProfilingLab.java', 'src/main/java/guide/performance/SumBenchmark.java'] },
];
const javaExecution = {};
for (const lab of labChapters.filter(item => completed.includes(item.id))) {
  const javaChapter = await readFile(path.join(root, 'src', `${lab.directory}.md`), 'utf8');
  const listings = [...javaChapter.matchAll(/```java\r?\n([\s\S]*?)\r?\n```/g)];
  assert.equal(listings.length, lab.listing ? 1 : 0, `All Chapter ${lab.id} Java listings must have mapped source checks`);
  if (lab.listing) {
    const listingSource = await readFile(path.join(root, 'code', lab.directory, lab.listing), 'utf8');
    assert.equal(listings[0][1].replace(/\r\n/g, '\n').trim(), listingSource.replace(/\r\n/g, '\n').trim(), `Chapter ${lab.id} inline Java matches saved source; this is not compilation`);
  }
  if (lab.sources) {
    const runner = await readFile(path.join(root, 'code', lab.directory, 'run.ps1'), 'utf8');
    for (const filename of lab.sources) {
      const source = await readFile(path.join(root, 'code', lab.directory, filename), 'utf8');
      assert.ok(source.trim(), `Chapter ${lab.id} standalone source exists`);
      assert.ok(javaChapter.includes(filename), `Chapter ${lab.id} documents standalone source`);
      assert.ok(runner.includes(`'${filename}'`), `Chapter ${lab.id} runner includes source`);
    }
  }
  const execution = JSON.parse((await readFile(path.join(root, 'code', lab.directory, 'execution.json'), 'utf8')).replace(/^\uFEFF/, ''));
  assert.ok(['PASS', 'FAIL', 'NOT EXECUTED'].includes(execution.status));
  assert.notEqual(execution.status, 'FAIL', 'Recorded Java lab failures must not pass the publication gate');
  if (execution.status === 'NOT EXECUTED') assert.ok(javaChapter.includes('**Execution status:** NOT EXECUTED.'));
  javaExecution[lab.id] = execution.status;
}
for (const lab of [{ id: '03', directory: '03-spring', commands: 2 }, { id: '04', directory: '04-jpa', commands: 1 }, { id: '19', directory: '19-testing', commands: 1 }, { id: '23', directory: '23-jvm-performance', commands: 1 }].filter(item => completed.includes(item.id))) {
  const runner = await readFile(path.join(root, 'code', lab.directory, 'run.ps1'), 'utf8');
  const mavenCommands = runner.split(/\r?\n/).filter(line => line.includes('& $maven.Source'));
  assert.equal(mavenCommands.length, lab.commands, `Chapter ${lab.id} runner has explicit Maven commands`);
  for (const command of mavenCommands) assert.match(command, /& \$maven\.Source -o -B /, 'Java lab Maven calls must stay offline');
}
for (const chapterId of ['08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27'].filter(id => completed.includes(id))) {
  const registered = catalog.chapters.find(chapter => chapter.id === chapterId);
  const source = await readFile(path.join(root, 'src', registered.file), 'utf8');
  assert.equal(Object.keys(registered.termAnchors || {}).length, registered.terms.length, `${chapterId}: all terms mapped`);
  for (const type of ['MECHANISM', 'TRAP', 'INTERVIEW', 'PRODUCTION', 'DECISION']) {
    assert.ok(source.includes(`> [!${type}]`), `${chapterId}: ${type} callout`);
  }
}
if (completed.includes('11')) {
  const source = await readFile(path.join(root, 'src', '11-lld.md'), 'utf8');
  const cases = [...source.matchAll(/## \d+\. LLD \d+:[\s\S]*?(?=<a id="ch11-|$)/g)];
  assert.equal(cases.length, 12, 'Twelve complete local design cases');
  for (const entry of cases) for (const required of ['**Requirements/API:**', '**Mechanism:**', '**Concurrency/failure:**', '**Tests:**', 'classDiagram', 'Use when', 'Avoid when', 'IntegrationHub']) {
    assert.ok(entry[0].includes(required), `${entry[0].split('\n')[0]} includes ${required}`);
  }
  const java = await readFile(path.join(root, 'code', '11-lld', 'LldLab.java'), 'utf8');
  const tests = ['testLru', 'testRateLimiter', 'testParking', 'testElevator', 'testNotifications', 'testLogger', 'testScheduler', 'testTtl', 'testPubSub', 'testConnectors', 'testIdempotency', 'testWebhooks'];
  for (const name of tests) {
    assert.ok(java.includes(`static void ${name}(`), `LLD test declared: ${name}`);
    assert.ok(java.includes(`${name}();`), `LLD test invoked: ${name}`);
  }
}
if (completed.includes('12')) {
  const source = await readFile(path.join(root, 'src', '12-hld.md'), 'utf8');
  const cases = [...source.matchAll(/## \d+\. [^\n]+\n[\s\S]*?(?=<a id="ch12-|$)/g)];
  assert.equal(cases.length, 12, 'Twelve complete system design cases');
  for (const entry of cases) for (const required of ['### Requirements and Estimate', '### API and Data Model', '### Flow, Deep Dive and Failures', '```mermaid', 'Use when', 'Avoid when', '**IntegrationHub mapping:**', '**Acceptance check:**']) {
    assert.ok(entry[0].includes(required), `${entry[0].split('\n')[0]} includes ${required}`);
  }
}
for (const lab of [{ id: '09', directory: '09-frontend', source: 'ViewStateLab.mjs', checks: 5 }, { id: '10', directory: '10-system-design', source: 'CapacityLab.mjs', checks: 4 }, { id: '16', directory: '16-delivery', source: 'PromotionLab.mjs', checks: 4 }, { id: '18', directory: '18-operations', source: 'SloLab.mjs', checks: 5 }, { id: '21', directory: '21-network-os', source: 'StreamLab.mjs', checks: 3 }, { id: '22', directory: '22-distributed', source: 'OrderingLab.mjs', checks: 4 }, { id: '25', directory: '25-architecture', source: 'ArchitectureLab.mjs', checks: 5 }, { id: '26', directory: '26-data-platform', source: 'WarehouseLab.mjs', checks: 5 }, { id: '27', directory: '27-payments', source: 'LedgerLab.mjs', checks: 8 }].filter(item => completed.includes(item.id))) {
  const execution = JSON.parse(await readFile(path.join(root, 'code', lab.directory, 'execution.json'), 'utf8'));
  assert.equal(execution.status, 'PASS', `Chapter ${lab.id} executable model checks passed`);
  assert.equal(execution.checks.length, lab.checks);
  const source = await readFile(path.join(root, 'code', lab.directory, lab.source), 'utf8');
  assert.ok(source.includes('node:assert/strict'));
  const chapterSource = await readFile(path.join(root, 'src', `${lab.directory}.md`), 'utf8');
  assert.ok(chapterSource.includes(lab.source), `Chapter ${lab.id} documents the executable model`);
}
if (completed.includes('27')) {
  const source = await readFile(path.join(root, 'src', '27-payments.md'), 'utf8');
  const cases = [...source.matchAll(/## \d+\. HLD Case Study:[\s\S]*?(?=<a id="ch27-|$)/g)];
  assert.equal(cases.length, 3);
  for (const entry of cases) for (const required of ['### Requirements and Estimate', '### API and Data Model', '### Flow, Deep Dive and Failures', '```mermaid', 'Use when', 'Avoid when', '**IntegrationHub mapping:**', '**Acceptance check:**']) assert.ok(entry[0].includes(required));
}
if (completed.includes('24')) {
  const source = await readFile(path.join(root, 'src', '24-capstone.md'), 'utf8');
  for (const required of ['ch24-day', 'ch24-deploy', 'ch24-failures', 'ch24-scale', 'ch24-security', 'ch24-whiteboard', 'ch24-script', 'ch24-poster']) assert.ok(source.includes(`<a id="${required}"></a>`));
  assert.equal((source.match(/\*\*Root-cause path:\*\*/g) || []).length, 5);
  assert.ok(source.includes('Secure HttpOnly session'));
}
if (completed.includes('19')) {
  const tests = await readFile(path.join(root, 'code', '19-testing', 'src/test/java/guide/testing/JobServiceTest.java'), 'utf8');
  for (const name of ['tenantDenialDoesNotTouchRepository', 'acceptedJobTransitionsWithExpectedVersion', 'optimisticConflictIsNotReportedAsSuccess', 'nonAcceptedJobsDoNotWrite']) assert.ok(tests.includes(name));
  const pom = await readFile(path.join(root, 'code', '19-testing', 'pom.xml'), 'utf8');
  assert.ok(pom.includes('<failIfNoTests>true</failIfNoTests>'), 'JUnit fixture must not silently discover zero tests');
}
if (completed.includes('23')) {
  const source = await readFile(path.join(root, 'code', '23-jvm-performance', 'src/main/java/guide/performance/SumBenchmark.java'), 'utf8');
  for (const annotation of ['@Benchmark', '@Warmup', '@Measurement', '@Fork', '@Param', '@State', '@Setup']) assert.ok(source.includes(annotation), `JMH fixture includes ${annotation}`);
}
if (completed.includes('13')) {
  const source = await readFile(path.join(root, 'code', '13-integration', 'IntegrationLab.py'), 'utf8');
  for (const name of ['test_decimal_contract', 'test_chunk_rollback_and_resume', 'test_quarantine_and_checkpoint_agree', 'test_restart_contract_and_tenant_scope']) {
    assert.ok(source.includes(`def ${name}(`), `Integration test source: ${name}`);
  }
  const execution = JSON.parse((await readFile(path.join(root, 'code', '13-integration', 'execution.json'), 'utf8')).replace(/^\uFEFF/, ''));
  assert.ok(['PASS', 'NOT EXECUTED'].includes(execution.status), 'Python failures cannot pass publication');
  if (execution.status === 'PASS') assert.equal(execution.tests, 4);
}
for (const lab of [
  { id: '14', directory: '14-docker', files: ['Dockerfile', 'ProbeServer.java', 'compose.json', '.dockerignore'], checks: 4 },
  { id: '15', directory: '15-kubernetes', files: ['workload.json'], checks: 5 },
  { id: '17', directory: '17-cloud', files: ['main.tf.json'], checks: 5 },
].filter(item => completed.includes(item.id))) {
  const report = JSON.parse((await readFile(path.join(root, 'code', lab.directory, 'execution.json'), 'utf8')).replace(/^\uFEFF/, ''));
  assert.equal(report.staticStatus, 'PASS', `${lab.id}: local file checks`);
  assert.equal(report.status, 'NOT EXECUTED', `${lab.id}: preflight does not establish runtime success`);
  assert.equal(report.checks.length, lab.checks);
  for (const filename of lab.files) assert.ok((await readFile(path.join(root, 'code', lab.directory, filename), 'utf8')).trim());
}
if (completed.includes('14')) {
  const chapter = await readFile(path.join(root, 'src', '14-docker.md'), 'utf8');
  const listing = chapter.match(/```dockerfile\r?\n([\s\S]*?)\r?\n```/);
  const dockerfile = await readFile(path.join(root, 'code', '14-docker', 'Dockerfile'), 'utf8');
  assert.equal(listing?.[1].replace(/\r\n/g, '\n').trim(), dockerfile.replace(/\r\n/g, '\n').trim(), 'Dockerfile listing matches saved recipe, not a build test');
  const compose = JSON.parse(await readFile(path.join(root, 'code', '14-docker', 'compose.json'), 'utf8'));
  assert.equal(compose.services.probe.pull_policy, 'never');
  assert.deepEqual(compose.services.probe.ports, ['127.0.0.1:18080:8080']);
}
if (completed.includes('15')) {
  const manifest = JSON.parse(await readFile(path.join(root, 'code', '15-kubernetes', 'workload.json'), 'utf8'));
  const deployment = manifest.items.find(item => item.kind === 'Deployment');
  const service = manifest.items.find(item => item.kind === 'Service');
  const hpa = manifest.items.find(item => item.kind === 'HorizontalPodAutoscaler');
  assert.equal(service.spec.selector.app, deployment.spec.template.metadata.labels.app);
  assert.equal(hpa.spec.scaleTargetRef.name, deployment.metadata.name);
  assert.equal(deployment.spec.template.spec.containers[0].imagePullPolicy, 'Never');
}
if (completed.includes('16')) {
  const pipeline = await readFile(path.join(root, 'code', '16-delivery', 'Jenkinsfile'), 'utf8');
  assert.ok(pipeline.includes('ProbeServer --self-test'));
  assert.ok(pipeline.includes('archiveArtifacts'));
  assert.ok(!/docker push|kubectl apply|terraform apply/.test(pipeline), 'Teaching pipeline does not deploy');
}
if (completed.includes('17')) {
  const config = JSON.parse(await readFile(path.join(root, 'code', '17-cloud', 'main.tf.json'), 'utf8'));
  assert.equal(config.resource.aws_s3_bucket.artifact.lifecycle.prevent_destroy, true);
  assert.equal(config.resource.aws_s3_bucket.artifact.force_destroy, false);
  for (const key of ['block_public_acls', 'block_public_policy', 'ignore_public_acls', 'restrict_public_buckets']) {
    assert.equal(config.resource.aws_s3_bucket_public_access_block.artifact[key], true);
  }
}
console.log(JSON.stringify({ result: 'PASS', completed, javaExecution, checks: 'Markdown subset, escaping, tables, callouts, fence errors, catalog, required sections, all internal links, reciprocal chapter relationships, chapter endings, topic anchors, Java listing/source synchronization (not compilation), offline assets, honest completion status', ...checks }, null, 2));