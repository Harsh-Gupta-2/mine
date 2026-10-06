# The Java Full-Stack Guide: Editorial and Build Plan

## Scope and Delivery Contract

Audience: a Java full-stack engineer with about three years of experience, preparing for backend, product-company, and fintech interviews. This is a mechanism-first reference, not a promise of an interview outcome. DSA, which some employers separately assess, is explicitly out of scope.

The authoritative requirements are in [PROMPT.md](PROMPT.md), preserved byte-for-byte from the supplied attachment. Step 0 writes Chapter 00 only, proves the publication pipeline, records limitations, and stops. Planned sections must never masquerade as completed chapters.

Recommended reading order:

**00, 01, 02, 21, 03, 04, 06, 05, 07, 26, 08, 09, 10, 25, 22, 11, 12, 27, 13, 14, 15, 16, 17, 18, 23, 19, 24, 20.**

Writing order for `next`: ascending numerical IDs, excluding 20 and 24. After 19, continue with 21, 22, 23, 25, 26, 27. `run all` completes those, then 24 Part A, then 20. The capstone is the final narrative chapter; the toolkit follows as an appendix. Part B is authorized only by `lite`.

## Chapter Outlines and Evidence

Every technical chapter uses: version assumptions; big-picture diagram; explainable outcomes; IntegrationHub placement; mechanisms with traces; code where useful; decisions with use/avoid criteria; traps; production failure cases; linked related chapters; one-page cheat sheet; interview corner with basic, internals, debugging, and scenario questions and model answers. The capstone links back without adding theory.

| ID | Chapter and teaching sequence | Principal evidence / exercise |
|---|---|---|
| 00 | How to use; system map; boundaries; dependency map; request journey; concept index | Trace an accepted sync through durable work and reconnecting UI |
| 01 | Core Java 8 through current LTS; JVM architecture; loading; memory/GC; collections; generics; exceptions; lambdas/Streams; records/sealed types/patterns; virtual threads | Runnable collection, type-safety, and resource-lifetime examples; distinguish Java 21 baseline from later changes |
| 02 | JMM; happens-before; visibility/atomicity; monitors/atomics/locks; executors; futures; concurrent collections; deadlocks; virtual threads | Deterministic tests of concurrency contracts; thread-pool rejection and starvation traces |
| 03 | IoC and lifecycle; proxies/AOP; auto-configuration; MVC; validation/config; transactions/security/Actuator; Cloud Gateway/Config/OpenFeign/discovery/load balancing | Follow one request and one proxy invocation; verify Boot/Cloud compatibility; identify Kubernetes-redundant components |
| 04 | Entity lifecycle; persistence context; dirty checking/flush; lazy loading; N+1; locking; caches; mapping; Spring Data | SQL-backed tests of fetch plans and optimistic conflict behavior |
| 05 | REST resources/contracts/errors/versioning/pagination; idempotency; rate limiting/webhooks; gRPC/GraphQL; polling/SSE/WebSocket; Reactor vs virtual threads | Reconnect/replay and slow-client traces; authentication without exposing bearer tokens in URLs |
| 06 | SQL workbook; indexes/EXPLAIN; isolation/MVCC/locks; storage/WAL/buffers/vacuum; PostgreSQL/MySQL; MongoDB/Redis/Elasticsearch; migrations/pools; replication/partitioning/PITR | Query fixtures, plans labeled with environment, lock experiments, expand/contract and restore drills |
| 07 | Kafka/RabbitMQ; delivery/order/DLQ; outbox/CDC; saga; idempotent consumers; event sourcing/CQRS; Streams; Spring Batch | Crash-at-each-boundary matrix; stateful stream topology; restartable chunk job |
| 08 | OAuth/PKCE/OIDC/JWT/SAML/SCIM; PKI/TLS/mTLS; secrets; API threats/CORS/CSRF; KMS/Vault/rotation; PII/tokenization/audits; GDPR/PCI | Login and refresh-token lifecycle; trust-boundary review; regulatory claims always tagged for verification |
| 09 | Browser/HTTP; JS/TS; React state/render/hooks/fetching; Angular overview; SPA authentication; performance; real-time UI | Cancellation, stale-response, tenant-switch, and SSE reconnection tests |
| 10 | Scaling/estimation; LB/CDN/cache; replication/shards; CAP/PACELC overview; queues; rate limits/gateway; Resilience4j; tenancy/regions | Explicit capacity assumptions and decision-driven interview framework |
| 11 | OOP/SOLID; relevant GoF patterns; twelve complete Java LLDs | Requirements, class diagrams, compiling code, concurrency contracts, and tests for all twelve |
| 12 | Twelve HLD cases with consistent sections | Requirements, assumptions/estimates, APIs, models, architecture, deep dive, trade-offs, failures, IntegrationHub mapping |
| 13 | ETL/ELT; batching/pagination/checkpoints; schema mapping; quarantine; IICS/MuleSoft/Anaplan Connect/Power BI; connector contracts | Restart a partial sync without skipping source records; Python used only where it clarifies a transform |
| 14 | Namespaces/cgroups/layers; Dockerfiles/multi-stage; networks/Compose; image security | Small image build and network/resource inspection, execution status recorded |
| 15 | Control plane/workloads; services/ingress; config/secrets; probes; autoscaling; rollout/RBAC/Helm/state; resources; troubleshooting; mesh | CrashLoop/ImagePull/Pending/readiness/OOM runbooks; Istio/Linkerd use/avoid decisions |
| 16 | Git; Maven/Gradle; Jenkins pipelines/libraries/agents/credentials; ArgoCD; Harness; rollout/rollback; LaunchDarkly; supply chain; Kanban | Trace immutable artifact promotion; keep schema and feature-flag rollback separate |
| 17 | AWS compute/VPC/S3/RDS/IAM/SQS/SNS/EKS/ALB/CloudWatch; Terraform; Lambda; API management | Least-privilege deployment sketch; cost/cold-start/operational boundaries without invented prices |
| 18 | Logs/metrics/traces; Prometheus/Grafana/Loki/OTel; SLOs; alerts/incidents/RCA; production playbooks; capacity/load/chaos; DR | Incident timeline, RPO/RTO assumptions, tested-restore criteria; deep profiling owned by 23 |
| 19 | Test pyramid; JUnit 5/Mockito; slices; Testcontainers; contracts; Playwright; performance; gates | Tests at ownership boundaries, not merely happy-path line coverage |
| 20 | Appendix, written last: decisions; 30-day revision; glossary; complete index | All chapters indexed after the capstone; no premature completeness claim |
| 21 | DNS/TCP/TLS/HTTP; L4/L7; pooling/timeouts/NAT; processes/threads/memory/FDs; blocking/NIO/epoll/Netty; zero-copy/cgroups; debugging | Annotated request and socket lifecycle; OS-specific command labels |
| 22 | Failure/time/order; consensus/Raft; election/leases; locks/fencing/Redlock debate; consistency/CAP/PACELC; quorums/replication/hashing; failure detection; exactly-once/2PC/saga | Paused leader and stale-writer timelines; both sides of contested claims with primary sources |
| 23 | JFR/JMC/async-profiler/MAT; CPU/allocation/thread/heap; GC; container limits; JIT; JMH; Little's law; pool sizing | Measured profiling sessions only; no synthetic output passed off as collected evidence |
| 24 | Capstone Part A: day/deploy/five failures/10x scale/security/whiteboard/script/poster | One connected narrative, no new theory; Part B deferred until `lite` |
| 25 | Monolith/modular monolith/microservices; strangler; DDD; aggregates/contexts/events; hexagonal packages; contracts/ADRs/evolution/debt | Boundaries justified by invariants and ownership, not number of services |
| 26 | OLTP/OLAP; warehouse/lake/lakehouse; pipelines/CDC; star schema; quality/lineage | Tenant-safe analytics feed and reconciliation of derived data |
| 27 | Payment lifecycle; ledger; idempotency/reconciliation; uncertain gateway outcomes; state machines/fraud; PCI/KYC/AML; UPI/cards | Wallet, gateway integration, and ledger HLDs; every regulatory/network claim marked for verification |

