import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** Chapter 06 lab: a connection pool is a limit with a queue, not extra database capacity. */
public final class ConnectionPoolLab {

    record Lease(int id) { }

    static final class PoolExhaustedException extends RuntimeException {
        PoolExhaustedException(String reason) {
            super(reason);
        }
    }

    /**
     * A bounded pool with an explicit admission limit in front of it.
     * The semaphore bounds concurrent users of the resource; the waiter counter bounds how many
     * callers may queue for it. Those are two different limits and failing to separate them is
     * how an outage becomes an unbounded backlog.
     */
    static final class ConnectionPool {
        private final Semaphore permits;
        private final int maxWaiters;
        private final AtomicInteger waiting = new AtomicInteger();
        private final AtomicInteger inUse = new AtomicInteger();
        private final AtomicInteger peakInUse = new AtomicInteger();
        private final AtomicInteger rejected = new AtomicInteger();
        private final AtomicInteger timedOut = new AtomicInteger();
        private final AtomicInteger nextLeaseId = new AtomicInteger();

        ConnectionPool(int size, int maxWaiters) {
            this.permits = new Semaphore(size);
            this.maxWaiters = maxWaiters;
        }

        /** Returns empty when the wait deadline expires; the deadline is a guard, not a measurement. */
        Optional<Lease> acquire(long timeoutMillis) throws InterruptedException {
            if (waiting.incrementAndGet() > maxWaiters) {
                waiting.decrementAndGet();
                rejected.incrementAndGet();
                throw new PoolExhaustedException("admission limit reached before any connection was offered");
            }
            try {
                if (!permits.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS)) {
                    timedOut.incrementAndGet();
                    return Optional.empty();
                }
            } finally {
                waiting.decrementAndGet();
            }
            int current = inUse.incrementAndGet();
            peakInUse.accumulateAndGet(current, Math::max);
            return Optional.of(new Lease(nextLeaseId.incrementAndGet()));
        }

        void release(Lease lease) {
            if (lease == null) {
                throw new IllegalArgumentException("nothing to release");
            }
            inUse.decrementAndGet();
            permits.release();
        }

        int peakInUse() {
            return peakInUse.get();
        }

        int timedOut() {
            return timedOut.get();
        }

        int rejected() {
            return rejected.get();
        }

        int available() {
            return permits.availablePermits();
        }
    }

    static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
    }

    static void poolSizeCapsConcurrentDatabaseWork() throws Exception {
        ConnectionPool pool = new ConnectionPool(4, 64);
        int callers = 16;
        CountDownLatch startGate = new CountDownLatch(1);
        AtomicInteger completed = new AtomicInteger();
        ExecutorService workers = Executors.newFixedThreadPool(callers);
        try {
            List<Future<?>> tasks = new ArrayList<>();
            for (int caller = 0; caller < callers; caller++) {
                tasks.add(workers.submit(() -> {
                    startGate.await();
                    Lease lease = pool.acquire(10_000).orElseThrow(() -> new AssertionError("acquire should succeed"));
                    try {
                        completed.incrementAndGet();
                    } finally {
                        pool.release(lease);
                    }
                    return null;
                }));
            }
            startGate.countDown();
            for (Future<?> task : tasks) {
                task.get(30, TimeUnit.SECONDS);
            }
            check(completed.get() == callers, "every caller eventually ran");
            check(pool.peakInUse() <= 4, "the pool never let more than its size touch the database at once");
            check(pool.available() == 4, "every lease was returned");
        } finally {
            workers.shutdownNow();
            check(workers.awaitTermination(10, TimeUnit.SECONDS), "executor terminated");
        }
    }

    static void acquireFailsWithADeadlineRatherThanWaitingForever() throws Exception {
        ConnectionPool pool = new ConnectionPool(1, 64);
        Lease held = pool.acquire(1_000).orElseThrow();
        try {
            check(pool.acquire(100).isEmpty(), "a saturated pool must return a timeout, not block indefinitely");
            check(pool.timedOut() == 1, "the timeout is observable and should be a metric in production");
        } finally {
            pool.release(held);
        }
        check(pool.acquire(1_000).isPresent(), "capacity is available again after the lease is returned");
    }

    static void aLeakedLeaseIsPermanentCapacityLoss() throws Exception {
        ConnectionPool pool = new ConnectionPool(2, 64);
        Lease leaked = pool.acquire(1_000).orElseThrow();
        check(leaked.id() > 0, "the leaked lease exists and is simply never released");
        Lease healthy = pool.acquire(1_000).orElseThrow();
        pool.release(healthy);
        check(pool.available() == 1, "the pool permanently lost one connection to the leak");
        Lease last = pool.acquire(1_000).orElseThrow();
        check(pool.acquire(100).isEmpty(), "the remaining capacity is one, no matter how long the process runs");
        pool.release(last);
    }

    static void releaseInFinallyPreservesCapacityWhenWorkFails() throws Exception {
        ConnectionPool pool = new ConnectionPool(2, 64);
        boolean propagated = false;
        Lease lease = pool.acquire(1_000).orElseThrow();
        try {
            throw new IllegalStateException("statement failed");
        } catch (IllegalStateException expected) {
            propagated = true;
        } finally {
            pool.release(lease);
        }
        check(propagated, "the failure is not swallowed");
        check(pool.available() == 2, "a failed statement must not cost a connection");
    }

    static void admissionLimitRejectsInsteadOfQueueingWithoutBound() throws Exception {
        ConnectionPool pool = new ConnectionPool(1, 2);
        Lease held = pool.acquire(1_000).orElseThrow();
        CountDownLatch queued = new CountDownLatch(2);
        ExecutorService waiters = Executors.newFixedThreadPool(2);
        try {
            for (int waiter = 0; waiter < 2; waiter++) {
                waiters.submit(() -> {
                    queued.countDown();
                    pool.acquire(30_000).ifPresent(pool::release);
                    return null;
                });
            }
            check(queued.await(10, TimeUnit.SECONDS), "both waiters entered the pool");
            boolean rejectedImmediately = false;
            for (int attempt = 0; attempt < 200 && !rejectedImmediately; attempt++) {
                try {
                    pool.acquire(50).ifPresent(pool::release);
                } catch (PoolExhaustedException expected) {
                    rejectedImmediately = true;
                }
            }
            check(rejectedImmediately, "past the admission limit a caller is rejected rather than queued");
            check(pool.rejected() >= 1, "rejections are counted separately from acquisition timeouts");
        } finally {
            pool.release(held);
            waiters.shutdownNow();
            check(waiters.awaitTermination(10, TimeUnit.SECONDS), "executor terminated");
        }
    }

    public static void main(String[] args) throws Exception {
        poolSizeCapsConcurrentDatabaseWork();
        acquireFailsWithADeadlineRatherThanWaitingForever();
        aLeakedLeaseIsPermanentCapacityLoss();
        releaseInFinallyPreservesCapacityWhenWorkFails();
        admissionLimitRejectsInsteadOfQueueingWithoutBound();
        System.out.println("ConnectionPoolLab checks completed");
    }
}
