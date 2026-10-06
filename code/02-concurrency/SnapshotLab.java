import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class SnapshotLab {
    record Limits(Map<String, Integer> byTenant) {
        Limits {
            byTenant = Map.copyOf(byTenant);
            if (byTenant.values().stream().anyMatch(limit -> limit < 1)) {
                throw new IllegalArgumentException("Limits must be positive");
            }
        }
    }

    static final class Registry {
        private volatile Limits current = new Limits(Map.of());

        void replace(Limits next) {
            current = Objects.requireNonNull(next);
        }

        Limits snapshot() {
            return current;
        }
    }

    public static void main(String[] args) throws Exception {
        var registry = new Registry();
        var oldSnapshot = registry.snapshot();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var reader = executor.submit(() -> {
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
                while (System.nanoTime() - deadline < 0) {
                    Limits observed = registry.snapshot();
                    if (observed.byTenant().containsKey("tenant-a")) {
                        return observed.byTenant().get("tenant-a");
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException("Reader cancelled");
                    }
                    Thread.onSpinWait();
                }
                throw new AssertionError("Reader deadline exceeded");
            });
            try {
                registry.replace(new Limits(Map.of("tenant-a", 3)));
                if (reader.get(10, TimeUnit.SECONDS) != 3) {
                    throw new AssertionError("Published snapshot contents");
                }
                if (!oldSnapshot.byTenant().isEmpty()) {
                    throw new AssertionError("Prior snapshot must remain unchanged");
                }
            } finally {
                reader.cancel(true);
            }
        }
        System.out.println("SnapshotLab: all checks passed");
    }
}