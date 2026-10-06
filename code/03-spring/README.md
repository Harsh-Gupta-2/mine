# Chapter 03 Spring Mechanism Labs

**NOT EXECUTED.** Existing Java/Maven and a local Maven dependency cache are unavailable. No dependencies were downloaded; compilation, dependency resolution and runtime assertions remain unverified.

The POM pins Boot parent 4.0.8 and imports Cloud BOM 2025.1.3. The official Boot 4.0 system-requirements page and Cloud compatibility matrix were consulted on 2026-10-06. These are reproducible teaching pins, not a guarantee of the latest supported security patches. The Cloud BOM is dependency management only: no Gateway, Config Server, Feign or discovery process is started or tested.

ProxyBoundaryLab uses Spring ProxyFactory with explicit interfaces and an interceptor. It checks that an external call crosses the proxy but a target's self-call does not. The final target is valid for an interface proxy; this does not imply it can be subclass-proxied.

ContainerTransactionLab uses AnnotationConfigApplicationContext, explicit class-based transaction proxies, a lifecycle probe, JdbcTemplate and H2. It checks singleton identity, initialization/destruction, external commit/rollback, nontransactional self-invocation leaving an auto-committed insert, and UnexpectedRollbackException after a participating REQUIRED transaction marks rollback-only. The deliberately broken self-call is a negative example. H2 is not evidence for PostgreSQL/MySQL isolation, production pooling or distributed transactions.

This is not a Boot web application or IntegrationHub-lite. No MVC server, SecurityFilterChain, Actuator endpoint, gateway, OAuth flow, Kafka relay, Kubernetes workload or real secret is configured. The parent/BOM versions do not make those scenarios tested.

Windows PowerShell from the workspace root, no administrator privileges:

```powershell
& './docs/java-fs-guide/code/03-spring/run.ps1'
```

The runner requires an existing JDK 21+ and Maven; -JdkHome can select an installed JDK. It always invokes Maven with -o (offline). All parent/BOM/dependency/plugin artifacts must already be cached. Missing prerequisites record NOT EXECUTED. An attempted build/test failure records FAIL and must not be bypassed by an online retry without authorization. The compiler-present path is untested here.

Success strings in Java source are not observed terminal output. Read execution.json for actual results. All code is supplied as complete source, not as a claim of successful compilation.