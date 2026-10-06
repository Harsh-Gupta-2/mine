# Chapter 07 Delivery Lab

**NOT EXECUTED:** the runner found no existing JDK compiler. No dependency downloads are authorized. Compilation and all assertion outcomes remain unverified.

DeliveryLab.java models a synchronized in-memory commit point for receipts, local effects and one ordered checkpoint. Its four probes arrange failure before commit, failure after commit before acknowledgement, tenant/consumer identity and content conflicts, and a failed-chunk restart. It does not run Kafka, RabbitMQ, PostgreSQL, Debezium or Spring Batch and does not survive a real process crash.

Windows PowerShell, from the workspace root, no administrator rights:

```powershell
& './docs/java-fs-guide/code/07-messaging/run.ps1'
```

The runner accepts -JdkHome for an existing JDK 21 or later, compiles with --release 21 and records actual status/output in execution.json. The preflight was executed; no Java output was produced. A future PASS would validate this local model, not broker delivery or framework transaction configuration.

The checkpoint represents one lane with monotonic positions supplied by the probes. The store copies all state for clarity and is not a production storage implementation. Retention, authorization, serialization, partition offset vectors, concurrent broker deliveries, persistent recovery and external side effects need separate tests.