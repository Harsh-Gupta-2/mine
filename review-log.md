# Review Log

## 2026-10-05 / Step 0 / Chapter 00

### Source Review

- Read the supplied master prompt in full; saved PROMPT.md byte-for-byte. Source and saved SHA-256 both equal `16AC0DBF1DDE624F17C1E40A8704038EC8448FAECD00E67345C6E5D975D2EB1B`.
- Wrote 00-plan.md, catalog.json and src/00-master-map.md. Chapter 00 is about 3,800 whitespace-delimited words including Mermaid source; no Java/SQL/shell application snippets require execution in this chapter.
- Registry contains all 28 unique chapter IDs in the exact prescribed reading order. Identified the extra LLDs as idempotency-key registry and webhook delivery dispatcher. Part B was not built.
- Technical review distinguished HTTP acceptance from completion, flush from commit, timeout from known failure, publication intent from cross-system atomicity, and Kafka transactions from external exactly-once effects.
- Reviewed tenant isolation, service-local ownership, deduplication transaction boundaries, external timeout uncertainty, SSE replay versus live fan-out, native EventSource credential constraints, and Kafka consumer-group load distribution.
- IntegrationHub is explicitly fictional. No real-company architecture, measured benchmark, or regulatory conclusion was invented.
- Official source consulted: https://spring.io/projects/spring-cloud. Its compatibility table lists Boot 4.0.x with Cloud 2025.1.x. No assertion is made that a family match establishes patch support or proves all starter/plugin combinations.

### Verification Items: 1