### Mandatory LLD Set

LRU cache; rate limiter; parking lot; elevator; notification service; logger framework; task scheduler; TTL key-value store; pub/sub; pluggable connector framework; **idempotency-key registry**; **webhook delivery dispatcher**. The final two reinforce the reference system. Each problem includes contract boundaries, Java tests, concurrency behavior, and failure cases; not twelve untested code sketches.

### Mandatory HLD Set

URL shortener; rate-limiter service; notification system; OAuth/token service; bulk data sync pipeline; webhook delivery; payment/wallet; distributed cache; API gateway; job scheduler; search; live-status service. Chapter 27 deepens the money-specific invariants instead of copying Chapter 12.

## IntegrationHub Design Registry

IntegrationHub is fictional. Its deliberately broad architecture is a teaching model, not a claim that a small team should deploy every component.

- **Client:** React SPA. Interactive login uses authorization code with PKCE and OIDC. Tenant selection is untrusted input until authorized. Angular is comparative, not a second mandatory UI.
- **Edge:** DNS -> public L7 load balancer/TLS termination -> gateway. The gateway routes, applies coarse quotas, and validates credentials. Services still enforce authorization. An internal encrypted hop is configured separately from public TLS.
- **Identity:** Auth/Token service plus authorized SCIM provisioning endpoint; external IdP federation is possible. Certificate/secret service mediates secret references and rotation, backed by managed key material. Tokens and secret values never enter normal logs.
- **Connector:** `POST /v1/sync-jobs` accepts a tenant-scoped idempotency key. In one PostgreSQL transaction persist the key/request digest, job, and outbox event. The acceptance response is `202`, not a claim of completed synchronization.
- **Events:** an outbox relay publishes to Kafka; CDC is the production evolution discussed in 07. Tenant/job partition keys preserve per-job ordering within a partition, not global order. Workers use bounded retries and idempotent effects. Broker semantics do not automatically cover an external API or PostgreSQL transaction.
- **Durable model:** tenant; connector configuration; sync job; job item/checkpoint; outbox event; processed event; job progress event. Shared-schema `tenant_id` is part of application checks, uniqueness, and access paths. PostgreSQL RLS is additional defense only when correctly configured and tested. No pooled connection may retain a previous tenant's session context.
- **Data ownership:** each service owns its tables/schema and API. Same database cluster may host separate schemas in the teaching model; no uncontrolled cross-service table writes. PostgreSQL is the operational authority. Redis is a cache/quota store, Elasticsearch a derived search projection, MongoDB an optional raw-payload store with retention and PII controls.
- **Progress:** workers commit result, deduplication record, progress event, and any next outbox event atomically where they share a database. SSE nodes use a live fan-out backplane plus a bounded durable replay store. Redis Pub/Sub alone is not replay. Kafka consumers in the same group distribute messages; they do not broadcast each event to every SSE node.
- **SSE identity:** same-origin secure HttpOnly session cookie via the gateway/BFF boundary, with state-changing HTTP requests protected against CSRF. The gateway may forward a short-lived access token internally. Alternative fetch-stream bearer handling is explained in 05/09; native EventSource does not accept arbitrary authorization headers. Never put long-lived tokens in stream URLs.
- **Operations:** container images built and tested by Jenkins; immutable image digest promoted in deployment Git; ArgoCD reconciles Kubernetes. Readiness governs endpoint eligibility, not successful business processing. OpenTelemetry context crosses HTTP and message headers; Prometheus metrics avoid unbounded tenant/job labels; Loki redacts sensitive data; Grafana presents views.
- **Regional baseline:** single write region, backups and restore objectives to be specified. Do not silently assume active-active writes. Payments are a later bounded context and case-study extension, not mixed into the initial sync schema.

