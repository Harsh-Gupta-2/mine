import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    static final class Pool {
        private final ArrayBlockingQueue<String> available;
        Pool(String... resources) { available = new ArrayBlockingQueue<>(resources.length); for (String r : resources) available.add(r); }
        Lease acquire() { String resource = available.poll(); return resource == null ? null : new Lease(resource); }
        final class Lease implements AutoCloseable {
            private String resource;
            Lease(String resource) { this.resource = resource; }
            String value() { if (resource == null) throw new IllegalStateException("lease closed"); return resource; }
            public void close() { if (resource != null) { String returned = resource; resource = null; if (!available.offer(returned)) throw new IllegalStateException("double return"); } }
        }
    }
    public static void main(String[] args) {
        Pool pool = new Pool("connection-1"); Pool.Lease lease = pool.acquire();
        if (!"connection-1".equals(lease.value()) || pool.acquire() != null) throw new AssertionError("exhaustion");
        lease.close(); lease.close();
        Pool.Lease reused = pool.acquire();
        if (!"connection-1".equals(reused.value())) throw new AssertionError("reuse");
        reused.close();
        try { reused.value(); throw new AssertionError("closed lease usable"); }
        catch (IllegalStateException expected) { System.out.println("Closed lease rejected; resource returned once."); }
    }
}