1. **[VERIFY: exact patches, support windows, and dependency/plugin compatibility must be rechecked against Spring's compatibility table, the Spring Boot system requirements, and the PostgreSQL, Kafka, Kubernetes and JDK vendor release documentation before executable examples are pinned.]** Location: Chapter 00 version assumptions. Status: open. Family-level Spring compatibility checked; exact runtime artifacts are intentionally not selected or tested here.

### Tooling and Degradation

- Empty workspace confirmed. Standalone Node/npm/Java/javac/Pandoc absent from PATH; installed Chrome and VS Code available.
- Portable Node download was skipped. Two npm dependency-install invocations returned no usable output and did not establish a usable installation; stopped that route. Portable Pandoc download was skipped. User then explicitly selected **No downloads**. No downloads were attempted after that selection.
- Existing VS Code Code.exe runs Node v24.20.0 via ELECTRON_RUN_AS_NODE. No separate runtime was installed. Local Chrome prints using a dedicated profile.
- Preferred markdown-it/highlight.js/Mermaid CLI pipeline unavailable. Pandoc fallback also unavailable. Implemented a constrained local-only renderer with escaping, tables, callouts, cross-reference rewriting and explicit validation. This is an additional degradation from the requested fallback, not an assertion that Pandoc ran.
- **Unmet visual requirements:** zero rendered SVG diagrams from three Mermaid source blocks; no syntax highlighting. No local Mermaid bundle is embedded. These limitations are disclosed in the HTML cover, README and state.
- The HTML is self-contained: CSS and reader JavaScript inline, no external scripts/styles/images/fonts required. External primary-source hyperlinks are optional references, not runtime dependencies.

### Executed Checks

- PowerShell initial source check: 28 unique registry entries, 65 source cross-chapter links checked, approximately 3,800 words.
- `build.ps1 -HtmlOnly` and `build.ps1 -Test`: PASS. Actual output reported 76 unique anchors, 665 internal links, zero broken links, 27 reciprocal relationships back to 00, and 474 unique registered coverage terms.
- Renderer checks cover inline escaping, bold, chapter link rewriting, rejecting unsafe link schemes, table/callout rendering, unclosed fence rejection, duplicate/missing anchor rejection, offline asset checks, and required Chapter 00 sections.
- `build.ps1`: generated HTML and PDF with installed Chrome; initial PDF size 1,306,470 bytes. Actual current result retained in dist/build-report.json, not a hard-coded success claim.
- PDF structural check: `%PDF-1.4` header, 34 page objects, 1,699 link annotations, valid `%%EOF` ending. This proves generation and link presence, not that every viewer resolves every annotation correctly.
- VS Code diagnostics: no errors in build.mjs, reader.js, reader.css, or verify.mjs at the check time.
- Browser desktop 1440x1000: search for `idempotency` returned 3 chapters; impossible query showed 0 results; dark theme changed root theme to `dark`; Chapter 00 link reached `#ch00-master-map`; document width 1425 did not exceed viewport 1440.
- Browser mobile 390x844: document width 375 did not exceed viewport 390; navigation initially collapsed, opened, navigated to `#ch06-databases`, and closed; search for `tenant` returned 1 chapter. Mobile screenshot inspected: title, toolbar, facts and labels fit without overlap.
- Final rebuild and test after the Node entry-point/search-title fix: PASS, same 76 anchors and 665 internal links; PDF regenerated at 1,306,470 bytes. Final workspace diagnostics reported no errors.
- Final desktop screenshot inspected at 1440x1000: sidebar, toolbar, cover and chapter directory fit without visible overlap. Copy control reported `Source copied`. Corrected search result title read `00 / The System Before the Details` without repeating the chapter number.

### Remaining Uncertainty

- Static diagram rendering and general-purpose highlighting have not been run and remain incomplete.
- CSS paged-media footer rules are present; exact footer text/placement and one-page cheat-sheet pagination have not yet been text-extracted or visually verified from the PDF.
- No Java, Spring, database, Kafka or Kubernetes runtime has been executed. Chapter 00 makes no such claim. Future runnable examples need a JDK and any relevant infrastructure, or explicit not-executed labels.
- No load, resilience, security, cross-region or recovery experiments have run. The reference design is explanatory, not production-ready software.
- Search, navigation, theme and layout smoke tests were executed in one Chromium-based browser; cross-browser and accessibility-assistive-technology checks are not claimed.

Stop after Step 0 as requested. Next authorized `next` action: Chapter 01.

## 2026-10-05 / Next / Chapter 01

### Files and Scope

- Re-read PROMPT.md and state.md before selecting the chapter. Wrote only the next requested chapter: src/01-java-jvm.md. Chapters 02-27 remain unwritten; no lite implementation was created.
- Chapter length: 9,220 whitespace-delimited words including fenced source; 8,432 excluding code/diagram fences. Nine Mermaid source diagrams, 24 interview-corner questions/model answers, topic-specific checks, one-page cheat sheet, IntegrationHub placement and reciprocal Related chapters links.
- Added code/01-java-jvm/CoreJavaLab.java, LoadingLab.java, run.ps1 and README.md. Runner generated execution.json with actual NOT EXECUTED status. No JDK was found on PATH, through JAVA_HOME or in the common installed-JDK locations checked. No runtime or dependency downloads were attempted.
- Updated catalog.json with eleven Chapter 01 topic-anchor mappings; generalized publication counts, topic index destinations, reciprocal-link validation and closing-section placement in build.mjs/verify.mjs. Added word/verification/diagram metrics to the build report and print rules for later chapters' closing sections/long code listings.
- Updated README.md and state.md for two written chapters and Chapter 02 as the next chapter. Removed a stale Chapter 00-only publication claim from the master map without rewriting its content. Preserved PROMPT.md.

### Technical Review

- Re-read the entire Chapter 01 source after initial rendering. Re-read CoreJavaLab source and checked the complete LoadingLab listing against its file. These are source reviews, not Java compilation.
- Consulted https://www.oracle.com/java/technologies/java-se-support-roadmap.html: current LTS is Java 25 in the roadmap accessed; retained Java 21 as the no-preview source baseline.
- Consulted https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-5.html: distinguished loading, verification/preparation/resolution and initialization; class identity includes defining loader; failed initialization can leave the class erroneous.
- Consulted https://openjdk.org/jeps/444 and https://openjdk.org/jeps/491: differentiated Java 21 virtual-thread behavior from Java 24's monitor-pinning change inherited by 25, including changes to old pinning diagnostics. No pinning experiment was run.
- Reviewed pass-by-value versus shared-object mutation; heap versus total process memory; unreachable cycles versus strongly retained leaks; HashMap equality versus collisions and mutable-key failure; invariance/erasure; reverse resource closing and suppressed exceptions; Stream laziness/optimization/single-use; shallow record immutability and sealed extension boundaries.
- Explicitly avoided universal HashMap tree-threshold/complexity guarantees, invented timing results, unconditional collector tuning advice, and claiming a timed Future.get bounds executor cleanup. Admission limits remain necessary with virtual threads.
- CoreJavaLab's side-effecting sequential Stream map and constant CollisionKey hash are marked diagnostic probes, not production recommendations. Uppercasing synthetic IDs is explicitly not an IntegrationHub connector normalization rule.
- Existing fictional IntegrationHub boundaries remain unchanged: tenant-scoped keys, service authorization, PostgreSQL authority and durable outbox/progress, bounded connector work, explicit external-effect uncertainty. No real-company system is asserted.

### Verification Items: 6

1. **[VERIFY: before using these examples in a service, check the chosen JDK vendor's current patch/support policy and the exact Spring Boot system requirements and Spring Cloud compatibility matrix. Java 25 LTS status was checked against the Oracle roadmap; the runnable source deliberately remains Java 21.]** Location: version assumptions. Status: LTS designation source-checked; exact deployed runtime/framework patch selection remains open.

2. **[VERIFY: release-specific feature claims should be checked against JEPs 286, 395, 409, 431, 440, 441 and 444 and the chosen release documentation. A feature being available does not mean its related preview APIs are stable; this chapter uses no preview APIs.]** Location: release milestones. Status: conservative known feature mappings documented; JEP 444 consulted directly, remaining enumerated JEPs are recheck references, not claimed individually fetched.

3. **[VERIFY: collector availability/defaults, heap ergonomics, object layout and container-awareness behavior vary by JDK vendor, release and platform. Check the selected runtime's GC tuning guide and actual configuration; do not reuse Java 21 tuning flags blindly on Java 25.]** Location: memory/GC. Status: deployment-specific verification open; no collector flags executed, heap measured or GC experiment run.

4. **[VERIFY: spread hashing, power-of-two sizing, tree-bin thresholds, resize behavior and tree-bin lookup details are OpenJDK implementation facts, not Map guarantees. Check HashMap.java in the exact JDK source tag when an interview asks for numerical thresholds or implementation complexity.]** Location: collections. Status: exact vendor/source tag not pinned; numerical thresholds deliberately not promised.

5. **[VERIFY: parallel Stream scheduling/pool interactions, optimization behavior and collection-return contracts must be checked against the chosen JDK's Stream API and implementation. Do not treat the common-pool execution pattern as a universal caller-controlled executor contract.]** Location: functional pipelines. Status: no runtime observations; source probes are unexecuted and not performance evidence.

6. **[VERIFY: pinning and diagnostic claims are version-sensitive. Check JEP 444 for Java 21 and JEP 491 for the Java 24 change, plus the exact deployed vendor build and library/native-call path. Both JEPs were consulted for this chapter; no pinning experiment was executed.]** Location: virtual threads. Status: primary JEPs checked; deployed build, native/library paths and measured pinning remain open.

Total guide markers: seven, including Chapter 00's existing one. Markers denote required release/deployment rechecks as well as residual uncertainty; they are not claims that all surrounding technical statements are guesses.

### Executed Validation

- `code/01-java-jvm/run.ps1`: actually executed the preflight and recorded NOT EXECUTED. Both Java compilation and both lab programs remain not executed. The wrapper's missing-JDK path is tested; compiler-present success/failure paths are not.
- `build.ps1 -HtmlOnly` plus `build.ps1 -Test` after the first publisher edit: PASS on the existing chapter. Reran after creating Chapter 01, then after index/review edits; PASS each time.
- Final `build.ps1` and `build.ps1 -Test`: PASS. 144 unique anchors, 738 internal links, zero broken internal links; all explicit cross-chapter links have reverse links. Two available chapters, 26 planned chapters, 474 registered coverage terms. Required sections, ending order, offline assets, section-level topic anchors and exact inline LoadingLab/source match validated. A recorded Java FAIL now fails the publication gate; NOT EXECUTED remains an explicit non-pass for Java.
- Final HTML and PDF rebuilt. PDF is 1,667,327 bytes with `%PDF-1.4` header, 64 page objects, 1,836 link annotations and a valid `%%EOF` ending. Link annotation presence is not exhaustive viewer-level testing.
- Browser check at 1440x1000: sidebar navigated to `01 / Core Java and the JVM`; search for `pinning` found Chapter 01; the Java code block contained the full LoadingLab class; cover showed `02 / 28`. Document width 1425 did not exceed viewport 1440.
- Browser width check at 390x844: document width 375 did not exceed viewport 390. This was a targeted layout check, not a new complete cross-browser/accessibility audit.
- Final VS Code diagnostics reported no errors. No Java compiler was available, so absence of editor diagnostics must not be construed as Java language/runtime validation.

### Remaining Limitations

- Java compilation, runtime assertions, Java 21/25 cross-runtime comparison, GC/JIT experiments and virtual-thread pinning/performance remain NOT EXECUTED. No fake output or benchmark is included.
- Twelve Mermaid sources are preserved, zero rendered SVGs; no Mermaid syntax/render execution occurred. Syntax highlighting is still unavailable. Existing no-download restriction remains in force.
- PDF generated successfully; exact footer positioning, one-page cheat-sheet fit, and every individual PDF link are not visually/text-extraction verified. CSS includes print page numbers and closing-section page breaks.
- Internal links and topic targets are validated. External primary-document URLs were included/consulted as described; an exhaustive external HTTP-link audit was not executed.

Next `next` action: Chapter 02, Concurrency. Re-read PROMPT.md and state.md first.

## 2026-10-06 / Next / Chapter 02

### Scope and Review

- Re-read PROMPT.md and state.md before writing only the next chapter, src/02-concurrency.md. No later chapter or IntegrationHub-lite implementation was written.
- Final length: 10,417 whitespace-delimited words including fences; 9,598 excluding fenced code/diagrams. Ten Mermaid source diagrams, 28 interview-corner questions/model answers, topic checks, one-page cheat sheet, IntegrationHub placement and bidirectional chapter relationships.
- Added code/02-concurrency/SnapshotLab.java, ConcurrencyLab.java, run.ps1 and README.md. Executed the wrapper; it generated execution.json with NOT EXECUTED because no compiler was available through PATH/JAVA_HOME. No dependency/runtime downloads attempted.
- Re-read the full chapter and ConcurrencyLab source. Publication validation compares the full SnapshotLab listing with its source file. These are source reviews and document checks, not Java compilation or concurrency stress testing.
- Checked Java 21 ThreadPoolExecutor API at https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ThreadPoolExecutor.html for core/queue/max ordering, rejection, captured submit failures, shutdown and termination semantics.
- Checked Java 21 CompletableFuture API at https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html for completion execution policies, allOf/anyOf, timeout mutation, cancellation and the ineffective mayInterruptIfRunning parameter in that implementation.
- Reused Chapter 01's JEP 444/491 source checks and Java 21/24/25 boundary. JLS and concurrent-package pages are linked as authoritative references without claiming they were newly fetched in this turn or that any JMM experiment ran.
- Reviewed visibility versus atomicity; transitive happens-before; publication versus later mutation; full-invariant locking; CAS retry side effects; LongAdder limitations; condition predicates; permit ownership; bounded admission; worker-slot deadlock; completion versus actual task termination; context propagation and cross-Pod coordination limits.
- Review corrections: allowed CAS or a suitable lock for competing snapshot updates; clarified that separately read atomics can produce an incoherent combination, not that they always do; distinguished waiting before the second submission from waiting after both submissions; added the executor shutdown-race rejection branch and a concurrent-collection implementation verification marker.
- SnapshotLab's bounded spin is explicitly a diagnostic probe. Its final fields and volatile handoff have overlapping guarantees, so deleting volatile is not claimed to fail deterministically. ConcurrencyLab deliberately splits an increment and arranges both reads before either write with a barrier; it does not pretend to measure the ++ operator probabilistically.
- Latches hold executor tasks for a core=1/max=2/queue=1 saturation scenario; these are fixture assumptions, not production settings or measured latency. Wait deadlines can fail under severe scheduling delay. No sleeps or permanently deadlocked processes are introduced.
- Added eleven Chapter 02 section-level term mappings, including a second owner for virtual threads while retaining 474 unique terms. Generalized listing/execution-status checks across Chapters 01 and 02; publication PASS now displays Java NOT EXECUTED separately. Updated README/state; next chapter is 03.

### Verification Items: 8

1. **[VERIFY: confirm the deployed JDK vendor/build and framework patch compatibility against the JDK release notes, Spring Boot system requirements and Spring Cloud compatibility table. Java 21 source compatibility does not validate a production executor configuration or third-party driver's cancellation behavior.]** Location: version assumptions. Status: exact deployed build/framework integration open; no new framework pairing was introduced.

2. **[VERIFY: atomic access modes, weak-CAS spurious-failure rules, VarHandle memory effects and LongAdder internals must be checked against the selected JDK's atomic/VarHandle API and exact source. This chapter's labs use ordinary strong atomic operations, not hand-written relaxed-memory algorithms or claimed hardware instruction mappings.]** Location: atomics. Status: no relaxed-memory experiments or CPU-specific mappings claimed; source-tag verification remains open.

3. **[VERIFY: fairness exceptions, condition interrupt behavior, StampedLock guarantees and AQS internals are API/implementation-specific. Check the Java 21 lock/condition documentation and the deployed JDK source before attributing a wait state, spinning policy or performance advantage to a particular lock.]** Location: explicit locks. Status: mechanisms described conservatively; exact implementation/performance not measured.

4. **[VERIFY: the queue-before-growth and shutdown contracts were checked in the Java 21 ThreadPoolExecutor API. The combined control field, worker loop and internal state encoding must be checked in the exact OpenJDK/vendor source tag; they are not an ExecutorService implementation requirement.]** Location: executor internals. Status: public API checked; exact private implementation tag not selected.

5. **[VERIFY: CompletableFuture completion, default-executor and cancellation policies were checked against the Java 21 API. Check the exact completion-stage implementation and HTTP/JDBC client contracts before relying on timeout propagation, interruption, exceptional-result selection or callback execution context.]** Location: futures. Status: public API checked; client/framework-specific cancellation and context remain open.

6. **[VERIFY: ConcurrentHashMap implementation details and concurrent-collection iterator/aggregate guarantees must be checked against the exact JDK API and source tag. Per-key atomicity is an API-level reasoning tool; segment/bin layouts and resize algorithms are not universal concurrent-map contracts.]** Location: concurrent collections. Status: no exact vendor source inspection or map performance/stress experiment claimed.

7. **[VERIFY: deadlock detection and thread-dump visibility differ for platform and virtual threads and across JDK tools/releases. Check ThreadMXBean, jcmd thread-dump documentation and JFR event support for the deployed version; an empty monitor-deadlock report does not prove the absence of pool, resource or virtual-thread deadlocks.]** Location: diagnosis. Status: no dump capture, detector run or profiling performed.

8. **[VERIFY: Java 21 virtual-thread contracts, the Java 24 monitor-pinning change, current structured-concurrency/scoped-value API status, and framework context support must be checked against JEP 444, JEP 491 and the selected release/framework documentation. These labs use final Java 21 APIs only and do not test pinning or preview structured-concurrency APIs.]** Location: virtual-thread policy. Status: prior primary JEP checks reused; no preview API, pinning test or context-integration run.

Total guide verification markers: fifteen, comprising 00: one, 01: six, 02: eight. These are explicit deployment/version rechecks and residual uncertainty, not invented claims of completed verification.

### Actual Validation

- Ran code/02-concurrency/run.ps1: preflight correctly recorded NOT EXECUTED for SnapshotLab and ConcurrencyLab. Java compilation and both programs' assertions did not run. Compiler-present wrapper paths remain unverified.
- Ran build.ps1 -HtmlOnly and build.ps1 -Test after chapter creation, then after review/index/listing changes: PASS. Reran final build.ps1 and build.ps1 -Test: PASS.
- Final publication checks: 219 unique anchors, 818 internal links, zero broken internal links, reciprocal links for explicit cross-chapter references, correct closing-section order, complete registered topic anchors, offline assets, exact Java listing/source match for both chapters. Three available chapters and 25 planned entries. Java statuses separately report NOT EXECUTED for 01 and 02; a recorded Java FAIL would fail the gate.
- Final PDF: generated from the same HTML by installed Chrome; 2,049,729 bytes, `%PDF-1.4`, 95 page objects, 1,985 link annotations, valid EOF marker. Structural checks do not establish correctness of every individual viewer link or exact printed layout.
- Browser desktop 1440x1000: Chapter 02 sidebar link opened `02 / Concurrency and the Memory Model`; CallerRunsPolicy search returned Chapter 02; the complete SnapshotLab listing was present; cover showed `03 / 28`. Document width 1425 did not exceed viewport 1440.
- Browser mobile 390x844: code listing scrolled into view; document width 375 did not exceed viewport 390. This is a targeted overflow/navigation/content check, not a comprehensive accessibility or cross-browser audit.
- VS Code diagnostics reported no errors. No Java compiler/language execution evidence follows from that result.

### Remaining Gaps

- All Chapter 02 Java compilation, runtime assertions, scheduling/stress claims, cancellation experiments and pinning/performance checks remain NOT EXECUTED. No jcstress/JMH run, database/network call or benchmark was performed.
- Twenty-two Mermaid sources across the guide, zero rendered SVGs; Mermaid syntax/rendering was not executed. Syntax highlighting remains unavailable. No-download restriction still applies.
- PDF page-number rules and closing-section page breaks exist, but exact footer placement, one-page cheat-sheet fit and every PDF link have not been visually/text-extraction verified.
- Internal links are checked; exhaustive external HTTP-link validation was not performed. Source documentation checks are listed above precisely.

Next `next` action: Chapter 03, Spring and Spring Boot internals. Re-read PROMPT.md and state.md first, and recheck framework compatibility before selecting executable examples.

## 2026-10-06 / Next / Chapter 03

### Scope and Technical Review

- Re-read PROMPT.md and state.md and wrote only src/03-spring.md. No later chapter, Boot web application or IntegrationHub-lite service was built.
- Final chapter length: 12,365 whitespace-delimited words including fences / 11,405 excluding fenced source. Eleven Mermaid source diagrams, 32 interview-corner questions/model answers, topic checks, one-page cheat sheet, IntegrationHub placement and reciprocal links.
- Added code/03-spring/pom.xml, src/main/java/guide/spring/ProxyBoundaryLab.java, ContainerTransactionLab.java, run.ps1 and README.md. Runner generated execution.json. Compilation, Maven dependency resolution and both Java programs remain NOT EXECUTED: no JDK, Maven or local Maven repository was available. No artifact/tool downloads occurred.
- Official compatibility matrix https://spring.io/projects/spring-cloud rechecked: Cloud 2025.1.x supports Boot 4.0.x; Boot 4.1.x support starts with Cloud 2025.1.2. The page identified Cloud 2025.1.3. Boot 4.0 requirements at https://docs.spring.io/spring-boot/4.0/system-requirements.html described Boot 4.0.8, Java 17 minimum and Framework 7.0.9 or above. Selected Java 21/Boot parent 4.0.8/Cloud BOM 2025.1.3 as explicit unexecuted teaching pins, not latest-security-patch recommendations.
- Consulted Framework proxy documentation, transaction annotation documentation, Boot 4.0 auto-configuration documentation and Spring Cloud OpenFeign reference. Verified the documented proxy self-invocation boundary, class-proxy limitations versus interface proxying, configurable transaction rollback rules, auto-configuration backoff and Spring Cloud's Retryer.NEVER_RETRY default. OpenFeign feature-complete status is explicitly version-labeled.
- Re-read the whole chapter, including its remaining interview section, plus the container/transaction lab. Exact inline ProxyBoundaryLab/source equality is enforced by publication validation. This is source review, not proof of Spring runtime behavior.
- Reviewed constructor injection, singleton/request/prototype ownership, definition versus instance post-processing, initialization versus exposed proxies, configuration-method enhancement versus service AOP, conditions versus bean overriding, typed config versus refresh, filter/MVC/validation boundaries, rollback-only propagation, resource-manager participation, and post-commit response failure.
- Reviewed first-matching security-chain behavior, tenant/resource authorization, cookie/CSRF versus internal bearer boundaries, Actuator exposure versus access control, liveness versus readiness, Gateway stack/filter/body constraints, controlled config refresh, Feign timeout/retry ownership and Kubernetes-native discovery alternatives.
- Corrections during review: discovery diagram now distinguishes metadata lookup from the client's direct request instead of suggesting the registry forwards HTTP; REQUIRES_NEW pool-starvation wording explicitly depends on insufficient capacity; removed an accidental non-ASCII spelling. No unrelated cleanup or chapter rewrites.
- The negative transaction fixture intentionally contrasts external rollback with a self-call's unadvised auto-committed insert. A separate facade invokes the service proxy inside a REQUIRED transaction and catches the inner failure to check UnexpectedRollbackException. H2 and explicit context configuration do not establish Boot defaults, PostgreSQL behavior, production pooling or distributed atomicity.
- Registered sixteen Chapter 03 termAnchors, extended Java listing/status checks to its Maven source path, and added drift checks for offline -o on both Maven command sites. Updated README/state; next chapter is 04.

### Verification Items: 13

1. **[VERIFY: Boot 4.0.8 and Cloud BOM 2025.1.3 are teaching pins selected after checking the official system requirements and compatibility matrix, not a latest-security-patch recommendation. Recheck the exact artifacts, support windows, JDK range, build plugins and transitive dependencies before execution or deployment; no Maven resolution was possible here.]** Location: version assumptions. Status: documentation/family compatibility checked; exact artifact resolution and supported deployment open.

2. **[VERIFY: exact callback order, early-reference behavior, circular-reference policy, scoped-proxy behavior and AOT/native constraints depend on the Spring/Boot version and bean kind. Check the selected Framework lifecycle/post-processor documentation and test the actual context; the chapter diagram is a normal-path model, not a universal callback trace.]** Location: lifecycle. Status: source model only; context callbacks unexecuted.

3. **[VERIFY: proxy defaults, method visibility support and per-bean proxy controls differ across Framework/Boot releases. Official proxy documentation was consulted; the labs deliberately choose interface proxying and class-based transaction proxying explicitly. Do not apply class-proxy final-method restrictions to all interface proxy calls or assume weaving behaves like proxies.]** Location: proxies. Status: official mechanism checked; no JVM execution/weaving/native-image test.

4. **[VERIFY: Boot 4 modularization, starter names, auto-configuration packages and condition definitions differ from Boot 3 examples. Check the pinned Boot reference and condition report; official Boot 4.0 auto-configuration documentation was consulted, but no Boot application or report was executed here.]** Location: Boot auto-configuration. Status: reference checked; no Boot context/conditions report produced.

5. **[VERIFY: property-source precedence, relaxed binding, constructor binding, validation activation and config-data imports vary with Boot/Cloud version and application setup. Consult the pinned external-configuration reference and inspect effective origins; no configuration binding or live-refresh test ran in this lab.]** Location: configuration. Status: actual binding/import/refresh remains untested.

6. **[VERIFY: MVC method-validation activation, exception types, validation groups and proxy-based service validation differ across Framework versions and annotation placement. Check the pinned MVC/Bean Validation integration documentation; no controller, validator or error-handler integration test was executed.]** Location: validation. Status: no HTTP/converter/validator execution.

7. **[VERIFY: transactional method visibility, global rollback defaults, reactive cancellation semantics, savepoint support and timeout/read-only enforcement vary by Framework, transaction manager and resource. Official annotation/proxy documentation was consulted; the H2 lab is unexecuted and cannot establish PostgreSQL behavior, production pooling or cross-resource atomicity.]** Location: transactions. Status: documentation checked; resource-specific and lab runtime behavior open.

8. **[VERIFY: SecurityFilterChain defaults/order, matcher APIs, method-security activation, JWT claim validation and management-endpoint security differ by Spring Security/Boot version and configuration. Check the pinned servlet security architecture and test both allowed and denied paths; no authentication or authorization integration test ran in these labs.]** Location: Security. Status: no security infrastructure configured or executed by the labs.

9. **[VERIFY: Actuator endpoint access/exposure defaults, property names, health groups, probe integration and security backoff vary across Boot versions. Verify the pinned operational reference and actual network paths; no Actuator endpoint or Kubernetes probe was executed.]** Location: Actuator. Status: no endpoints/probes/network exposure tested.

10. **[VERIFY: Gateway variant, starter names, route/filter configuration namespaces, filter ordering and rate-limiter defaults are release-specific. Check the pinned Spring Cloud Gateway documentation for the chosen WebFlux or MVC server; no route, Redis limiter or streaming proxy was executed.]** Location: Gateway. Status: source-only conceptual flow, no gateway configuration/runtime validation.

11. **[VERIFY: Config Client import/bootstrap behavior, refresh/rebind support, RefreshScope restrictions, Bus integration and management endpoint exposure vary by Cloud/Boot version. Check the selected Config/Common documentation and test the specific bean/resource; no Config Server or refresh cycle was executed.]** Location: Config Server. Status: no remote configuration, Bus or scope recreation tested.

12. **[VERIFY: OpenFeign defaults, client selection, Retryer.NEVER_RETRY, ErrorDecoder behavior, URL/load-balancer resolution, refresh support and feature-complete status are release-sensitive. The official reference was consulted; underlying HTTP-client timeout/cancellation semantics and every remote invocation remain untested.]** Location: OpenFeign. Status: official reference checked; no HTTP client, pool or remote request executed.

13. **[VERIFY: Eureka lease/cache behavior, Spring Cloud LoadBalancer selection/caching and Kubernetes DNS/Service/endpoint routing depend on release and deployment. Check the actual client and cluster data plane; no registry, Kubernetes service or multi-Pod load-distribution experiment was run.]** Location: discovery. Status: no discovery registry or cluster test.

Total guide markers: twenty-eight (00: 1, 01: 6, 02: 8, 03: 13). These preserve version/deployment uncertainties without implying that unavailable runtime checks passed.

### Executed Validation

- Ran code/03-spring/run.ps1: prerequisite path generated NOT EXECUTED for both labs; compiler-present/offline-cache paths remain untested. Parsed pom.xml with PowerShell's XML parser: well-formed XML with artifact spring-mechanisms-lab and Boot parent 4.0.8. XML parsing is not Maven dependency resolution.
- Ran build.ps1 -HtmlOnly and build.ps1 -Test after chapter creation and again after review/index/test edits: PASS. Final build.ps1 plus build.ps1 -Test: PASS.
- Final publication checks: 311 unique anchors, 907 internal links, zero broken internal links, required sections and ending order, reciprocal chapter references, registered topic anchors, self-contained reading assets and all three chapters' inline Java/source matches. Runner-source checks confirm both Maven invocations contain offline mode. Four available chapters, 24 planned entries, 474 unique registered terms.
- Java execution statuses are explicitly NOT EXECUTED for 01, 02 and 03, separate from document PASS. No Spring, H2, JDBC, HTTP, authentication, broker or cluster output is invented.
- Final PDF: 2,512,093 bytes, `%PDF-1.4` header, 135 page objects, 2,143 link annotations, valid EOF ending. Generated by installed Chrome from the same HTML, not Pandoc or a downloaded converter.
- Browser at 1440x1000: Chapter 03 navigation reached the correct heading; searching UnexpectedRollbackException returned that chapter; complete ProxyBoundaryLab listing present; cover showed 04 / 28. Document width 1425 did not exceed viewport 1440.
- Browser at 390x844: code listing scrolled into view; document width 375 did not exceed viewport 390. These are targeted content/navigation/overflow checks, not a comprehensive accessibility/cross-browser audit.
- VS Code diagnostics reported no errors. Missing Java/Maven means no compilation claim follows from editor diagnostics or the POM parser.

### Remaining Gaps

- Java compilation, artifact/plugin resolution and both Spring/H2 program assertions remain NOT EXECUTED. The lab is deliberately not a Boot web application. MVC, validation, Security, Actuator, Gateway, Config, Feign and discovery examples have no executed integration coverage.
- Thirty-three Mermaid sources across the guide, zero rendered SVGs; Mermaid parsing/rendering not run. Syntax highlighting remains unavailable and no-download restriction persists.
- PDF structure passed; exact footer placement, one-page cheat-sheet fit, visual pagination and every individual PDF link were not verified through screenshots/text extraction.
- Internal links are validated. External documentation was consulted as listed; exhaustive external HTTP-link auditing was not performed.

Next `next` action: Chapter 04, Hibernate and JPA. Re-read PROMPT.md and state.md, then verify the provider/API versions managed by the chosen Boot baseline before writing executable examples.

## 2026-10-06 / Diagram Repair and Chapter 04 / Resumed Completion

### Recovered Work and Scope

- User requested repairing diagrams in all previous chapters and continuing to Chapter 04, then asked to resume the interrupted work. Read PROMPT.md, state.md, the current build report and actual source before continuing. The saved state still described Chapters 00-03 and raw diagrams, but files already contained Chapter 04, the vendored renderer and an HTML-only build with 42 SVGs. Continued from those files rather than recreating them or starting Chapter 05.
- Diagram dependency permission is limited to Mermaid rendering assets. Existing vendor/mermaid/README.md records Mermaid 11.12.0 provenance and prior npm SHA-512 integrity verification; LICENSE is retained. No downloads were performed during this resumed work. Java/Maven/ORM dependencies remain outside that permission.
- Completed Chapter 04 review and publication: 8,997 words including fenced source / 8,285 excluding fenced code and diagrams; nine Mermaid diagrams, 24 interview questions/model answers, cheat sheet, IntegrationHub placement and explained-here index. Added all ten Chapter 04 registered term-to-section mappings and advanced state/README to Chapter 05.
- Reviewed the complete chapter, JpaLab.java, Connector.java, SyncJob listing, persistence XML and POM. The Boot dependency-table observations are retained as version-qualified prior-work evidence: Jakarta Persistence 3.2.0, Hibernate 7.2.24.Final, Spring Data JPA 4.0.7 and H2 2.4.240 under the selected Boot 4.0 family. No local artifact resolution or PostgreSQL compatibility test is claimed.
- Lab uses assigned fixture IDs, a small tenant/connector/job model, Version, a lazy to-one association and disposable H2 create-drop. It does not implement production tenant enforcement, Spring Data repository behavior, L2 caching, pessimistic locking or a full IntegrationHub service. Identity, flush/rollback, detached merge, stale version and cold-context fetch-plan assertions are present but not executed.

### Diagram Repair and Visual Review

- Retained the prior local-browser rendering pipeline: Mermaid browser bundle evaluated in installed headless Chrome with HTTP/HTTPS blocked, pre-rendered SVG embedded in HTML, source/config/bundle-fingerprinted cache and per-diagram ID/reference prefixes. No CDN or client-side Mermaid runtime is needed to read the publication.
- Prior interrupted work repaired sequence-label semicolons, added layer colors to sequence participants, local scrolling, source disclosure and expanded views. During this resume, a fresh cache revision rerendered every diagram successfully; all 33 older diagrams plus nine Chapter 04 diagrams are present.
- Removed obsolete source-only rendering notices in Chapters 01-03 while preserving Java NOT EXECUTED status. Replaced outdated README/state claims without rewriting historical review entries, which intentionally document the older fallback editions.
- Initial visual audit found long horizontal flows compressed below useful text size. Removed the 960px minimum-width cap; screen diagrams retain approximately 85% or more of natural width and scroll inside their own viewport. Expanded views now use natural SVG width rather than stretching narrow diagrams or shrinking the widest ones.
- Browser testing found Escape did not close the expanded dialog in the shared browser. Added an explicit dialog keydown handler; focused-control Escape retest passed. Close-button and source controls remain available.
- Dark-theme ER screenshot exposed a relationship-label background with the same green fill as its text. Renderer revision 3 gives ER background rectangles a white fill. Re-rendered all 42 diagrams; measured label text rgb(22,101,52) against rgb(255,255,255), and inspected the corrected print-view screenshot with visible referenced_by label.
- Inspected a light-theme IntegrationHub architecture screenshot and dark/print ER views. Diagrams retain a white canvas for stable contrast across reader themes. Print CSS hides source/expand controls and fits vectors to its height bound. These are representative visual checks, not manual inspection of every page and label.

### Verification Items: 9

1. **[VERIFY: recheck the exact Boot-managed Hibernate/Jakarta Persistence/Spring Data versions, provider support and database driver/dialect against the selected artifacts before execution. The published Boot dependency table was consulted, but no Maven resolution or PostgreSQL compatibility test ran.]** Location: version assumptions. Status: prior documentation observation retained; actual artifact resolution remains open.

2. **[VERIFY: dirty-checking implementation, enhancement, read-only optimizations and persistence-context lifecycle vary by Hibernate/Framework configuration. Check the selected provider documentation and actual SQL/memory behavior; no dirty-checking or batch-memory experiment ran locally.]** Location: context. Status: mechanism reviewed; provider execution/memory evidence absent.

3. **[VERIFY: AUTO/COMMIT and Hibernate-specific flush behavior, native-query synchronization, SQL ordering, identifier timing and bulk-mutation semantics require the pinned provider/driver configuration. The lab is unexecuted; statement-shape explanations are not actual SQL traces or a guarantee that one annotation produces one statement.]** Location: flush. Status: no actual SQL trace, timing or bulk-update test.

4. **[VERIFY: lazy/eager realization, proxy/enhancement requirements, to-one defaults and Open Session in View behavior must be checked against Jakarta Persistence, the exact Hibernate mapping and Boot settings. No web serialization or lazy-detachment failure test ran in this environment.]** Location: lazy loading. Status: web/context boundary behavior untested.

5. **[VERIFY: Hibernate fetch joins, entity graph semantics, batch/subselect fetching, duplicate-root handling, multiple-bag restrictions and pagination warnings/failure settings are provider/version-specific. Verify actual SQL and row volume on the selected database; no fixture count, query plan or load result is measured here.]** Location: fetch plans. Status: fixture assertions are source only, not measured counts.

6. **[VERIFY: optimistic-check timing, exception translation, relationship version increments, pessimistic lock scope/timeouts and deadlock behavior depend on provider, mapping and database. H2 is not evidence for PostgreSQL locks or isolation; no pessimistic-lock experiment or PostgreSQL conflict test was executed.]** Location: locking. Status: no provider/database runtime checks.

7. **[VERIFY: L2/query cache defaults, provider integration, concurrency strategies, cache layout and external-write invalidation are Hibernate-version/configuration specific. Both shared/query caches are explicitly disabled in the lab, so no cache-provider behavior or cluster coherence was tested.]** Location: caching. Status: no shared cache configured or tested.

8. **[VERIFY: mapping access rules, ID generation/batching, cascade/orphan-removal behavior, proxy equality, enum/temporal conversion and schema generation need validation against the selected provider and database. The lab is deliberately minimal and create-drop is restricted to its disposable H2 database; no production migration or tenant-constraint enforcement was executed.]** Location: mapping. Status: fixture mapping reviewed; production invariants not implemented/tested.

9. **[VERIFY: Spring Data JPA new-entity detection, repository transaction defaults, modifying-query flush/clear options, entity-graph execution and paging/scroll support must be checked against the selected version. The standalone JPA lab does not instantiate Spring Data repositories or execute any repository integration tests.]** Location: repositories. Status: repository discussion only, no Spring Data runtime evidence.

Total guide verification markers: 37 (00: 1, 01: 6, 02: 8, 03: 13, 04: 9).

### Executed Gates

- After the first resumed edits, ran build.ps1 -HtmlOnly and build.ps1 -Test. Repeated after sizing, Escape and ER-contrast changes. All successful builds passed required-section, internal-anchor, reciprocal-link, SVG-count and source-listing checks.
- Extended verify.mjs to compare Chapter 04's complete SyncJob listing with its Maven source and include its execution report. Both Chapter 03/04 Maven runner command sites are checked for offline mode; Java FAIL is rejected while NOT EXECUTED remains explicitly separate from publication PASS.
- Fresh renderer revision 3 generated all 42 SVGs in local Chrome. No missing/blank SVGs found in the browser audit; final document has 1,153 unique IDs and 972 internal links with zero missing fragment targets. These include namespaced SVG identifiers; they are not all chapter anchors.
- Final build.ps1 plus build.ps1 -Test: PASS, with five chapters available and 23 planned. Java execution statuses for 01-04: NOT EXECUTED. Chapter 04 runner preflight re-executed and again recorded missing existing JDK/Maven/cached dependencies. No Java assertions ran.
- Parsed POM and persistence XML with PowerShell XML APIs: artifact jpa-mechanisms-lab, persistence schema version 3.2. This establishes well-formed XML, not remote XSD validation or provider compatibility.
- Final PDF rebuilt from the same SVG-containing HTML: 3,170,608 bytes, %PDF-1.4 header, 187 page objects, 2,245 link annotations, valid EOF marker.
- Browser: 42 diagram figures, no blank SVGs, no page-level horizontal overflow at 1440px or 390px. At mobile size, minimum rendered/natural width ratio approximately 0.849 (rounding around the 0.85 target), with 40 wide diagrams scrolling within their viewport instead of shrinking text to fit the page.
- Expanded ER image decoded successfully at its natural 246px width. Explicit Escape closed the focused dialog. Dark theme remained functional. ER text/background contrast check and print screenshot passed after repair.
- Print-media audit: all 42 figures present, every source disclosure hidden, no diagram SVG above the tested 840px height threshold, no external script/stylesheet/image URLs. This checks print-media CSS bounds, not every paginated PDF page.
- Final Chapter 04 navigation and orphanRemoval search passed; cover showed 05 / 28. VS Code diagnostics reported no errors at the final source check.

### Remaining Limits and Handoff

- Java/Hibernate/H2 compilation/execution, ORM statement counts, optimistic/pessimistic behavior and production database assumptions remain NOT EXECUTED. No runtime output or performance result is invented.
- Syntax highlighting is still unavailable; Markdown remains the tested subset renderer. Static diagrams are now implemented, so the earlier diagram-rendering limitation is resolved.
- Exact PDF footer placement, every PDF link, every printed label and one-page cheat-sheet fit were not exhaustively verified. Long diagrams remain vector graphics; the HTML's scrolling/expanded view offers larger inspection. Representative screenshots and automated bounds are not an exhaustive visual audit.
- User asked to resume and finish this work, not begin another chapter. Next `next` action is Chapter 05: REST, API design and real-time communication. Re-read PROMPT.md and state.md first.

## 2026-10-06 / Next / Chapter 05

### Scope and Technical Review

- Wrote src/05-apis-realtime.md: 11,693 words including fenced source, 10,494 excluding it. Eight rendered Mermaid diagrams, fourteen reference tables, six typed callouts, 28 interview-corner questions, twelve [VERIFY] markers.
- Fifteen numbered sections plus explained-here index, Related chapters, cheat sheet and interview corner. Explicit anchors: ch05-resources, ch05-errors, ch05-versioning, ch05-pagination, ch05-idempotency, ch05-webhooks, ch05-documentation, ch05-protocols, ch05-polling, ch05-sse, ch05-websocket, ch05-reconnect, ch05-backpressure, ch05-reactive, ch05-labs, ch05-concept-index, ch05-cheat-sheet, ch05-interview.
- All 21 registered Chapter 05 terms now map to explanatory section anchors in catalog.json; the coverage index links to sections rather than the chapter head.
- Mechanism review: HTTP method properties are presented as three separate attributes (safe, idempotent, cacheable) rather than one notion; 202 is tied to durable acceptance, never to completed work; the unknown-outcome definition from Chapter 00 drives the idempotency section.
- Idempotency is written as a four-part contract (scope, content binding, stored result, retention) and explicitly not as exactly-once. The concurrent-retry race is attributed to select-then-insert, with the unique constraint as the fix. Downstream duplication is deferred to 07.
- Replay is written as five distinct cursor cases, with out-of-retention falling back to a durable snapshot rather than the oldest buffered event. The chapter states that Last-Event-ID requests retained history and does not guarantee it.
- Transport guidance follows the guide's existing position: polling stays the authoritative baseline, SSE is the optimization, WebSocket requires a specific bidirectionality justification. SSE identity follows the Chapter 00 ruling: same-origin secure HttpOnly session cookie through the gateway or BFF, never a token in the stream URL.
- Backpressure is framed as the absence of unbounded per-subscriber buffers, with coalesce, disconnect-and-replay and block-the-producer presented as three trade-offs, the last marked as wrong for a shared stream.
- Reactor versus virtual threads is presented as a constraint comparison, not a performance claim. No benchmark, latency or throughput figure appears anywhere in the chapter.
- The single illustrative HTTP/problem-details listing uses a `text` fence and is labelled as a contract sketch, not captured output. No SQL, HTTP trace or broker output is fabricated.
- Webhook destination validation is stated as a server-side request forgery control enforced at delivery time rather than only at registration, with ownership assigned to 08 and 17.

### Verification Items: 12

1. **[VERIFY: recheck the Boot-managed Reactor, Jackson, springdoc/OpenAPI and gRPC artifact versions, plus the HTTP semantics RFC numbers cited below, against the actual dependency table and the current IETF documents before publication. No artifact resolution, specification diff or protocol trace was performed in this environment.]** Location: version assumptions. Status: no dependency resolution or specification retrieval occurred.

2. **[VERIFY: method safety, idempotency and cacheability definitions, the exact semantics of 202 and 409, and conditional-request handling should be rechecked against RFC 9110 and RFC 9111 rather than quoted from memory. No specification text was retrieved in this environment.]** Location: HTTP semantics. Status: mechanism described from knowledge, not from retrieved specification text.

3. **[VERIFY: confirm RFC 9457's obsoletion of RFC 7807, the registered `application/problem+json` media type behavior and Spring's current ProblemDetail defaults, including whether a given Boot version emits problem details for framework-level errors without explicit configuration.]** Location: error model. Status: Spring default behavior unverified against the selected Boot version.

4. **[VERIFY: check the current Sunset and Deprecation header specifications and their adoption status before presenting them as standard practice, and confirm the chosen JSON binding's unknown-field defaults in the selected Boot version.]** Location: versioning. Status: advisory headers described generically; binding defaults unverified.

5. **[VERIFY: deep-offset cost, index-supported keyset behavior and count-query cost are engine- and plan-dependent. Measure on PostgreSQL 17 with representative data volume; no query, plan or timing was executed here.]** Location: pagination. Status: no query, plan or measurement performed.

6. **[VERIFY: idempotency-key header conventions are still being standardized in the IETF; check the current draft status before describing a header name as a standard. Also confirm the chosen store's constraint behavior and expiry semantics for the production deployment.]** Location: idempotency. Status: no header name is presented as standardized; draft status unverified.

7. **[VERIFY: webhook signature schemes, timestamp tolerance windows and standard rate-limit header names vary by vendor and draft specification. Confirm the chosen scheme and header set against current references before publishing them as the IntegrationHub contract.]** Location: limits and webhooks. Status: scheme described structurally; no specific header set is claimed as standard.

8. **[VERIFY: gRPC-Web and browser support details, GraphQL cost-analysis tooling and the current Spring support for both should be rechecked against their project documentation. No gRPC or GraphQL runtime was exercised in this environment.]** Location: protocol alternatives. Status: no runtime exercised.

9. **[VERIFY: EventSource header limitations, default reconnection timing, per-origin connection limits under HTTP/1.1 versus HTTP/2, and proxy buffering defaults vary by browser, server and gateway. Confirm against current browser documentation and your gateway configuration; no browser or proxy behavior was measured here.]** Location: SSE. Status: no browser, gateway or proxy behavior measured.

10. **[VERIFY: confirm the current Spring WebSocket and STOMP support, broker relay requirements and the status of the Framework's messaging abstractions in the selected Boot version before recommending a topology.]** Location: WebSocket. Status: topology discussed, not configured or run.

11. **[VERIFY: per-subscriber buffering behavior, default overflow strategies and write-timeout settings differ across Spring MVC SSE emitters, WebFlux and the servlet container. Confirm the defaults for the selected stack; no load test or memory measurement was performed here.]** Location: backpressure. Status: no load test or heap measurement.

12. **[VERIFY: virtual-thread support in the selected Boot version, pinning behavior differences between Java 21 and JEP 491 in Java 24 and 25, and current WebFlux defaults all require confirmation against release documentation. No benchmark, thread dump or load comparison was produced in this environment.]** Location: Reactor and virtual threads. Status: no benchmark, thread dump or comparison produced.

Total guide verification markers: 49 (00: 1, 01: 6, 02: 8, 03: 13, 04: 9, 05: 12).

### Executed Validation

- Extended verify.mjs with the Chapter 05 lab mapping (code/05-apis-realtime, listing ReplayCursorLab.java) and added the 21 Chapter 05 termAnchors to catalog.json.
- build.ps1: six chapters available, 22 planned; 50 Mermaid sources and 50 rendered SVGs; 1,381 anchors, 1,061 internal links, 0 broken. Eight new diagrams rendered in local Chrome with network blocked.
- build.ps1 -Test: PASS. Required sections, topic anchors, reciprocal cross-chapter links, Related-chapters ordering, inline Java listing/source synchronization and offline-asset checks all passed. Java execution for 01-05 reported separately as NOT EXECUTED.
- Chapter 05 runner preflight had already been executed and recorded NOT EXECUTED with no reachable JDK compiler and downloads unauthorized. Nothing was compiled; no assertion in either lab was evaluated.
- PDF rebuilt: 3,678,649 bytes, %PDF-1.4 header, 226 page objects, 2,387 link annotations, valid EOF marker.
- Browser audit of the published chapter at desktop width: eight diagram figures, no blank SVGs, minimum rendered-to-natural canvas ratio 0.85, no page-level horizontal overflow, fourteen tables, six callouts, exactly one Java listing, Related-chapters navigation ahead of the cheat sheet anchor.
- Screenshot check of the widest new diagram (05.2, natural 1976px) confirmed readable text with inner horizontal scrolling rather than shrink-to-fit.

### Remaining Gaps

- IdempotencyLab and ReplayCursorLab remain NOT EXECUTED. No assertion result, output line, HTTP exchange, SQL statement, latency figure or throughput number in this chapter is an observation.
- No specification text was retrieved. RFC numbers, media types, header names and browser behaviors are stated from knowledge and carry explicit [VERIFY] markers; treat them as claims to confirm, not citations checked in this session.
- Proxy buffering, SSE keep-alive behavior, connection limits and slow-consumer memory growth are described as mechanisms and have not been reproduced against a real gateway, browser or load generator.
- The problem-details listing is an illustrative sketch rather than a response captured from a running service, and is labelled as such in the text.
- Syntax highlighting is still unavailable and the Markdown renderer remains the tested subset. Mobile-width and print-media audits were not repeated for this chapter; the desktop audit and the automated print-media rules from the previous entry still apply.
- Next action in the ascending sequence is Chapter 06: databases and durable state.

## 2026-10-06 / Next / Chapter 06

### Scope and Technical Review

- Wrote src/06-databases.md: 11,662 words including fenced source, 10,198 excluding it. Eight rendered Mermaid diagrams, twelve reference tables, five typed callouts covering all five kinds, 28 interview-corner questions, thirteen [VERIFY] markers.
- Fifteen numbered sections plus explained-here index, Related chapters, cheat sheet and interview corner. Explicit anchors: ch06-relational-model, ch06-storage, ch06-indexes, ch06-plans, ch06-transactions, ch06-isolation, ch06-locks, ch06-maintenance, ch06-pooling, ch06-replication, ch06-scaling, ch06-migrations, ch06-backups, ch06-engines, ch06-labs, ch06-concept-index, ch06-cheat-sheet, ch06-interview.
- All 28 registered Chapter 06 terms map to explanatory section anchors in catalog.json, so the coverage index links to sections rather than the chapter head.
- Added two new labs under code/06-databases: MvccLab.java and ConnectionPoolLab.java, with run.ps1, README.md and execution.json, following the Chapter 05 runner shape. No Maven, no database, no downloads.
- Mechanism review: durability is stated as a local write-ahead-log promise about one instance, explicitly not protection against logical damage or machine loss. Replication is separated from backup in three places because the conflation is the most expensive one in the chapter's subject area.
- Isolation is written as anomalies rather than level names, with lost update and write skew distinguished, first-committer-wins explained, and the serialization-failure retry obligation stated as part of the contract. A TRAP callout records that level names are not portable between engines.
- The connection pool is framed as a limit rather than capacity, with admission and concurrency as two separate bounds, matching the Chapter 02 executor argument and the Chapter 05 rate-limit argument.
- Sharding is gated behind a DECISION callout about reversible versus irreversible options, and the ordered escalation path is stated explicitly.
- Migrations use expand and contract with the rolling-deploy justification, and rollback is described honestly as compatibility of intermediate states rather than reversing a dropped column.
- ACID is unpacked letter by letter because each is routinely overclaimed, particularly consistency and durability.
- No SQL, DDL, plan output, timing, row count, lock trace or replication lag figure appears anywhere; the chapter contains no SQL fence at all and exactly one Java listing.

### Verification Items: 13

1. **[VERIFY: recheck PostgreSQL 17, MySQL 8.4, Redis, MongoDB and Elasticsearch version-specific behavior, along with the Boot-managed driver and pool versions, against current release documentation. No engine was installed, no driver resolved and no release note retrieved in this environment.]** Location: version assumptions. Status: no engine, driver or release note available.

2. **[VERIFY: full-page-write behavior, checkpoint tuning, compaction strategies and buffer-pool defaults are engine- and version-specific. Confirm against PostgreSQL 17 and MySQL 8.4 documentation; no engine was installed or measured in this environment.]** Location: storage engines. Status: mechanism described, nothing measured.

3. **[VERIFY: index-only scan requirements, partial-index matching rules, implicit-conversion behavior and index maintenance costs differ between PostgreSQL and MySQL and across versions. Verify with real plans on representative data; no index was created or measured here.]** Location: indexes. Status: no index built or planned.

4. **[VERIFY: plan node names, EXPLAIN option syntax, statistics collection behavior and planner cost parameters differ by engine and version. Confirm against PostgreSQL 17 and MySQL 8.4 before relying on specific option names; no plan was captured in this environment.]** Location: reading plans. Status: no plan captured; node names are described generically.

5. **[VERIFY: the exact anomalies permitted at each level in PostgreSQL 17 and MySQL 8.4, PostgreSQL's serializable snapshot isolation behavior, and phantom handling under each engine's repeatable read must be checked against their documentation. No engine behavior was executed or observed here.]** Location: isolation. Status: the chapter states explicitly that level names are not portable; engine behavior unverified.

6. **[VERIFY: lock modes, deadlock detection behavior, lock-timeout settings and which DDL operations take blocking locks are strongly engine- and version-specific, including concurrent index creation and constraint validation. Confirm against your engine's documentation; no lock or deadlock was reproduced in this environment.]** Location: locks. Status: no lock or deadlock reproduced.

7. **[VERIFY: autovacuum thresholds and cost settings, freeze and wraparound behavior, InnoDB purge, checkpoint tuning and statistics collection defaults are version-specific and interact with workload. Confirm against PostgreSQL 17 and MySQL 8.4 documentation and with your own monitoring; nothing was measured here.]** Location: maintenance. Status: no monitoring or tuning evidence.

8. **[VERIFY: pool defaults, leak-detection behavior, timeout semantics and recommended sizing formulas differ between pool implementations and engines, and sizing guidance should be derived from measurement rather than a formula. Confirm against the selected pool's documentation; no pool was run or measured here.]** Location: connection pooling. Status: no real pool exercised; the lab is an in-memory model.

9. **[VERIFY: synchronous commit modes, replica consistency options, slot management, failover tooling and lag monitoring differ by engine, version and managed-service provider. Confirm against your deployment; no replication topology was configured or observed here.]** Location: replication. Status: no topology configured.

10. **[VERIFY: partition pruning behavior, partition-wise joins, limits on partition counts, foreign-key and unique-constraint support across partitions, and online repartitioning differ substantially by engine and version. Confirm before designing a partitioning scheme; nothing was created or measured here.]** Location: partitioning and sharding. Status: nothing created or measured.

11. **[VERIFY: engine-specific behavior for adding columns with defaults, concurrent index creation, not-valid constraint validation, and the exact locking of each DDL statement must be confirmed per engine and version before planning an online change. No migration was executed in this environment.]** Location: migrations. Status: no migration executed.

12. **[VERIFY: base-backup tooling, log-archiving configuration, retention mechanics and managed-service restore behavior vary by engine, version and provider, and restore duration is specific to your data volume and storage. Confirm and measure in your environment; no backup or restore was performed here.]** Location: backups. Status: no backup taken or restored.

13. **[VERIFY: feature claims for MongoDB, Redis and Elasticsearch, including their durability and transaction options, change across versions and deployment modes. Confirm against current documentation before relying on any of them; no store was installed or exercised in this environment.]** Location: choosing a store. Status: no alternative store installed.

Total guide verification markers: 62 (00: 1, 01: 6, 02: 8, 03: 13, 04: 9, 05: 12, 06: 13).

### Executed Validation

- Created code/06-databases with MvccLab.java, ConnectionPoolLab.java, run.ps1, README.md and execution.json; added the Chapter 06 lab mapping to verify.mjs and the 28 Chapter 06 termAnchors to catalog.json.
- Executed the Chapter 06 runner preflight. It reported NOT EXECUTED with no reachable JDK compiler and downloads unauthorized. Nothing was compiled and no assertion in either lab was evaluated.
- build.ps1: seven chapters available, 21 planned; 58 Mermaid sources and 58 rendered SVGs; 1,619 anchors, 1,152 internal links, 0 broken. Eight new diagrams rendered in local Chrome with network blocked.
- build.ps1 -Test: PASS, run twice, once after adding the DECISION and INTERVIEW callouts. Required sections, topic anchors, reciprocal cross-chapter links, Related-chapters ordering, inline Java listing/source synchronization and offline-asset checks all passed. Java execution for 01-06 reported separately as NOT EXECUTED.
- PDF rebuilt: 4,143,271 bytes, %PDF-1.4 header, 267 page objects, 2,534 link annotations, valid EOF marker.
- Browser audit of the published chapter at desktop width: eight diagram figures, no blank SVGs, minimum rendered-to-natural canvas ratio 0.85, no page-level horizontal overflow, twelve tables, exactly one Java listing, Related-chapters navigation ahead of the cheat sheet anchor, cover reporting 07 / 28.

### Remaining Gaps

- MvccLab and ConnectionPoolLab remain NOT EXECUTED. Neither has been compiled, and in any case they are in-memory teaching models: MvccLab is not any engine's visibility implementation, and ConnectionPoolLab contains no driver or database.
- Every engine-specific claim in the chapter is reasoning carrying an explicit [VERIFY] marker. No SQL ran, no plan was captured, no lock was taken, no replica was configured, no backup was restored and nothing was timed.
- Index, partitioning and pool-sizing guidance is directional. The chapter deliberately avoids numeric recommendations because none were measured here.
- Mobile-width and print-media audits were not repeated for this chapter; the desktop audit and the automated print-media rules from the Chapter 04 entry still apply.
- Next action in the ascending sequence is Chapter 07: messaging, batch and streams.

## 2026-10-06 / Continued / Chapter 07

### Scope and Source Review

- Wrote src/07-messaging.md: 10,340 whitespace-delimited words including fences, 9,698 excluding them; nine Mermaid diagrams, eighteen tables, all five callout types and 24 interview-corner answers. All 24 catalog terms map to explanatory anchors.
- Added code/07-messaging/DeliveryLab.java, run.ps1, README.md and the runner-generated execution.json. The complete Java model stays in its source file instead of being duplicated inline. verify.mjs explicitly maps its standalone source, checks its presence in the chapter and runner, and includes its execution status while retaining earlier chapters' exact inline checks.
- Reviewed broker acknowledgement versus business completion, ordinary Kafka group ownership versus broadcast, completed-prefix offsets, receipt/effect atomicity, outbox publish/mark duplicates, polling cursor gaps, CDC slot retention, ordered retry consequences, saga unknown outcomes, event-sourcing replay side effects, CQRS rebuild inputs, KStream/KTable replacement semantics, event-time grace, Kafka-scoped transactions and Batch identity/checkpoints.
- The model's immutable state replacement is explicitly not database persistence or actual process termination. Its single checkpoint is one ordered lane, not a multi-partition offset vector. Sources have no external dependencies; compilation remains unavailable.
- Retrieved the Kafka 4.0 Streams core-concepts page, RabbitMQ's current 4.3 confirms guide, Debezium's stable outbox-router reference and Spring Batch's current domain reference. The older Kafka documentation fragment returned a redirect script; no substantive evidence is attributed to that response. Moving references remain version-sensitive.
- Reconciled the Chapter 06 state record with its existing built source. Also recorded its missing SQL query-writing workbook and expanded Redis treatment; the earlier structural publication PASS does not establish complete coverage of PROMPT.md. These editorial gaps remain open.

### Verification Items: 9

1. **[VERIFY: check the actual Boot-managed Spring Kafka, Kafka client and Spring Batch versions, broker compatibility, RabbitMQ queue policies and Debezium connector compatibility before choosing artifacts. Kafka 4.0 Streams, current Batch domain, RabbitMQ confirms and Debezium outbox-router documentation were consulted, but dependencies were not resolved and no broker or framework ran.]** Location: version assumptions. Status: documentation inspected, artifact compatibility and runtime unverified.

2. **[VERIFY: verify producer idempotence constraints, acks/min.insync.replicas interactions, unclean leader election, compaction/tombstone retention, group protocol and rebalance configuration against the selected Kafka 4.x broker and client. This section describes ordinary partition-assigned groups, not share groups; no failover or rebalance test ran.]** Location: Kafka. Status: no broker failover, group assignment or retention experiment.

3. **[VERIFY: confirm RabbitMQ queue type, persistence/confirm behavior, mandatory returns, prefetch scope, delivery limits and dead-letter guarantees against the deployed version. The current 4.3 confirms page was consulted; no RabbitMQ deployment or channel test was executed.]** Location: RabbitMQ. Status: confirms/ack distinction and routing-return behavior inspected in documentation; runtime not tested.

4. **[VERIFY: check Debezium outbox-router insert/update/delete handling, snapshot behavior, connector offset recovery, publication ordering and PostgreSQL slot retention against pinned connector versions. The stable router reference was consulted; no CDC connector, cleanup policy or migration was executed.]** Location: outbox. Status: documented router identity/key semantics inspected; connector recovery and source lifecycle untested.

5. **[VERIFY: validate Spring Kafka retry and dead-letter publication acknowledgement behavior, offset handling on failed recovery, and RabbitMQ dead-letter delivery safety for the chosen queue policies. A configured DLQ is not automatically an atomic transfer; no fault-injection test of either framework ran.]** Location: retries. Status: framework recovery behavior remains unexecuted.

6. **[VERIFY: confirm Kafka Streams window boundary, grace, suppression, stream-time advancement and versioned-store join behavior against the exact 4.x API and topology. The 4.0 core-concepts reference was consulted; no late-event or timestamp-skew experiment ran.]** Location: windows. Status: stream-time/grace and store-version distinctions inspected; no topology tested.

7. **[VERIFY: validate processing.guarantee, transactional IDs/fencing, read_committed consumers, state changelog configuration, standby recovery and topology migration compatibility against the selected Kafka Streams release. Documentation supports the Kafka-scoped guarantee; broker transactions and restore behavior were not tested locally.]** Location: state stores. Status: transaction scope supported by documentation, runtime and recovery unverified.

8. **[VERIFY: check Spring Batch 6.x job/repository configuration, builders, transaction managers, execution-context serialization and restart APIs against the Boot-managed version. The current domain reference was consulted; DeliveryLab is not a Spring Batch execution and no Batch schema was created.]** Location: Batch. Status: job instance/execution and context concepts inspected; no framework configuration or schema ran.

9. **[VERIFY: verify Spring Batch retry/skip defaults, rollback classification, reader thread safety, partition restart behavior and JobRepository/business transaction coordination for the selected release and resources. No framework fault-injection, remote partitioning or API-cursor restart ran.]** Location: restart. Status: no actual Batch failure/restart experiment.

Total guide verification markers: 71 (00: 1, 01: 6, 02: 8, 03: 13, 04: 9, 05: 12, 06: 13, 07: 9).

### Executed Validation

- Chapter 07 runner preflight: NOT EXECUTED, no existing JDK compiler. No downloads, Java compilation, assertion results or broker execution.
- First behavior-scoped check after chapter creation: build.ps1 -HtmlOnly passed, rendering all nine new diagrams and validating the Markdown subset and links.
- After adding term/lab registration, publication tests passed; then rebuilt both HTML and PDF and reran build.ps1 -Test successfully against the fresh output. All eight available chapters have required sections, reciprocal links and expected closing order. Java 01-07 statuses remain NOT EXECUTED, separate from publication PASS.
- Final report: eight available chapters, twenty planned; 67 sources / 67 inline SVGs, 1,860 unique IDs, 1,208 internal links, zero broken links. Full PDF: 4,597,446 bytes, %PDF-1.4 header, 303 page objects, 2,625 link annotations and valid EOF.
- VS Code diagnostics returned no errors for the changed catalog, verifier, chapter, Java source and runner. Diagnostics are not a Java compiler result.
- The previous shared browser page was no longer available. Opened the final local HTML in a new integrated-browser page. At 1440px and 390px widths: nine Chapter 07 diagrams, no zero-width/missing SVGs, eighteen tables, all five callout types and no document-level horizontal overflow. Mobile minimum canvas/natural-width ratio was about 0.8496, matching the existing 0.85 rule after rounding.
- Mobile screenshot of diagram 07.4 confirmed readable table labels within a locally scrollable viewport. This is a representative check, not exhaustive PDF page inspection.

### Remaining Work

- Chapters 08-19, 21-23 and 25-27 remain unwritten, followed by 24 Part A and 20 last. The standing instruction is to continue without asking for `next`; the whole-guide request is not complete.
- Chapter 06's SQL workbook and detailed Redis patterns remain editorial gaps. Chapters 05/06 also need a final accuracy and coverage pass against the authoritative spec; source availability is not a claim of complete spec compliance.
- All Java/framework/broker execution is unverified. Dependency downloads beyond diagrams still need authorization. Syntax highlighting remains unavailable. Every PDF page, printed label and link has not been individually inspected.

## 2026-10-06 / Five-Chapter Batch / Chapter 08

- Wrote src/08-security.md: 6,255 words including fences / 5,778 prose words; seven rendered diagrams, sixteen interview answers, all five callout types and all 26 catalog terms mapped. Reviewed OAuth/OIDC token purpose, tenant authorization, PKCE limitations, refresh races, SAML/SCIM separation, trust/identity, CORS/CSRF, lifecycle rotation, envelope encryption and regulatory caveats.
- build.ps1 -HtmlOnly and -Test passed: 74/74 diagrams, 2,026 IDs, 1,250 links, zero broken links. No protocol implementation was hand-written. Identity-provider, cryptographic, browser-security and regulatory validation remain NOT EXECUTED; references are verification destinations, not new retrieval claims.
- No Java lab is required to illustrate a homemade security protocol: the chapter deliberately directs implementation to maintained protocol libraries. Final five-chapter PDF and browser checks follow after Chapter 12.

### Verification Items: 9

1. **[VERIFY: check the selected Spring Security/resource-server version, OAuth provider discovery metadata, supported flows and RFC 9700 security guidance before implementation. Boot/Cloud family compatibility was checked previously; no identity provider, resource server or federation test ran here.]**
2. **[VERIFY: confirm JWT validation defaults, required claims, clock-skew tolerance, JWK cache/rotation behavior, introspection and refresh-family reuse handling against the selected Spring Security and identity-provider versions. No token, concurrent refresh or logout experiment was executed.]**
3. **[VERIFY: check SAML profile validation, SCIM RFC 7643/7644 behavior, PATCH/filter support, stable identifier mappings and Okta tenant-specific provisioning/deactivation semantics against configured products. No federation or provisioning environment was exercised.]**
4. **[VERIFY: confirm TLS version/cipher policy, certificate path and revocation behavior, hostname checks, keystore provider support and live rotation behavior for the deployed JVM, proxy and mesh. The sequence is conceptual; no handshake, certificate or reload test ran.]**
5. **[VERIFY: compare controls with the current OWASP API Security Top 10 and ASVS requirements appropriate to the product. The categories here are an engineering review checklist, not a security assessment, penetration test or certification.]**
6. **[VERIFY: confirm Vault authentication, policy, lease, renewal and revocation semantics, Kubernetes delivery/reload behavior and provider-specific credential overlap in the actual deployment. No secret store, rotation or revoked-connection test ran.]**
7. **[VERIFY: verify provider KMS envelope-encryption APIs, authenticated context, key versioning, quotas, availability and recovery behavior; confirm algorithm/nonce requirements with the chosen library. No cryptographic code or key-rotation experiment is supplied or executed here.]**
8. **[VERIFY: GDPR applicability, lawful bases, rights handling, retention, breach notification and cross-border transfer obligations require current official guidance and qualified legal/privacy review. Tokenization or pseudonymization is not by itself anonymization. This chapter provides engineering considerations, not legal advice or a compliance determination.]**
9. **[VERIFY: check the currently applicable PCI DSS version, card-data retention restrictions, sensitive-authentication-data rules and eligibility for any reduced-scope assessment with PCI SSC guidance, the payment provider and a qualified assessor. No PCI compliance or scope determination is claimed; Chapter 27 expands the payment architecture.]**

All nine remain open for the stated environment/provider/regulatory validation. Total source markers through 08: 80.

## 2026-10-06 / Five-Chapter Batch / Chapter 09

- Wrote src/09-frontend.md: 5,623 words including fences / 5,245 prose words; six diagrams, sixteen interview answers, all five callouts and twelve mapped terms. Reviewed render/commit, effects, identity generations, cancellation versus unknown server outcome, runtime type validation, snapshot versus delta handling, session cleanup and framework boundaries.
- Added ViewStateLab.mjs and offline run.ps1. Actual run: PASS on v24.20.0 for five checks: stale request, tenant switch-back, progress versions, reverse completion despite ignored abort, disposal. This is not React/Angular/DOM/HTTP/SSE evidence. TypeScript and framework integration remain NOT EXECUTED.
- HTML build passed: 80/80 diagrams, 2,181 IDs, 1,292 links, zero broken links. Added executable-model report/source checks to verify.mjs; final PDF follows the five-chapter batch.

### Verification Items: 5

1. **[VERIFY: confirm React/TypeScript/Angular versions, React Compiler adoption, framework scheduling/effect behavior and target-browser APIs before applying examples to a product. The local lab validates a pure state model, not installed framework integration.]**
2. **[VERIFY: check TypeScript strictness flags, JSON-schema/OpenAPI tooling, unknown-field handling and the Java serializer's numeric/date conventions in the selected versions. The chapter describes contracts; no TypeScript compilation or generated-client integration ran.]**
3. **[VERIFY: confirm EventSource/browser reconnect behavior, fetch-stream parsing support, credentials/origin policy and the backend snapshot/replay protocol against target browsers and gateways. ViewStateLab checks snapshot versions only; it does not exercise SSE transport or React rendering.]**
4. **[VERIFY: verify Angular signals, change-detection mode, HTTP observable cancellation and lifecycle cleanup APIs against the selected major release. Angular is an architectural comparison here; no Angular application or dependency set was installed.]**
5. **[VERIFY: confirm current Core Web Vitals definitions, measurement tools, browser support and accessibility requirements appropriate to the product. No field performance measurements, accessibility conformance audit or framework/browser end-to-end test is claimed by the pure-model lab.]**

All five remain open at the stated framework/environment boundary. Total source markers through 09: 85.

## 2026-10-06 / Five-Chapter Batch / Chapter 10

- Wrote src/10-system-design.md: 5,657 words including fences / 5,278 prose words; six diagrams, sixteen interview answers, all five callouts and seventeen mapped terms. Reviewed requirement boundaries, cache invalidation races, completed versus accepted work, CAP/PACELC, limiter atomicity, retry composition, tenant fairness and fencing versus routing.
- CapacityLab.mjs with offline run.ps1 executed: four PASS checks on v24.20.0. Inputs are assumptions, not measurements: 2,000/s, mean residence 0.125s, 1,000 bytes, seven days, three copies. Calculated concurrency 250, daily records 172,800,000 and storage 3,628,800,000,000 bytes exclude documented overhead. Backlog and deadline checks also passed.
- build.ps1 -HtmlOnly and -Test passed: 86/86 diagrams, 2,343 IDs, 1,344 links, zero broken links. No Resilience4j, network, database, load or regional failover test ran.

### Verification Items: 4

1. **[VERIFY: validate load-balancer health checks, retries, connection draining, idle timeouts and protocol handling against the actual gateway/cloud deployment. No balancing, failover or autoscaling load test ran.]**
2. **[VERIFY: verify consistency guarantees, read-after-write mechanisms, replica promotion/data-loss policies and partitioning constraints against the chosen database/service configuration. CAP/PACELC are reasoning tools, not evidence of an untested product guarantee.]**
3. **[VERIFY: check Resilience4j version compatibility, exception/slow-call classification, decorator/aspect order, scheduler behavior and cancellation semantics against the selected Spring stack. No Resilience4j application, timer or circuit transition test ran; the lab tests budget arithmetic only.]**
4. **[VERIFY: verify managed-service RPO/RTO behavior, replication durability, fencing, DNS/load-balancer propagation and regional key/secret availability in the actual deployment. No failover rehearsal or regional data-loss experiment ran.]**

All four remain open for deployment/runtime verification. Total source markers through 10: 89.

## 2026-10-06 / Five-Chapter Batch / Chapter 11

- Wrote src/11-lld.md: 5,940 words including fences / 5,506 prose words; fourteen diagrams including one class diagram for each of twelve LLD cases. All 26 catalog terms mapped; all five callouts; eight synthesis interview answers plus four-level checks per case.
- Added LldLab.java and offline run.ps1. All twelve implementations and named test methods are present: LRU, token bucket, parking, elevator simulation, notifications, logger, scheduler, TTL, pub/sub, connector strategies, idempotency registry and webhook dispatcher. The final two follow 00-plan.md's agreed set.
- Compiler preflight actually ran and reported NOT EXECUTED. No Java assertions or output were claimed as observed. Reviewed source callback/lock separation, duplicate-owner reservation, TTL replacement, cancellation boundaries, retry classifications and bounded Future guards. VS Code diagnostics reported no errors, not compiler success.
- build.ps1 -HtmlOnly and -Test passed: 100/100 diagrams, 2,612 IDs, 1,400 links, zero broken. Verifier includes Chapter 11 standalone source/runner/report checks. Each case states local scope, APIs, mechanism, concurrency, failure limits, tests and IntegrationHub analogy. Elevator is explicitly not a physical safety controller.

### Verification Items: 2

1. **[VERIFY: compile LldLab.java with an existing JDK 21 or later using --release 21 and run all twelve test methods. Source review and publication checks do not establish Java compilation, concurrent runtime behavior or performance; the runner preflight reported NOT EXECUTED.]**
2. **[VERIFY: before adapting any model, validate actual capacity bounds, clock units/precision, collaborator thread safety, persistence, shutdown and external side-effect contracts. These are intentionally bounded interview models, not tested production cache, scheduler, identity or delivery libraries.]**

Both remain open. The twelve cases are complete for their stated local contracts, not production implementations. Total source markers through 11: 91.

## 2026-10-06 / Five-Chapter Batch / Chapter 12 and Final Gates

- Wrote src/12-hld.md: 7,409 words including fences / 6,648 prose words; fourteen diagrams, twelve complete HLD cases, twelve synthesis interview answers and all five callout types. Every case includes requirements/estimate, API/data model, architecture, main flow/deep dive/failures, use/avoid table, IntegrationHub mapping and proposed acceptance checks. All twelve terms have explanatory anchors.
- Cases: URL shortener, rate limiter, notifications, OAuth/token service, bulk sync, webhooks, payment/wallet, distributed cache, gateway, durable scheduler, search and live status. Estimates state hypothetical units and omit no overhead silently; no capacity, provider, monetary or compliance result is claimed as measured.
- Reviewed identity/authority in each case, callback uncertainty, cache freshness, snapshot/replay continuity, scheduling leases versus fencing, current ledger state versus projections and tenant authorization. HLDs are designs, not deployed services. Payment/provider/regulatory implementation needs the stated qualified review.

### Verification Items: 3

1. **[VERIFY: validate all service/version capabilities, topology limits, data-retention policies, security controls and operational capacity against the selected implementations before building these designs. No HLD deployment, load test, failover rehearsal or provider integration ran.]**
2. **[VERIFY: confirm identity-provider protocol conformance, refresh rotation/reuse behavior, client registration, key storage and regional recovery semantics for the actual provider. No authorization-server, OIDC or session failover test was executed.]**
3. **[VERIFY: monetary posting rules, payment-provider idempotency/retention, authorization/capture/refund behavior, PCI DSS scope and regulatory obligations require current provider documentation and qualified financial/compliance review. This is a design exercise, not a tested or compliant payment system.]**

All three remain open. Total source markers: 94, with 23 added in this five-chapter batch (08=9, 09=5, 10=4, 11=2, 12=3).

### Final Coverage and Corrections

- Added verifier checks requiring all five callout types and complete term mapping for 08-12, twelve LLD cases with API/mechanism/concurrency/tests/class diagrams/IntegrationHub references, all twelve Java test declarations and main invocations, and twelve HLDs with every required design section. These checks supplement link/render tests; they are not a substitute for semantic or runtime review.
- The stronger gate caught Chapter 09's missing Mechanism callout and missing explicit IntegrationHub mappings in LLD cases 11/12. Added the guarded-response explanation and both mappings, reran the same gate, and obtained PASS. Initial failures are recorded here rather than presented as uninterrupted success.
- Corrected compact UML declarations so every field/method is rendered as a separate member. This reduced excessive diagram width and improved legibility. Representative mobile screenshot of diagram 11.3 confirms distinct compartments and labels.
- Final source metrics: 08=6,255 words, 09=5,672, 10=5,657, 11=6,000, 12=7,409; combined 30,993 words including diagrams, or 28,564 excluding fences. Earlier entries record pre-correction counts where applicable.

### Actual Final Validation

- Per-chapter HTML builds and publication tests completed. Final full build.ps1 rebuilt HTML/PDF from the reviewed sources, followed by build.ps1 -Test PASS. Thirteen chapters available, fifteen planned; 114 Mermaid sources/114 inline SVGs; 2,973 IDs, 1,475 internal links, zero broken.
- Final PDF: 6,100,742 bytes, %PDF-1.4 header, 440 page objects, 3,088 link annotations and valid EOF. No assertion of exhaustive page/link inspection or exact one-page cheat-sheet fit.
- Browser checks at widths 1440 and 390 found no page-level horizontal overflow; every new chapter has five callout types and no zero-width/missing diagram. New diagram counts: 08=7, 09=6, 10=6, 11=14, 12=14. The local reader continues to scroll oversized diagrams rather than shrink labels illegibly.
- ViewStateLab: five actual PASS checks on v24.20.0, scoped to a pure state model. CapacityLab: four actual PASS checks on v24.20.0, scoped to hypothetical arithmetic. Neither validates framework/browser/network behavior.
- LldLab compiler preflight: NOT EXECUTED, no existing JDK. Java source diagnostics had no reported errors but do not establish compilation. All twelve Java implementations and tests remain unexecuted. No unauthorized downloads, installations, commits or IntegrationHub-lite work.

### Remaining Limits

- This request's next five chapters, 08-12, are written, source-reviewed and published. Next chapter is 13; this batch does not proceed beyond 12.
- Previously recorded Chapter 05/06 accuracy/coverage work, Chapter 06 SQL workbook/Redis detail, syntax highlighting and application-runtime validation remain outstanding and were not silently declared fixed.
- Security/regulatory/provider-specific checks, framework/browser integration, distributed failure/load tests and all Java compilation require appropriate environments. Publication PASS and model PASS have deliberately narrower meanings.

## 2026-10-06 / Chapters 13-17 Batch / Chapter 13

- Wrote src/13-integration.md: 4,541 words including fences / 4,172 prose words, six diagrams and eleven mapped terms. All five callouts; twelve interview answers. Reviewed input/mapping identity, timestamp ties, deletion capture, partial acceptance, checkpoint ordering, quarantine and vendor responsibility boundaries.
- Added IntegrationLab.py with four standard-library unittest cases and no-download run.ps1. Preflight found only a Windows Store Python alias, so actual status is NOT EXECUTED. No Python assertions, target writes or vendor operations ran.
- HTML build and publication test passed: 120/120 diagrams, 3,121 IDs, 1,518 internal links, zero broken. Tool availability check found no Docker, kubectl, Terraform, AWS CLI, javac, Maven or Gradle. No account/cluster access or installation was attempted.

### Verification Items: 5

1. **[VERIFY: confirm each source's cursor lifetime, snapshot consistency, timestamp/commit ordering, deletion feed and rate-limit contract against the selected connector/API version. No real pagination, CDC or cursor-expiry experiment ran.]**
2. **[VERIFY: confirm current Informatica IICS/IDMC naming, Secure Agent/runtime choices, connector operation support, pushdown, taskflow/restart semantics, licensing and network requirements in the vendor documentation for the selected service. No Informatica environment was accessed.]**
3. **[VERIFY: check Mule runtime/DataWeave versions, streaming/repeatability, transaction boundaries, connector retry/error semantics and deployment options against the chosen MuleSoft release. No Mule flow, transformation or connector was executed.]**
4. **[VERIFY: verify Anaplan Connect authentication/actions/file limits/result semantics and Power BI semantic-model storage modes, gateway/refresh/RLS behavior, quotas and licensing in current product documentation. No tenant, workspace, report or import operation was accessed.]**
5. **[VERIFY: run the four Python standard-library tests with an existing compatible interpreter and validate real source/target adapters separately. The current preflight is NOT EXECUTED; in-memory state replacement and input hashing are not persistence, authenticity or vendor-runtime evidence.]**

All five remain open. Source marker total through Chapter 13: 99. Earlier whole-guide coverage gaps remain unchanged.

## 2026-10-06 / Chapters 13-17 Batch / Chapter 14

- Wrote src/14-docker.md: 3,837 words including fences / 3,566 prose words; three diagrams, all five callouts and nine mapped terms. Reviewed namespaces versus cgroups, kernel/VM boundaries, image context/history, tag/digest trust, PID 1, signals, networking and full JVM memory accounting.
- Added ProbeServer.java, Dockerfile, .dockerignore, compose.json and a preflight runner. Explicit base-image arguments avoid silently selecting/downloading a base. Compose uses pull_policy never, loopback binding, non-root/read-only runtime and reduced capabilities. Four file-contract checks passed; Java/Docker build/runtime remain NOT EXECUTED.
- First build exposed a semicolon in a Mermaid sequence note being interpreted as a statement separator. Reworded the note and reran the build successfully. Publication test passed: 123/123 diagrams, 3,219 IDs, 1,563 links, zero broken. No tooling replacement or dependency download.

### Verification Items: 4

1. **[VERIFY: confirm namespace/user-namespace/rootless behavior, runtime privileges and Docker Desktop backend details for the actual OS/engine version. No namespace, host-mount or daemon inspection ran.]**
2. **[VERIFY: verify cgroup v1/v2 CPU, memory and swap accounting, JDK container awareness and collector/native headroom against the selected runtime and limits. No resource-limit or OOM/throttling experiment was executed.]**
3. **[VERIFY: build the recipe with explicitly approved compatible local base images, confirm module availability, target architecture, permissions and image scanning, and test the actual Java process. Dockerfile text checks are not Docker parser/build/runtime evidence.]**
4. **[VERIFY: confirm engine/Compose network isolation, DNS, port binding, rootless networking and volume permissions for the selected OS/backend. No container socket, network or storage experiment ran.]**

All four remain open. Source marker total through Chapter 14: 103.

## 2026-10-06 / Chapters 13-17 Batch / Chapter 15

- Wrote src/15-kubernetes.md: 4,336 words including fences / 4,006 prose words; five diagrams, all five callouts and 24 mapped terms. Reviewed controller ownership, Pod replacement, Service data paths, startup/readiness/liveness, resource loops, PDB scope, storage and mesh/application identity boundaries.
- Added workload.json and a file-only runner. Five static checks passed: parsing, selectors/named ports, HPA target, local-image/token policy and probe routes. No API schema/admission validation, apply, scheduling or runtime tests. kubectl is absent.
- Build and publication test passed: 128/128 diagrams, 3,358 IDs, 1,607 links, zero broken. Troubleshooting includes Pending, ImagePullBackOff, CrashLoopBackOff, readiness failure, OOMKilled and Running-but-not-serving, with evidence-first repair directions.

### Verification Items: 6

1. **[VERIFY: confirm Kubernetes API support, CNI/Service routing, NetworkPolicy enforcement, ingress/Gateway controller behavior and load-balancer/TLS integration for the chosen cluster. No network policy, ingress or packet path was tested.]**
2. **[VERIFY: verify Secret at-rest/access configuration, ConfigMap/Secret volume propagation, subpath behavior, application reload and external-secret rotation for the selected cluster/runtime. No configuration or secret rollout ran.]**
3. **[VERIFY: confirm probe timing, startup gating, endpoint propagation, termination behavior and Spring Boot health-group configuration against the selected versions. The fixture's routes are statically matched; no kubelet probe or shutdown test ran.]**
4. **[VERIFY: verify requests/limits enforcement, metrics availability, HPA stabilization, VPA modes and in-place resizing support for the actual Kubernetes/components/JDK combination. No autoscaling, resource-pressure or OOM experiment ran.]**
5. **[VERIFY: confirm CSI/StorageClass topology, access modes, reclaim/retention policy, snapshot consistency and StatefulSet update/recovery behavior against the selected storage and operator. No volume, database operator or restore was exercised.]**
6. **[VERIFY: confirm RBAC/admission enforcement and the chosen Istio/Linkerd data-plane architecture, mTLS identity, traffic split, retry, probe and shutdown behavior for installed versions. No mesh or RBAC authorization test ran.]**

All six remain open. Source marker total through Chapter 15: 109.

## 2026-10-06 / Chapters 13-17 Batch / Chapter 16

- Wrote src/16-delivery.md: 4,323 words including fences / 4,053 prose words; four diagrams, all five callouts, nineteen mapped terms and twelve interview answers. Reviewed build identity, Maven phases/Gradle tasks, wrapper/offline limits, agent/credential trust, desired-state ownership, rollout/flag separation and schema-compatible recovery.
- Added a non-deploying Jenkinsfile reusing Chapter 14's probe plus PromotionLab.mjs/run.ps1. Four synthetic promotion checks actually passed on v24.20.0: matching intent, stale approval, failed evidence/mutable identity and incompatible rollback. No real artifact/signature/approval evidence is implied by the model.
- Jenkins, Java, Maven/Gradle, registry, GitOps and deployment remain NOT EXECUTED. HTML build and publication tests passed: 132/132 diagrams, 3,483 IDs, 1,648 links, zero broken.

### Verification Items: 5

1. **[VERIFY: confirm Maven/Gradle plugin lifecycle, toolchain, dependency locking/verification, wrapper distribution and test-task behavior against the actual project and versions. Neither build tool nor its dependency cache is available here; no Maven/Gradle build ran.]**
2. **[VERIFY: validate Jenkins declarative syntax, plugin/shared-library versions, agent isolation, credential binding and SCM/approval permissions in the selected controller. The saved Jenkinsfile is source-reviewed only; no Jenkins parser or agent executed it.]**
3. **[VERIFY: confirm ArgoCD sync/prune/health/rollback behavior and Harness product/delegate/connector/verification capabilities against deployed versions and policies. No GitOps controller or Harness pipeline was contacted or executed.]**
4. **[VERIFY: validate LaunchDarkly/selected SDK evaluation, offline/default behavior, targeting data, credential scope and event/privacy settings for the actual environment. No SDK, flag service or cohort experiment ran.]**
5. **[VERIFY: execute the saved Jenkinsfile on an approved isolated controller/agent and replace synthetic promotion evidence with validated artifact, test, approval and schema records. The local JavaScript PASS is not pipeline syntax, build, signature, deployment or rollback evidence.]**

All five remain open at their actual implementation boundary. Source marker total through Chapter 16: 114.

## 2026-10-06 / Chapters 13-17 Batch / Chapter 17 and Final Gates

- Wrote src/17-cloud.md: final 4,516 words including fences / 4,116 prose words; six diagrams, all five callouts and fifteen mapped terms. Reviewed account/region/AZ boundaries, routes versus permissions, managed compute responsibilities, S3 versions, RDS topology distinctions, IAM trust/policy, SQS/SNS, Lambda lifecycle and Terraform state.
- Added main.tf.json and a file-only preflight. Five checks passed for JSON/deletion guards/public-access blocks/versioning/encryption declarations. Terraform syntax/provider validation, init/plan/apply and all AWS operations remain NOT EXECUTED. No credentials, billable resources or provider downloads were used.

### Verification Items: 6

1. **[VERIFY: confirm AWS regional/AZ availability, VPC routing, security-group/NACL/endpoint behavior, quotas and cost/availability implications for the selected services. No AWS network or account configuration was inspected or changed.]**
2. **[VERIFY: confirm S3 consistency, versioning, notification and replication behavior and RDS engine-specific Multi-AZ, replica, failover, backup/PITR and connection semantics for the selected deployment. No storage, failover or restore operation ran.]**
3. **[VERIFY: verify IAM role trust, policy evaluation, permission boundaries/organization controls, EC2 metadata protections and EKS/Lambda workload-identity configuration against the actual account and service. No credentials or IAM APIs were accessed.]**
4. **[VERIFY: confirm SQS standard/FIFO ordering, deduplication windows, visibility limits, DLQ/redrive and SNS subscription delivery behavior, including Lambda partial-batch handling, for the selected configurations. No queue, notification or event-source mapping was created or tested.]**
5. **[VERIFY: confirm Lambda runtime support, initialization/cold-start options, timeout/concurrency limits, VPC networking, temporary storage and retry/event-source semantics for the selected region/configuration. No function or latency benchmark was deployed or measured.]**
6. **[VERIFY: validate native Terraform JSON, provider 5.x resource schemas, state/backend locking, plan/apply identity and deletion/encryption/access semantics using approved installed tools and a sandbox account. No provider download, Terraform validation or AWS API operation ran.]**

All six remain open. Total source markers: 120, with 26 added in this batch (13=5, 14=4, 15=6, 16=5, 17=6).

### Final Batch Review

- Completed exactly Chapters 13-17. Final words including fences: 13=4,541; 14=3,837; 15=4,336; 16=4,330; 17=4,516. Combined 21,560 words / 19,920 excluding fences. Twenty-four new diagrams and 78 new term mappings. Earlier Chapter 16 metrics precede a small wrapping correction.
- Extended verify.mjs to require all five callout types and full term mappings through 17; inspect the Python test declarations/status, static reports/artifacts for Docker/Kubernetes/Terraform, exact inline Dockerfile synchronization, native JSON relationships, the non-deploying Jenkinsfile and the actual PromotionLab report.
- Source review also added cleanup of the Jenkins pipeline-owned out/ directory before compilation, preventing stale class files from being archived on a reused agent. Jenkins remains unexecuted.
- Chapter 14's Mermaid note failed because a semicolon split a statement; reworded and rebuilt successfully. Final mobile audit found 8px overflow from two long slash-separated text runs in Chapters 16/17. Text-range measurements identified them; comma-separated prose fixed wrapping without CSS changes. Both corrections were followed by fresh builds and checks.

### Actual Final Validation

- Per-chapter HTML builds and publication tests passed. Final full build.ps1 plus build.ps1 -Test: PASS. Eighteen chapters available, ten planned; 138 sources/138 inline SVGs, 3,638 IDs, 1,696 internal links, zero broken.
- Final PDF: 7,254,765 bytes, %PDF-1.4 header, 531 page objects, 3,492 link annotations and valid EOF. This is structural inspection, not exhaustive pagination or printed-link review.
- Browser audit after the final rebuild: no page-level horizontal overflow at 1440px or 390px, all 24 new diagrams present/nonzero width, all five callout types in each new chapter. Representative mobile screenshot of diagram 15.3 confirmed readable probe text with local scrolling. Client width, rather than innerWidth, was used to account for scrollbar space.
- Local file checks passed: Docker four, Kubernetes five, Terraform five. These inspect declared properties and do not establish tool schema, daemon/API admission, runtime or security behavior.
- PromotionLab: four actual PASS checks on v24.20.0. Python preflight: NOT EXECUTED, only store alias available. Java, Docker, Kubernetes, Jenkins, Terraform/AWS and vendor integration execution remain NOT EXECUTED. No unauthorized downloads, cluster/account operations, commits or IntegrationHub-lite work.
- Editor diagnostics reported no errors for checked new chapter sources and verifier; this is not a compiler/provider result.

### Remaining Work

- The requested next-five batch ends at 17. Next chapter is 18 Observability, Reliability and Operations; future writing sequence is 18, 19, 21, 22, 23, 25, 26, 27, then 24 Part A and 20 last.
- Existing Chapter 05/06 accuracy/coverage work, Chapter 06 SQL workbook/Redis detail and syntax highlighting remain outstanding. No claim that these were repaired in this batch.
- Provider/platform-specific guarantees and all unavailable runtime tests remain marked for verification. Static file checks and publication PASS are intentionally narrower evidence.

## 2026-10-06 / Chapters 18-23 Batch / Chapter 18

- Wrote src/18-operations.md: 4,568 words including fences / 4,233 prose words; five diagrams, all five callouts, 22 mapped terms and twelve interview answers. Reviewed outcome SLIs, asynchronous completion, propagation/sampling, histogram aggregation, telemetry cardinality, incident roles, evidence-first playbooks, load-model limits and restore reconciliation.
- SloLab.mjs/run.ps1 executed on v24.20.0: five PASS checks for budget arithmetic, no-traffic unknown state, dual-window condition, cardinality and invalid input. No PromQL, telemetry backend, load/chaos test or restore was executed.
- HTML build and publication test passed: 143/143 diagrams, 3,775 IDs, 1,735 links, zero broken.

### Verification Items: 5

1. **[VERIFY: check OpenTelemetry SDK/agent, semantic conventions, propagation, sampling, Collector queue/retry behavior and Spring instrumentation compatibility for selected versions. No agent, collector or cross-process trace was executed.]**
2. **[VERIFY: verify Prometheus rate/reset/staleness semantics, classic/native histogram support, query aggregation and scrape/export configuration against the selected stack. SloLab checks arithmetic only; no PromQL or Prometheus rule evaluation ran.]**
3. **[VERIFY: confirm Grafana data-source/query behavior and Loki label, ingestion, retention and access policies for the deployed versions. No dashboard, log backend or telemetry retention policy was exercised.]**
4. **[VERIFY: select load-generator arrival semantics, latency recording, telemetry sampling and fault-injection controls for the actual environment. No load, chaos, saturation or production fault experiment was run; all suggested experiments require explicit scoped authorization.]**
5. **[VERIFY: verify backup/PITR integrity, key availability, regional promotion/fencing, replay/reconciliation and measured RPO/RTO with the selected engines/providers. No backup, restore or failover rehearsal ran.]**

All five remain open for their stated deployment/evidence boundary. Total markers through 18: 125.

## 2026-10-06 / Chapters 18-23 Batch / Chapter 19

- Wrote src/19-testing.md: 4,012 words including fences / 3,767 prose words; four diagrams, all five callouts, nine mapped terms and twelve interview answers. Reviewed test boundary meaning, discovery, mocks/fakes, Spring rollback effects, real dependency tests, browser isolation, concurrency and flaky-test policy.
- Added standalone POM, JobService.java and JobServiceTest.java with four methods/five planned Jupiter cases and Mockito collaborators. Maven runner is offline and Surefire rejects zero discovery. POM/source checks passed; no JDK/Maven, dependency resolution or Java assertions ran. Overall NOT EXECUTED.
- HTML build and publication test passed: 147/147 diagrams, 3,893 IDs, 1,767 links, zero broken.

### Verification Items: 4

1. **[VERIFY: resolve and execute the pinned standalone JUnit/Mockito/plugin versions on Java 21, and separately verify the actual Boot-managed test stack, discovery and lifecycle behavior. The POM was parsed only; no JUnit engine or compiler ran.]**
2. **[VERIFY: check Spring Boot test-slice packages/imports, security inclusion, database replacement, transaction defaults and context caching for the selected Boot generation. No Spring slice or full-context test ran; older tutorial imports may not match Boot 4.]**
3. **[VERIFY: validate Testcontainers version, runtime/image availability, readiness strategy, lifecycle cleanup and production-engine compatibility before execution. No images were pulled, containers started or database/broker tests run.]**
4. **[VERIFY: check Playwright/browser versions, locator/assertion behavior, authentication-state handling and test-environment routing before running business journeys. Reader publication browser checks are separate; no IntegrationHub end-to-end application was deployed or tested.]**

All four remain open. Total markers through 19: 129. Chapter 20 remains reserved until the end.

## 2026-10-06 / Chapters 18-23 Batch / Chapter 21

- Wrote src/21-network-os.md: 5,019 words including fences / 4,530 prose words; seven diagrams, all five callouts, 37 mapped terms and twelve interview answers. Reviewed DNS/pool lifetime, TCP byte framing, flow/congestion control, HTTP multiplexing, TLS boundaries, NAT, OS memory/descriptors and readiness/Netty ownership.
- StreamLab.mjs/run.ps1 actually passed three checks on v24.20.0: split UTF-8 decoding, line framing across chunks and loopback HTTP assembly. Temporary server bound 127.0.0.1 on an ephemeral port and closed afterward. No external host, packet capture, DNS, TLS or Java NIO test.
- First build exposed Mermaid's reserved loop keyword used as a participant ID. Renamed only the internal ID to EventThread, rebuilt and passed publication tests: 154/154 diagrams, 4,059 IDs, 1,812 links, zero broken. Linux diagnostic tools are described, not executed.

### Verification Items: 6

1. **[VERIFY: verify JVM positive/negative DNS cache security properties, resolver behavior, client address selection and pool refresh against the selected JDK/OS/client. No DNS failover or cache experiment ran.]**
2. **[VERIFY: confirm TCP congestion algorithm, keepalive, retransmission, TIME_WAIT and socket-buffer behavior for the actual kernel/network path. The loopback lab is not a packet trace or congestion test.]**
3. **[VERIFY: confirm HTTP/2 and HTTP/3 support, QUIC/UDP reachability, flow-control and proxy translation behavior for the deployed clients and edge. No protocol negotiation or packet capture ran.]**
4. **[VERIFY: confirm TLS 1.2/1.3 negotiation, resumption/early-data policy, certificate verification, SNI and mTLS behavior for the actual JVM, browser and proxy. No handshake timing or certificate-validation experiment ran.]**
5. **[VERIFY: confirm Linux virtual/resident memory accounting, page-cache/writeback, descriptor limits and cgroup behavior for the actual kernel/container runtime. No Linux resource or descriptor experiment ran in this Windows workspace.]**
6. **[VERIFY: verify Java NIO selector/channel semantics, Netty event-loop and buffer ownership, epoll modes and zero-copy/TLS support against the selected OS/JDK/library. No Java NIO, Netty or zero-copy benchmark ran.]**

All six remain open beyond the stated local stream scope. Total source markers through 21: 135.

## 2026-10-06 / Chapters 18-23 Batch / Chapter 22

- Wrote src/22-distributed.md: 5,340 words including fences / 4,954 prose words; six diagrams, all five callouts, 31 mapped terms and twelve interview answers. Reviewed safety/liveness, clock ordering, Paxos/Raft boundaries, leadership versus effects, quorum caveats, consistency, repair and transaction coordination.
- Consulted Redis's distributed-lock documentation, Kleppmann's 2016 critique and Sanfilippo's response. The chapter distinguishes elapsed acquisition-time checks from post-expiry effect safety and records the current Redis fencing disclaimer. No quoted benchmark or historical implementation detail is presented as a current local result.
- OrderingLab.mjs/run.ps1 actually passed four local checks on v24.20.0. A higher epoch must reach the effect resource before the model rejects an old token; the model is not a lease, consensus implementation or durable register.
- HTML build and publication tests passed: 160/160 diagrams, 4,206 IDs, 1,848 links, zero broken.

### Verification Items: 5

1. **[VERIFY: verify physical/monotonic clock, drift/offset bounds and the selected HLC/vector metadata policy in the actual system. The local lab checks Lamport receive and vector comparison only; no clock synchronization or lease-timing experiment ran.]**
2. **[VERIFY: verify Raft/Paxos implementation persistence, read semantics, membership change and crash-recovery assumptions against the selected proven service and primary algorithm references. No consensus algorithm or cluster was implemented or tested here.]**
3. **[VERIFY: check etcd/ZooKeeper lease/session/watch behavior, Kubernetes leader-election client semantics, database lock scope and usable fencing revisions for the selected versions. No election, disconnect, expired lease or singleton scheduling experiment ran.]**
4. **[VERIFY: Redlock is contested. Consult the Redis distributed-lock specification, Kleppmann's analysis and Sanfilippo's response, and validate the exact client/server timing, persistence, renewal and effect-side fencing assumptions. All three sources were consulted; no Redis/Redlock or fault-injection experiment was executed.]**
5. **[VERIFY: validate read/write quorum membership, durability, conflict resolution, repair, tombstones and failover against the selected storage implementation. R+W>N alone is not an asserted product consistency guarantee; no replicated-store experiment ran.]**

All five remain open at the distributed implementation boundary. Total source markers through 22: 140.

## 2026-10-06 / Chapters 18-23 Batch / Chapter 23 and Final Gates

- Wrote src/23-jvm-performance.md: 4,517 words including fences / 4,219 prose words; five diagrams, all five callouts, 21 mapped terms and twelve interview answers. Reviewed CPU versus elapsed/allocation/retained evidence, flame-graph units, root paths, thread states, collector trade-offs, container headroom, JIT history and benchmark scope.
- Added bounded self-generated ProfilingLab.java, JMH SumBenchmark.java, POM and offline runner. POM/source preflight passed; no JDK/Maven execution, recording, dump, profile or benchmark output. The optional benchmark prerequisite is checked before any JFR work so unavailable Maven does not leave a partially executed request with stale reporting.

### Verification Items: 7

1. **[VERIFY: verify JFR event names/defaults, sampling/stack settings, recording bounds, JMC compatibility and attach permissions for the selected JDK/OS. No JFR recording or JMC analysis ran locally.]**
2. **[VERIFY: confirm async-profiler release, supported event modes, native stack visibility, kernel permissions and interpretation of flame-graph units on the actual host. No profiler was installed, attached or executed.]**
3. **[VERIFY: confirm heap-dump capture semantics, pause/disk impact, MAT retained-size interpretation and sensitive-data handling for the actual JVM and environment. No heap dump or leak analysis was performed.]**
4. **[VERIFY: verify G1/ZGC mode availability and defaults, GC/safepoint log syntax, pause-target semantics and effective flags for the selected JDK. Java 21 and later ZGC generations differ; no collector comparison or GC-log experiment ran.]**
5. **[VERIFY: verify container processor/memory detection, MaxRAMPercentage and explicit heap interaction, native-memory visibility, virtual-thread diagnostics and pinning behavior against the actual JDK/cgroup version. No container or thread-memory experiment ran.]**
6. **[VERIFY: resolve JMH 1.37 and its annotation processor/shaded harness, verify benchmark discovery and run representative trials on an approved JDK/host. The fixture was not compiled or run; no benchmark ranking or timing is claimed.]**
7. **[VERIFY: execute the self-generated JFR smoke fixture and optional offline JMH project with the approved toolchain, inspect recording event coverage and actual benchmark output, and record the environment before drawing performance conclusions. No recording, dump, profile or benchmark output exists from this session.]**

All seven remain open. Total source markers: 147; this batch adds 27 (18=5, 19=4, 21=6, 22=5, 23=7).

### Final Coverage and Evidence

- Completed precisely the requested next five: 18, 19, 21, 22, 23. Chapter 20 remains the final appendix and 24 the final narrative. Final combined new source length is 23,456 words including fences / 21,703 excluding fences, with 27 diagrams and 120 new term mappings.
- Expanded verify.mjs to require the five callout types/full term mapping in all five chapters, Java source/report mappings for 19/23, offline Maven command sites, nonzero-discovery policy and test names in the JUnit fixture, JMH annotations, and actual model reports for 18/21/22.
- Three executed JS labs: SloLab five checks, StreamLab three, OrderingLab four. All PASS on v24.20.0. Only StreamLab creates a temporary local HTTP server, bound to loopback and closed in finally; no external network test. Models do not validate Prometheus, Raft, Redlock, Java NIO or production behavior.
- Chapter 21's reserved Mermaid Loop actor name caused a parse failure; changed the internal identifier to EventThread, kept the visible label and reran the build successfully. No renderer replacement or dependency download.
- Final build.ps1 and build.ps1 -Test: PASS. Twenty-three available chapters, five planned; 165 Mermaid sources/165 inline SVGs, 4,347 IDs, 1,883 internal links, zero broken.
- Final PDF: 8,191,410 bytes, %PDF-1.4 header, 627 page objects, 3,769 link annotations, valid EOF. This does not assert exhaustive printed-page/link inspection or one-page cheat-sheet fit.
- Browser at 1440px and 390px: no document-level horizontal overflow; all new diagrams nonzero/present and all five callout types per chapter. Representative mobile screenshot of diagram 21.7 confirms readable labels and intentional local horizontal scrolling.
- Editor diagnostics reported no errors in checked chapters, verifier and Java fixture files; that is not Java compiler success. No unauthorized downloads, privileged attachments, chaos actions, cluster/account changes or IntegrationHub-lite work.

### Remaining Work

- Five chapters remain: 25 Architecture, 26 Data Platform, 27 Payments, then 24 Part A Capstone and 20 Toolkit/Index last. This next-five request stops after 23.
- Preserve prior Chapter 05/06 accuracy/coverage work, Chapter 06 SQL workbook/Redis detail and syntax-highlighting limitation. They were not silently declared fixed.
- All Java compilation/JUnit/JFR/JMH, Linux diagnostic, protocol, telemetry-backend, distributed-fault and production performance validation remains unexecuted unless explicitly listed above. Primary Redlock sources were read, not experimentally verified.

## 2026-10-06 / Final Five / Chapter 25

- Resumed the already-saved Chapter 25 and reviewed it before final publication. Final source: 6,084 words including fences, 5,620 excluding fences; four diagrams, nineteen mapped terms and nine verification items.
- Corrected absolute claims about all method calls becoming remote, context cycles preventing every independent deployment, one aggregate per transaction, event visibility before commit, and a Spring-free application layer while using @Transactional. The strict rule applies to the illustrated domain; extraction still requires remote-contract and data-migration work.
- ArchitectureLab.mjs rerun: five PASS checks on v24.20.0. Declared graph checks and in-memory aggregate tests are not a Java package scan, Spring application or production architecture validation. Java structural illustration remains NOT EXECUTED.

### Verification Items: 9

1. [VERIFY: current ArchUnit and Spring Modulith capabilities and versions at https://www.archunit.org/ and https://spring.io/projects/spring-modulith]
2. [VERIFY: original formulation and current guidance at https://martinfowler.com/bliki/StranglerFigApplication.html]
3. [VERIFY: terminology and canonical definitions against Evans' and Vernon's published material and https://martinfowler.com/tags/domain%20driven%20design.html]
4. [VERIFY: Conway's law statement and the "inverse Conway" practice as described at https://martinfowler.com/bliki/ConwaysLaw.html]
5. [VERIFY: original description at https://alistair.cockburn.us/hexagonal-architecture/]
6. [VERIFY: layer naming and the dependency rule as published at https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html]
7. [VERIFY: current OpenAPI specification version and tooling at https://spec.openapis.org/ and schema-registry behavior for your broker]
8. [VERIFY: template variants and current practice at https://adr.github.io/]
9. [VERIFY: fitness function terminology and current guidance at https://www.thoughtworks.com/insights/topic/evolutionary-architecture]

These remain verification destinations, not newly completed tool or runtime checks in this continuation.

## 2026-10-06 / Final Five / Chapter 26

- Wrote src/26-data-platform.md: 4,026 words including fences / 3,791 excluding fences; three diagrams, all five callout types, nine mapped terms and twelve interview answers.
- Reviewed OLTP/OLAP interference, storage versus table-format responsibilities, ETL/ELT versus cadence, CDC snapshot and commit gaps, replacement versus additive events, fact grain, historical dimensions, late data, backfills, quality gates, lineage and tenant access.
- WarehouseLab.mjs rerun: five PASS checks. Explicit limitations include no durable checkpoint/concurrency, no historical interval validation, simple separator keys and no conflicting-content detection for repeated facts. No SQL, CDC, warehouse or BI execution.
- First build rejected noncanonical cross-file subanchors. Corrected links to canonical chapter anchors; fresh HTML build and publication gate then passed. No publisher relaxation was needed.

### Verification Items: 5

1. [VERIFY: check the selected warehouse, object store, table format and query engine for snapshot isolation, concurrent writes, schema evolution, delete support and catalog interoperability. No warehouse or lakehouse implementation was executed.]
2. [VERIFY: confirm Debezium and the selected database connector's consistent snapshot, source-position, transaction metadata, tombstone, schema-history and recovery semantics for the deployed versions. No CDC snapshot, log-retention or restart test ran.]
3. [VERIFY: validate merge/upsert atomicity, unique-key enforcement, interval checks and partition publication with the chosen analytical engine. The in-memory fixture does not execute SQL or enforce concurrent historical intervals.]
4. [VERIFY: validate the chosen orchestration and data-quality tooling's retry, backfill, dependency, alerting and atomic-publication behavior in a sandbox. No scheduler, quality framework or BI freshness alert was executed.]
5. [VERIFY: confirm retention, erasure, audit and cross-border data obligations with the responsible privacy/legal team and current jurisdiction-specific guidance. Validate access enforcement across landing, curated tables and BI exports; no legal determination or live access audit was performed.]

All five remain open at the named implementation boundary.

## 2026-10-06 / Final Five / Chapter 27

- Wrote src/27-payments.md: 5,630 words including fences / 5,162 excluding fences; seven diagrams, nineteen mapped terms, all callouts, sixteen interview answers and three HLD studies: wallet, gateway integration and ledger. Each study has requirements, assumptions, API/model, architecture, flow/failures, use/avoid criteria and unexecuted acceptance checks.
- Reviewed lifecycle dimensions, debit-positive examples versus the fixture's arithmetic convention, per-currency balance, spend serialization, provider identity, unknown outcomes, webhook races, independent reconciliation, fraud policy and compliance boundaries. Regulatory and payment-network descriptions carry verification markers.
- Repaired the existing payment model before relying on it: capture and refund keys now bind amount; duplicate refunds replay the original result even after later transitions; invalid operation amounts and payment identity overwrite are rejected. Posting references are checked and input lines copied. Eight actual check groups PASS on v24.20.0.
- Remaining model limits are explicit: mutable in-memory storage, no tenant/auth boundary, no durable transaction, no concurrent spend, simplified lifecycle, and no general arbitrary-precision balance engine. No payment, SQL, provider, webhook or compliance runtime was exercised.

### Verification Items: 10

1. [VERIFY: confirm authorization, capture, clearing, settlement, void, refund, payout and chargeback semantics, supported partial operations and deadlines against the selected acquirer/provider and current card-network rules. No network flow was executed.]
2. [VERIFY: confirm current ISO 4217 currency metadata and provider-specific amount, rounding, zero-decimal and special-unit conventions before integrating. The lab uses synthetic EUR minor-unit integers and does not validate a currency registry or provider amount format.]
3. [VERIFY: validate account constraints, posting atomicity, balance serialization, unique receipt scope, outbox commit and failover durability with the chosen database and accounting design. The JavaScript model has no SQL transaction, crash durability or concurrent spend test.]
4. [VERIFY: confirm provider idempotency scope, key retention, content-conflict behavior, concurrent-request handling, status lookup and retry guidance for every integrated operation. No external idempotency or gateway failover was tested.]
5. [VERIFY: verify webhook signature/certificate validation, signed payload format, timestamp tolerance, event identifiers, delivery ordering, redelivery and authoritative status semantics in the selected provider documentation. No webhook endpoint or provider signature was executed.]
6. [VERIFY: confirm settlement and payout report schemas, fee/netting rules, currencies, business-day cutoffs, report revisions and authoritative matching references with the provider and finance team. No settlement file, bank statement or close process was processed.]
7. [VERIFY: establish PCI DSS applicability, current version, assessment scope, hosted-payment integration responsibilities and storage prohibitions with the current PCI SSC documents, acquirer and qualified assessor. No compliance certification or scope determination is claimed.]
8. [VERIFY: confirm KYC, AML, sanctions-screening, reporting, retention and approval obligations with qualified compliance/legal owners for the actual jurisdiction, entity and product. No regulatory control or screening provider was validated.]
9. [VERIFY: confirm current network participant roles, issuer/acquirer flows, 3-D Secure responsibilities, dispute processes and regional variations against selected network, EMVCo and provider documentation. This is a conceptual overview, not a certified integration.]
10. [VERIFY: confirm current UPI participant roles, permitted flows, status/reversal/dispute handling, limits, authentication and integration obligations using NPCI, RBI and the contracted PSP/bank documentation. No UPI integration, limits or regulatory status was tested.]

All ten remain open; no compliance or network certification is implied.

## 2026-10-06 / Final Five / Chapter 24 Part A

- Wrote src/24-capstone.md after 27: 3,666 words including fences / 3,057 excluding fences, four diagrams and eight mapped terms. The day-in-life sequence, deploy story, five structured failure stories, conditional 10x scale order, refresh-token containment, whiteboard, timed spoken script and technology poster link the earlier mechanisms.
- Corrected the sequence to match the frozen baseline: gateway/BFF retains tokens, browser receives a secure HttpOnly session, state-changing cookie requests have CSRF controls, and SSE does not carry bearer tokens in URLs. Provisioning mutation goes through the identity ownership boundary.
- Part A only. No IntegrationHub-lite files, integrated deployment, fault drill or identity-provider experiment. Canary routing is an explicit additional mechanism, not an assumed Deployment feature. Speaking time is a rehearsal allocation, not a measured result.

### Verification Items: 2

1. [VERIFY: validate the actual Jenkins, ArgoCD, rollout-controller/routing, Kubernetes probe and schema-compatibility configuration before performing this deployment story. No integrated build, rollout, canary or rollback was executed.]
2. [VERIFY: verify the selected issuer's refresh rotation, reuse detection, concurrent refresh handling, family revocation and access-token containment behavior against current OAuth security guidance and provider configuration. This security story was not exercised against an identity provider.]

Both remain open at the integrated-system boundary.

## 2026-10-06 / Final Five / Chapter 20 Last

- Wrote src/20-toolkit.md last: decision tables with use/avoid criteria, a 30-day route covering every chapter, a glossary of high-risk distinctions, evidence discipline and the catalog-derived global index. One diagram and four mapped terms. Built length is 8,226 words including the generated 474-term index, not 8,226 words of new explanatory prose.
- Added globalConceptIndex(catalog) to the publisher and a single source placeholder. Index links retain each owner's exact spelling and section anchor. The focused test caught the case-folded collision of GoF State and React state; preserving owner-specific terms fixed it. Both owning explanations and both virtual-thread owners are checked.
- Added Chapter 00 term mappings to existing explanation anchors. The appendix's emitted links cover all 28 chapters. Source chapter markers and generated index links are tested separately; no stale placeholder is permitted in published HTML.

### Verification Items: 1

1. [VERIFY: recheck version-sensitive APIs, provider/network contracts, regulatory claims and every remaining chapter verification item against current primary documentation and an approved execution environment. This appendix does not close the guide's outstanding verification or editorial gaps.]

### Final Batch Gates

- All 28 chapter sources are available, with capstone before appendix. All 184 Mermaid sources render; latest HTML has 4,977 anchors, 3,320 internal links and zero broken links. Final PDF/browser results are recorded below after execution.
- verify.mjs now enforces callout/term coverage for all five final chapters, the three model reports (5/5/8 checks), three payment HLD structures, all capstone story anchors and five failure paths, BFF session presence, generated index coverage and actual appendix destination links.
- This batch adds 27 verification markers (25=9, 26=5, 27=10, 24=2, 20=1), bringing the source total to 174. Model checks are not runtime proof for Java, providers or infrastructure.
- Preserve Chapter 05/06 final accuracy/coverage work, Chapter 06 SQL workbook and expanded Redis patterns, syntax-highlighting limitation and all previously unexecuted external/runtime checks. Chapter availability is not a claim of complete editorial or experimental verification.

### Final Executed Publication Results

- Final full build and subsequent publication gate: PASS. Twenty-eight chapters, none planned, 184/184 diagrams, 4,977 IDs, 3,320 internal links and zero broken links. Final-five built length: 27,632 words including diagram/code fences and the generated appendix index; not all of that is new explanatory prose.
- PDF structural inspection: 10,310,450 bytes, %PDF-1.4, 725 page objects, 5,518 link annotations and valid EOF. No claim of exhaustive printed-page review or exact one-page fit for every cheat sheet.
- Browser audit at 1440px and 390px after reloading the persisted final HTML: zero document overflow, 28 chapter articles, 184 SVG diagrams and 474 appendix index rows. New-chapter diagram counts 25=4,26=3,27=7,24=4,20=1; none blank. All five callout types exist in each.
- Initial mobile audit found 114px overflow from raw verification URLs in Chapter 25. Text-range measurements isolated the prose; `.chapter p{overflow-wrap:anywhere}` removed overflow without changing natural diagram scale or local table scrolling. Rebuilt HTML/PDF and repeated browser checks. Representative mobile screenshot inspected at the capstone whiteboard.
- Three model reruns: architecture five, warehouse five, ledger eight check groups; all PASS on v24.20.0. Marker synchronization: 174 source markers, zero absent from this log. Editor diagnostics for checked sources, publisher, verifier and state/review files were clear.
- No unauthorized downloads, real payment traffic, account/cluster access, privileged process attachment, commits or Part B implementation. Earlier editorial gaps and unavailable runtime evidence remain explicit.