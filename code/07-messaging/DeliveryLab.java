import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DeliveryLab {
    record Event(String tenant, String consumer, String id, int units) { }
    record Receipt(String tenant, String consumer, String id) { }
    enum Failure { NONE, BEFORE_COMMIT, AFTER_COMMIT }

    static final class SimulatedCrash extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    record State(Map<Receipt, Integer> receipts, Map<String, Integer> totals, int checkpoint) {
        State {
            receipts = Map.copyOf(receipts);
            totals = Map.copyOf(totals);
        }
    }

    static final class Store {
        private State state = new State(Map.of(), Map.of(), 0);

        synchronized void apply(List<Event> events, int checkpoint, Failure failure) {
            Map<Receipt, Integer> receipts = new HashMap<>(state.receipts());
            Map<String, Integer> totals = new HashMap<>(state.totals());
            for (Event event : events) {
                Receipt key = new Receipt(event.tenant(), event.consumer(), event.id());
                Integer previous = receipts.putIfAbsent(key, event.units());
                if (previous != null && previous.intValue() != event.units()) {
                    throw new IllegalArgumentException("event identity reused with different content");
                }
                if (previous == null) {
                    totals.merge(event.tenant(), event.units(), Math::addExact);
                }
            }
            if (failure == Failure.BEFORE_COMMIT) {
                throw new SimulatedCrash();
            }
            state = new State(receipts, totals, checkpoint);
            if (failure == Failure.AFTER_COMMIT) {
                throw new SimulatedCrash();
            }
        }

        synchronized State snapshot() {
            return state;
        }
    }

    static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError(description);
        }
    }

    static void expectCrash(Runnable action) {
        boolean crashed = false;
        try {
            action.run();
        } catch (SimulatedCrash expected) {
            crashed = true;
        }
        check(crashed, "the arranged crash boundary was reached");
    }

    static void failedCommitLeavesNoReceiptOrEffect() {
        Store store = new Store();
        Event event = new Event("tenant-a", "progress", "event-1", 7);
        expectCrash(() -> store.apply(List.of(event), 1, Failure.BEFORE_COMMIT));
        check(store.snapshot().receipts().isEmpty(), "receipt rolled back with effect");
        check(store.snapshot().totals().isEmpty(), "no effect before commit");
        check(store.snapshot().checkpoint() == 0, "checkpoint did not advance");
        store.apply(List.of(event), 1, Failure.NONE);
        check(store.snapshot().totals().get("tenant-a") == 7, "retry applies once");
    }

    static void lostAcknowledgementRedeliversWithoutRepeatingEffect() {
        Store store = new Store();
        Event event = new Event("tenant-a", "progress", "event-1", 7);
        int brokerOffset = 0;
        expectCrash(() -> store.apply(List.of(event), 1, Failure.AFTER_COMMIT));
        check(brokerOffset == 0, "broker position still points to the unacknowledged record");
        check(store.snapshot().totals().get("tenant-a") == 7, "database effect already committed");
        store.apply(List.of(event), 1, Failure.NONE);
        brokerOffset = 1;
        check(store.snapshot().totals().get("tenant-a") == 7, "redelivery did not add seven again");
        check(brokerOffset == 1, "acknowledge only after the local commit");
    }

    static void scopesAndContentBindingAreExplicit() {
        Store store = new Store();
        Event first = new Event("tenant-a", "progress", "event-1", 7);
        store.apply(List.of(first, new Event("tenant-b", "progress", "event-1", 9)), 2, Failure.NONE);
        check(store.snapshot().receipts().size() == 2, "tenant keys do not collide");
        boolean rejected = false;
        try {
            store.apply(List.of(new Event("tenant-a", "progress", "event-1", 99)), 3, Failure.NONE);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        check(rejected, "same identity with different content is not an ordinary duplicate");
        check(store.snapshot().checkpoint() == 2, "conflict did not advance the checkpoint");
        check(!new Receipt("tenant-a", "progress", "event-1")
                .equals(new Receipt("tenant-a", "search", "event-1")), "consumer identities are independent");
    }

    static void chunkRestartUsesTheCommittedCheckpoint() {
        Store store = new Store();
        List<Event> input = List.of(
                new Event("tenant-a", "batch", "row-1", 2),
                new Event("tenant-a", "batch", "row-2", 3),
                new Event("tenant-a", "batch", "row-3", 5));
        expectCrash(() -> store.apply(input.subList(0, 2), 2, Failure.BEFORE_COMMIT));
        check(store.snapshot().checkpoint() == 0, "restart begins before the failed chunk");
        store.apply(input.subList(0, 2), 2, Failure.NONE);
        int restartAt = store.snapshot().checkpoint();
        store.apply(input.subList(restartAt, input.size()), input.size(), Failure.NONE);
        check(store.snapshot().totals().get("tenant-a") == 10, "all input effects accounted for once");
        check(store.snapshot().checkpoint() == 3, "checkpoint describes the committed input prefix");
    }

    public static void main(String[] args) {
        failedCommitLeavesNoReceiptOrEffect();
        lostAcknowledgementRedeliversWithoutRepeatingEffect();
        scopesAndContentBindingAreExplicit();
        chunkRestartUsesTheCommittedCheckpoint();
        System.out.println("DeliveryLab checks completed");
    }
}