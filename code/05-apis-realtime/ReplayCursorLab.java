import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.OptionalLong;

/** Chapter 05 lab: what a Last-Event-ID cursor can and cannot recover after a dropped stream. */
public final class ReplayCursorLab {

    record ProgressEvent(long id, String status, int processed) { }

    sealed interface Resume permits Replay, SnapshotRequired { }

    /** The server could satisfy the cursor entirely from retained history. */
    record Replay(List<ProgressEvent> missed) implements Resume { }

    /** The cursor is unknown or older than retention, so the client must re-read durable state first. */
    record SnapshotRequired(String reason) implements Resume { }

    /** A bounded per-job retention buffer. Retention is a capacity decision, not an unlimited log. */
    static final class ProgressStream {
        private final Deque<ProgressEvent> retained = new ArrayDeque<>();
        private final int retentionSize;
        private long nextId;
        private int processedTotal;

        ProgressStream(int retentionSize) {
            this.retentionSize = retentionSize;
        }

        ProgressEvent publish(String status, int batchSize) {
            processedTotal += batchSize;
            ProgressEvent event = new ProgressEvent(++nextId, status, processedTotal);
            retained.addLast(event);
            while (retained.size() > retentionSize) {
                retained.removeFirst();
            }
            return event;
        }

        /** The durable projection the snapshot endpoint would read, independent of the event buffer. */
        ProgressEvent snapshot() {
            return new ProgressEvent(nextId, nextId == 0 ? "PENDING" : "RUNNING", processedTotal);
        }

        Resume resume(OptionalLong lastEventId) {
            if (lastEventId.isEmpty()) {
                return new SnapshotRequired("no cursor supplied");
            }
            long cursor = lastEventId.getAsLong();
            if (cursor > nextId) {
                return new SnapshotRequired("cursor ahead of the stream, likely a different job or restarted sequence");
            }
            long oldestRetained = retained.isEmpty() ? nextId + 1 : retained.getFirst().id();
            if (cursor + 1 < oldestRetained) {
                return new SnapshotRequired("cursor older than retention");
            }
            List<ProgressEvent> missed = new ArrayList<>();
            for (ProgressEvent event : retained) {
                if (event.id() > cursor) {
                    missed.add(event);
                }
            }
            return new Replay(missed);
        }
    }

    static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
    }

    static ProgressStream streamWith(int retention, int events) {
        ProgressStream stream = new ProgressStream(retention);
        for (int index = 0; index < events; index++) {
            stream.publish("RUNNING", 10);
        }
        return stream;
    }

    static void cursorInsideRetentionReplaysOnlyMissedEvents() {
        ProgressStream stream = streamWith(5, 5);
        Resume resume = stream.resume(OptionalLong.of(3));
        check(resume instanceof Replay, "a cursor inside retention is replayable");
        List<ProgressEvent> missed = ((Replay) resume).missed();
        check(missed.size() == 2, "only events after the cursor are resent");
        check(missed.get(0).id() == 4 && missed.get(1).id() == 5, "replay is ordered and gapless");
        check(missed.get(1).processed() == 50, "the last replayed event carries the cumulative figure");
    }

    static void currentCursorReplaysNothing() {
        ProgressStream stream = streamWith(5, 5);
        Resume resume = stream.resume(OptionalLong.of(5));
        check(resume instanceof Replay, "an up-to-date cursor is still a valid resume");
        check(((Replay) resume).missed().isEmpty(), "nothing is missed, so nothing is resent");
    }

    static void cursorOlderThanRetentionFallsBackToSnapshot() {
        ProgressStream stream = streamWith(3, 10);
        Resume resume = stream.resume(OptionalLong.of(2));
        check(resume instanceof SnapshotRequired, "retention is bounded, so old cursors cannot be honoured");
        ProgressEvent snapshot = stream.snapshot();
        check(snapshot.id() == 10, "the snapshot carries the position the client should resume from");
        check(snapshot.processed() == 100, "durable state, not the event buffer, is the recovery authority");
    }

    static void missingCursorRequiresSnapshotFirst() {
        ProgressStream stream = streamWith(5, 4);
        check(stream.resume(OptionalLong.empty()) instanceof SnapshotRequired, "a first connection is not a replay");
    }

    static void cursorAheadOfStreamIsRejected() {
        ProgressStream stream = streamWith(5, 4);
        Resume resume = stream.resume(OptionalLong.of(99));
        check(resume instanceof SnapshotRequired, "an impossible cursor must not be treated as caught up");
    }

    public static void main(String[] args) {
        cursorInsideRetentionReplaysOnlyMissedEvents();
        currentCursorReplaysNothing();
        cursorOlderThanRetentionFallsBackToSnapshot();
        missingCursorRequiresSnapshotFirst();
        cursorAheadOfStreamIsRejected();
        System.out.println("ReplayCursorLab checks completed");
    }
}
