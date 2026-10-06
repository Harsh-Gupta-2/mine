import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Chapter 06 lab: what a snapshot shows, and which anomalies a snapshot alone does not prevent. */
public final class MvccLab {

    /**
     * How a transaction is checked at commit time.
     * LAST_WRITE_WINS models an application doing read-modify-write with no protection at all.
     * SNAPSHOT models first-committer-wins on written keys, which is what snapshot isolation gives you.
     * SNAPSHOT_WITH_READ_CHECK additionally rejects a commit whose read set moved, approximating
     * serializable snapshot isolation. It is a teaching model, not PostgreSQL's implementation.
     */
    enum Mode { LAST_WRITE_WINS, SNAPSHOT, SNAPSHOT_WITH_READ_CHECK }

    record Version(long value, long commitTime) { }

    static final class AbortedException extends RuntimeException {
        AbortedException(String reason) {
            super(reason);
        }
    }

    /** A tiny multi-version store. Each key keeps every committed version with its commit time. */
    static final class Store {
        private final Map<String, List<Version>> history = new HashMap<>();
        private long clock;

        Store seed(String key, long value) {
            history.computeIfAbsent(key, ignored -> new ArrayList<>()).add(new Version(value, ++clock));
            return this;
        }

        long now() {
            return clock;
        }

        /** The value a transaction started at snapshotTime is entitled to see. */
        long valueAt(String key, long snapshotTime) {
            List<Version> versions = history.get(key);
            if (versions == null) {
                throw new IllegalArgumentException("unknown key " + key);
            }
            long visible = Long.MIN_VALUE;
            for (Version version : versions) {
                if (version.commitTime() <= snapshotTime) {
                    visible = version.value();
                }
            }
            if (visible == Long.MIN_VALUE) {
                throw new IllegalStateException("no version visible to this snapshot");
            }
            return visible;
        }

        /** The commit time of the newest version of a key, used to detect concurrent change. */
        long lastChange(String key) {
            List<Version> versions = history.get(key);
            return versions.get(versions.size() - 1).commitTime();
        }

        Transaction begin(Mode mode) {
            return new Transaction(this, mode, clock);
        }

        private void append(Map<String, Long> writes) {
            long commitTime = ++clock;
            for (Map.Entry<String, Long> write : writes.entrySet()) {
                history.computeIfAbsent(write.getKey(), ignored -> new ArrayList<>())
                        .add(new Version(write.getValue(), commitTime));
            }
        }
    }

    static final class Transaction {
        private final Store store;
        private final Mode mode;
        private final long snapshotTime;
        private final Set<String> readSet = new HashSet<>();
        private final Map<String, Long> writes = new HashMap<>();
        private boolean finished;

        Transaction(Store store, Mode mode, long snapshotTime) {
            this.store = store;
            this.mode = mode;
            this.snapshotTime = snapshotTime;
        }

        long read(String key) {
            readSet.add(key);
            if (writes.containsKey(key)) {
                return writes.get(key);
            }
            return mode == Mode.LAST_WRITE_WINS ? store.valueAt(key, store.now()) : store.valueAt(key, snapshotTime);
        }

        void write(String key, long value) {
            writes.put(key, value);
        }

        void commit() {
            if (finished) {
                throw new IllegalStateException("transaction already finished");
            }
            finished = true;
            if (mode != Mode.LAST_WRITE_WINS) {
                for (String key : writes.keySet()) {
                    if (store.lastChange(key) > snapshotTime) {
                        throw new AbortedException("write-write conflict on " + key);
                    }
                }
            }
            if (mode == Mode.SNAPSHOT_WITH_READ_CHECK) {
                for (String key : readSet) {
                    if (store.lastChange(key) > snapshotTime) {
                        throw new AbortedException("read set changed under this snapshot: " + key);
                    }
                }
            }
            store.append(writes);
        }
    }

