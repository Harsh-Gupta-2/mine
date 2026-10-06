import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class CoreJavaLab {
    record TenantKey(String tenantId, String connectorId) {
        TenantKey {
            Objects.requireNonNull(tenantId);
            Objects.requireNonNull(connectorId);
            if (tenantId.isBlank() || connectorId.isBlank()) {
                throw new IllegalArgumentException("Identifiers must not be blank");
            }
        }
    }

    record SyncBatch(TenantKey key, List<String> recordIds) {
        SyncBatch {
            Objects.requireNonNull(key);
            recordIds = List.copyOf(recordIds);
        }
    }

    sealed interface Outcome permits Accepted, Rejected {}
    record Accepted(SyncBatch batch) implements Outcome {}
    record Rejected(String reason) implements Outcome {}

    static String describe(Outcome outcome) {
        Objects.requireNonNull(outcome);
        return switch (outcome) {
            case Accepted(SyncBatch batch) -> "accepted:" + batch.recordIds().size();
            case Rejected(String reason) -> "rejected:" + reason;
        };
    }

    static <NumberType extends Number> void copyNumbers(
            List<? extends NumberType> source,
            List<? super NumberType> destination) {
        destination.addAll(source);
    }

    static void changeReference(List<String> values) {
        values.add("shared mutation");
        values = new ArrayList<>();
        values.add("local reference only");
    }

    static final class CollisionKey {
        private final String value;

        CollisionKey(String value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof CollisionKey key && value.equals(key.value);
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    static final class FailingResource implements AutoCloseable {
        private final String name;
        private final List<String> closed;

        FailingResource(String name, List<String> closed) {
            this.name = name;
            this.closed = closed;
        }

        @Override
        public void close() throws IOException {
            closed.add(name);
            throw new IOException("close:" + name);
        }
    }

    static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void expect(Class<? extends RuntimeException> type, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException failure) {
            check(type.isInstance(failure), "Unexpected exception: " + failure);
            return;
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }

    static void valuesAndCollections() {
        var key = new TenantKey("tenant-a", "crm");
        var original = new ArrayList<>(List.of("row-1", "row-2"));
        var batch = new SyncBatch(key, original);
        original.add("row-3");
        check(batch.recordIds().size() == 2, "Defensive snapshot");
        expect(UnsupportedOperationException.class, () -> batch.recordIds().add("row-4"));
        check(batch.equals(new SyncBatch(new TenantKey("tenant-a", "crm"),
                List.of("row-1", "row-2"))), "Value equality");
        expect(IllegalArgumentException.class, () -> new TenantKey(" ", "crm"));

        Map<TenantKey, SyncBatch> batches = new HashMap<>();
        batches.put(key, batch);
        check(batches.get(new TenantKey("tenant-a", "crm")) == batch, "Equal key lookup");
        check(batches.get(new TenantKey("tenant-b", "crm")) == null, "Tenant-scoped key");

        Map<CollisionKey, String> collisions = new HashMap<>();
        collisions.put(new CollisionKey("first"), "one");
        collisions.put(new CollisionKey("second"), "two");
        check(collisions.size() == 2, "A hash collision is not equality");
        check("one".equals(collisions.get(new CollisionKey("first"))), "Collision lookup");

        var passed = new ArrayList<String>();
        changeReference(passed);
        check(passed.equals(List.of("shared mutation")), "References are passed by value");
        check("accepted:2".equals(describe(new Accepted(batch))), "Record pattern branch");
        check("rejected:invalid".equals(describe(new Rejected("invalid"))), "Sealed branch");
        expect(NullPointerException.class, () -> describe(null));
    }

    static void genericsAndStreams() {
        List<Integer> source = List.of(1, 2, 3);
        List<Number> numbers = new ArrayList<>();
        copyNumbers(source, numbers);
        check(numbers.equals(source), "Producer extends, consumer super");

        var processed = new ArrayList<String>();
        var pipeline = List.of(" a ", "", " b ").stream()
                .map(String::strip)
                .filter(value -> !value.isEmpty())
                .map(value -> {
                    processed.add(value);
                    return value.toUpperCase(java.util.Locale.ROOT);
                });
        check(processed.isEmpty(), "Intermediate operations are lazy");
        List<String> result = pipeline.toList();
        check(result.equals(List.of("A", "B")), "Terminal traversal");
        check(processed.equals(List.of("a", "b")), "Sequential diagnostic trace");
        expect(UnsupportedOperationException.class, () -> result.add("C"));
        expect(IllegalStateException.class, () -> pipeline.toList());

        Map<String, Long> counts = List.of("tenant-a", "tenant-b", "tenant-a")
                .stream().collect(Collectors.groupingBy(value -> value, Collectors.counting()));
        check(counts.get("tenant-a") == 2L, "Grouping without external shared mutation");
        expect(IllegalStateException.class, () -> List.of("same", "same").stream()
                .collect(Collectors.toMap(value -> value, String::length)));
    }

    static void resourceFailures() {
        var closed = new ArrayList<String>();
        try (var first = new FailingResource("first", closed);
             var second = new FailingResource("second", closed)) {
            throw new IOException("body");
        } catch (IOException failure) {
            check("body".equals(failure.getMessage()), "Preserve primary failure");
            check(closed.equals(List.of("second", "first")), "Reverse close order");
            check(failure.getSuppressed().length == 2, "Preserve close failures");
            check("close:second".equals(failure.getSuppressed()[0].getMessage()),
                    "First suppressed failure follows close order");
        }
    }

    static void virtualThreadIdentity() throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var future = executor.submit(() -> Thread.currentThread().isVirtual());
            try {
                check(future.get(5, TimeUnit.SECONDS), "Task runs on a virtual thread");
            } finally {
                future.cancel(true);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        valuesAndCollections();
        genericsAndStreams();
        resourceFailures();
        virtualThreadIdentity();
        System.out.println("CoreJavaLab: all checks passed");
    }
}