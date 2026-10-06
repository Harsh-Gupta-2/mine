# Chapter 04 JPA Lab

**NOT EXECUTED:** no existing Java compiler, Maven or cached ORM dependencies. Permission to download Mermaid does not authorize Java/Maven artifacts. The runner always uses Maven offline mode.

Boot parent 4.0.8 manages the dependencies; its published 4.0 coordinates table was checked for Hibernate ORM 7.2.24.Final, Jakarta Persistence 3.2.0 and H2 2.4.240. Maven resolution of this POM is unverified. This is a standalone persistence-unit lab, not Spring Data, a web service or IntegrationHub-lite.

JpaLab seeds three synthetic jobs, each with a different connector; checks managed identity, dirty flushing followed by rollback, detached merge returning a managed copy, a sequentially arranged optimistic conflict, and lazy association traversal versus a to-one fetch join. Second-level/query caches and batch fetching are disabled for that query fixture. The source prints counts only if executed; no expected counts are presented as observed output.

SyncJob/Connector are intentionally small entity examples with assigned fixture IDs. The Java constructor's tenant check and tenant-filtered query are not sufficient security for all write paths. The simple single-column FK does not independently enforce matching tenant IDs. Production requires service authorization and appropriate schema-level tenant invariants. Do not expose these entities directly as writable API payloads.

The persistence unit creates/drops only its in-memory H2 schema. Do not copy create-drop into a real database configuration. The empty credential is for this ephemeral in-memory fixture, not a production credential. No PostgreSQL/MySQL server is accessed.

Windows PowerShell from the workspace root:

```powershell
& './docs/java-fs-guide/code/04-jpa/run.ps1'
```

Optional -JdkHome selects an already installed JDK 21+. A pre-existing Maven installation and every parent/plugin/dependency artifact in its local cache are required. The report distinguishes NOT EXECUTED, PASS and FAIL. Offline dependency failure must not trigger an online retry.

Untested: all Java compilation, persistence XML schema validation, Hibernate/H2 assertions, Spring Data repository semantics, pessimistic lock blocking/deadlocks/timeouts, PostgreSQL isolation, L2/query cache invalidation, SQL batching, cross-tenant enforcement and runtime performance. Source/listing checks and XML well-formedness do not establish those results.