## Diagram Style Guide

Mermaid source stays in Markdown. Prefer architecture flowcharts, important-flow sequences, state diagrams for lifecycles, ER for invariants, and decision trees when alternatives are genuinely branching. Every diagram has a caption/nearby prose; color never carries meaning alone.

| Layer | Fill | Border/text | Meaning |
|---|---|---|---|
| Client | `#e0f2fe` | `#075985` | Browser and UI |
| Edge | `#ffedd5` | `#9a3412` | DNS/LB/gateway |
| Service | `#dcfce7` | `#166534` | Business processing |
| Data | `#fef3c7` | `#854d0e` | Durable stores and derived views |
| Infra | `#e2e8f0` | `#334155` | Runtime, delivery, telemetry |
| Security | `#ffe4e6` | `#9f1239` | Identity and secrets |

Use at most roughly eight participants per sequence, split a crowded flow by ownership boundary, and number narrative steps. Sequence participant labels include their layer where color assignment is unavailable. Diagram palettes remain readable on a white diagram surface in both reading themes. No decorative diagrams or unrelated stock imagery.

Callouts: Mechanism (cog/teal), Trap (triangle/red), Interview (message/blue), Production (activity/amber), Decision (branch/green). Use a text label as well as an icon. Comparison tables always contain **Use when** and **Avoid when**.

## Cross-References and Index

- Chapter IDs never change. Canonical roots are `chNN-slug`, recorded in `catalog.json` and [state.md](state.md).
- Source links to other chapters use relative Markdown paths plus canonical anchors. The build rewrites them to same-document fragments.
- Until a chapter exists, its canonical anchor belongs to an explicitly labeled planned section generated from the catalog. It states that explanations are not yet written and links back to 00. There are no fabricated source chapters.
- The build generates reciprocal Related chapters blocks from an undirected relationship graph. Explicit source cross-links also add reverse relationships. Link validation rejects missing fragments and duplicate IDs.
- The front matter separates the **explained-here index** from the **planned coverage index**. Terms link to actual section anchors as explanations are written. Chapter 20 consolidates and reviews the global index last.
- Source chapter anchors are explicit HTML anchors. Any generated heading anchor is prefixed by its chapter ID to avoid repeated headings colliding.

## Build and Review Gates

Preferred pipeline: Markdown -> markdown-it -> build-time highlight.js -> Mermaid CLI inline SVG -> one HTML with embedded CSS/JS/icons -> headless Chrome PDF. HTML includes collapsible chapter groups, full-text search, light/dark themes, code copying, a cover and TOC, responsive diagrams, and print rules. No network is required for reading.

Run source, anchor, diagram, browser, and PDF checks. Verify search, theme and navigation on desktop and mobile; inspect screenshots. PDF must be generated from the same HTML and contain page numbers. Keep actual machine results in the review log. No Java code is required to teach the orientation chapter; later snippets belong under `code/` and must be compiled/tested or explicitly marked not executed.

Allow no more than two repair attempts per tooling problem. On failure use the requested Pandoc fallback when available; otherwise record the exact alternative and lost functionality. Preserve source and build scripts regardless. Do not label a merely created file as a verified publication.

Before every `next` or named-chapter request, re-read PROMPT.md and state.md. Review technical claims against primary documentation, execute applicable code, validate all links, and append every verification marker and remaining uncertainty to review-log.md.