    static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
    }

    static void snapshotReadIsStableWhileAnotherTransactionCommits() {
        Store store = new Store().seed("job-1.processed", 100);
        Transaction reader = store.begin(Mode.SNAPSHOT);
        check(reader.read("job-1.processed") == 100, "the reader starts from the committed value");

        Transaction writer = store.begin(Mode.SNAPSHOT);
        writer.write("job-1.processed", 150);
        writer.commit();

        check(reader.read("job-1.processed") == 100, "a snapshot does not move because someone else committed");
        check(store.valueAt("job-1.processed", store.now()) == 150, "a transaction starting now sees the new value");
    }

    static void readModifyWriteWithoutProtectionLosesAnUpdate() {
        Store store = new Store().seed("connector-7.quota", 100);
        Transaction first = store.begin(Mode.LAST_WRITE_WINS);
        Transaction second = store.begin(Mode.LAST_WRITE_WINS);

        long firstRead = first.read("connector-7.quota");
        long secondRead = second.read("connector-7.quota");
        check(firstRead == 100 && secondRead == 100, "both transactions read the same starting value");

        first.write("connector-7.quota", firstRead - 10);
        first.commit();
        second.write("connector-7.quota", secondRead - 10);
        second.commit();

        check(store.valueAt("connector-7.quota", store.now()) == 90, "two decrements of ten produced one decrement");
    }

    static void snapshotIsolationAbortsTheSecondWriterInstead() {
        Store store = new Store().seed("connector-7.quota", 100);
        Transaction first = store.begin(Mode.SNAPSHOT);
        Transaction second = store.begin(Mode.SNAPSHOT);

        first.write("connector-7.quota", first.read("connector-7.quota") - 10);
        first.commit();

        second.write("connector-7.quota", second.read("connector-7.quota") - 10);
        boolean aborted = false;
        try {
            second.commit();
        } catch (AbortedException expected) {
            aborted = true;
        }
        check(aborted, "first committer wins, so the stale writer must be told to retry");
        check(store.valueAt("connector-7.quota", store.now()) == 90, "the losing write was not applied");

        Transaction retry = store.begin(Mode.SNAPSHOT);
        retry.write("connector-7.quota", retry.read("connector-7.quota") - 10);
        retry.commit();
        check(store.valueAt("connector-7.quota", store.now()) == 80, "the retry recomputes from a fresh snapshot");
    }

    static void writeSkewCommitsUnderSnapshotIsolation() {
        // Invariant the application believes it has: at least one connector stays enabled.
        Store store = new Store().seed("connector-a.enabled", 1).seed("connector-b.enabled", 1);
        Transaction first = store.begin(Mode.SNAPSHOT);
        Transaction second = store.begin(Mode.SNAPSHOT);

        check(first.read("connector-a.enabled") + first.read("connector-b.enabled") >= 2, "first sees two enabled");
        check(second.read("connector-a.enabled") + second.read("connector-b.enabled") >= 2, "second sees two enabled");

        first.write("connector-a.enabled", 0);
        second.write("connector-b.enabled", 0);
        first.commit();
        second.commit();

        long remaining = store.valueAt("connector-a.enabled", store.now()) + store.valueAt("connector-b.enabled", store.now());
        check(remaining == 0, "both commits succeeded because they wrote different keys, and the invariant broke");
    }

    static void readSetCheckingRejectsTheSecondWriteSkewCommit() {
        Store store = new Store().seed("connector-a.enabled", 1).seed("connector-b.enabled", 1);
        Transaction first = store.begin(Mode.SNAPSHOT_WITH_READ_CHECK);
        Transaction second = store.begin(Mode.SNAPSHOT_WITH_READ_CHECK);

        first.read("connector-a.enabled");
        first.read("connector-b.enabled");
        second.read("connector-a.enabled");
        second.read("connector-b.enabled");

        first.write("connector-a.enabled", 0);
        first.commit();

        second.write("connector-b.enabled", 0);
        boolean aborted = false;
        try {
            second.commit();
        } catch (AbortedException expected) {
            aborted = true;
        }
        check(aborted, "checking the read set is what turns write skew into a serialization failure");
        long remaining = store.valueAt("connector-a.enabled", store.now()) + store.valueAt("connector-b.enabled", store.now());
        check(remaining == 1, "the invariant survived because the application must retry, not because writes are magic");
    }

    public static void main(String[] args) {
        snapshotReadIsStableWhileAnotherTransactionCommits();
        readModifyWriteWithoutProtectionLosesAnUpdate();
        snapshotIsolationAbortsTheSecondWriterInstead();
        writeSkewCommitsUnderSnapshotIsolation();
        readSetCheckingRejectsTheSecondWriteSkewCommit();
        System.out.println("MvccLab checks completed");
    }
}
