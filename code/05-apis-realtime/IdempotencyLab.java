import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/** Chapter 05 lab: what a tenant-scoped idempotency key does and does not guarantee. */
public final class IdempotencyLab {

    /** The key is scoped, not a bare client string: tenant plus operation plus client key. */
    record RequestKey(String tenantId, String operation, String clientKey) { }

    /** The stored result binds the accepted outcome to the request content and a retention deadline. */
    record StoredResult(String digest, String jobId, long expiresAt) { }

    sealed interface Outcome permits Accepted, Replayed, Conflict { }

    record Accepted(String jobId) implements Outcome { }

    record Replayed(String jobId) implements Outcome { }

    record Conflict(String reason) implements Outcome { }

    /** An in-memory stand-in for the single database transaction described in the chapter. */
    static final class JobService {
        private final Map<RequestKey, StoredResult> results = new ConcurrentHashMap<>();
        private final AtomicLong jobSequence = new AtomicLong();
        private final AtomicLong acceptedWork = new AtomicLong();
        private final AtomicLong logicalClock = new AtomicLong();
        private final long retentionMillis;

        JobService(long retentionMillis) {
            this.retentionMillis = retentionMillis;
        }

        /** A test clock: real retention is enforced by the store, never by wall-clock guesses in the handler. */
        void advanceClock(long millis) {
            logicalClock.addAndGet(millis);
        }

        long acceptedWork() {
            return acceptedWork.get();
        }

        Outcome submit(RequestKey key, String body) {
            String digest = digest(body);
            long now = logicalClock.get();
            boolean[] createdHere = { false };
            StoredResult winner = results.compute(key, (ignored, current) -> {
                if (current != null && current.expiresAt() > now) {
                    return current;
                }
                createdHere[0] = true;
                acceptedWork.incrementAndGet();
                return new StoredResult(digest, "job-" + jobSequence.incrementAndGet(), now + retentionMillis);
            });
            if (!winner.digest().equals(digest)) {
                return new Conflict("key reused with a different request body");
            }
            return createdHere[0] ? new Accepted(winner.jobId()) : new Replayed(winner.jobId());
        }
    }

    static String digest(String body) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(sha256.digest(body.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException cause) {
            throw new IllegalStateException("SHA-256 is required by the platform", cause);
        }
    }

    static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
    }

    static void retrySameRequestIsNotSecondJob() {
        JobService service = new JobService(60_000);
        RequestKey key = new RequestKey("tenant-a", "POST /v1/sync-jobs", "k-1");
        Outcome first = service.submit(key, "{\"connectorId\":7}");
        Outcome retry = service.submit(key, "{\"connectorId\":7}");
        check(first instanceof Accepted, "first submission is accepted");
        check(retry instanceof Replayed, "identical retry replays the stored result");
        check(((Accepted) first).jobId().equals(((Replayed) retry).jobId()), "replay returns the same job identity");
        check(service.acceptedWork() == 1, "only one unit of work was accepted");
    }

    static void sameKeyWithDifferentBodyIsAConflict() {
        JobService service = new JobService(60_000);
        RequestKey key = new RequestKey("tenant-a", "POST /v1/sync-jobs", "k-1");
        service.submit(key, "{\"connectorId\":7}");
        Outcome reused = service.submit(key, "{\"connectorId\":9}");
        check(reused instanceof Conflict, "a reused key with different content must not silently replay");
        check(service.acceptedWork() == 1, "the conflicting request created no extra work");
    }

    static void keysAreScopedPerTenant() {
        JobService service = new JobService(60_000);
        Outcome a = service.submit(new RequestKey("tenant-a", "POST /v1/sync-jobs", "k-1"), "{\"connectorId\":7}");
        Outcome b = service.submit(new RequestKey("tenant-b", "POST /v1/sync-jobs", "k-1"), "{\"connectorId\":7}");
        check(a instanceof Accepted && b instanceof Accepted, "identical client strings in two tenants are two requests");
        check(service.acceptedWork() == 2, "tenant scoping prevents cross-tenant collision and cross-tenant disclosure");
    }

    static void retentionExpiryEndsTheGuarantee() {
        JobService service = new JobService(1_000);
        RequestKey key = new RequestKey("tenant-a", "POST /v1/sync-jobs", "k-1");
        Outcome first = service.submit(key, "{\"connectorId\":7}");
        service.advanceClock(1_001);
        Outcome afterExpiry = service.submit(key, "{\"connectorId\":7}");
        check(afterExpiry instanceof Accepted, "past the retention window the key is no longer known");
        check(!((Accepted) first).jobId().equals(((Accepted) afterExpiry).jobId()), "a second job identity now exists");
        check(service.acceptedWork() == 2, "retention is part of the contract, not an implementation detail");
    }

    static void concurrentDuplicatesCollapseToOneAcceptance() throws Exception {
        JobService service = new JobService(60_000);
        RequestKey key = new RequestKey("tenant-a", "POST /v1/sync-jobs", "k-1");
        int callers = 8;
        CountDownLatch startGate = new CountDownLatch(1);
        List<Future<Outcome>> submissions = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(callers);
        try {
            for (int caller = 0; caller < callers; caller++) {
                submissions.add(pool.submit(() -> {
                    startGate.await();
                    return service.submit(key, "{\"connectorId\":7}");
                }));
            }
            startGate.countDown();
            long accepted = 0;
            long replayed = 0;
            for (Future<Outcome> submission : submissions) {
                Outcome outcome = submission.get(10, TimeUnit.SECONDS);
                if (outcome instanceof Accepted) {
                    accepted++;
                } else if (outcome instanceof Replayed) {
                    replayed++;
                }
            }
            check(accepted == 1, "exactly one concurrent caller creates the job");
            check(replayed == callers - 1, "every other caller observes the stored result");
            check(service.acceptedWork() == 1, "atomic reservation, not read-then-write, is what makes this safe");
        } finally {
            pool.shutdownNow();
            check(pool.awaitTermination(10, TimeUnit.SECONDS), "executor terminated");
        }
    }

    public static void main(String[] args) throws Exception {
        retrySameRequestIsNotSecondJob();
        sameKeyWithDifferentBodyIsAConflict();
        keysAreScopedPerTenant();
        retentionExpiryEndsTheGuarantee();
        concurrentDuplicatesCollapseToOneAcceptance();
        System.out.println("IdempotencyLab checks completed");
    }
}
