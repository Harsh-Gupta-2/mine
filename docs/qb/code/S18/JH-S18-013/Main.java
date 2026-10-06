import java.util.ArrayList;
import java.util.List;

public class Main {
    static final class Batcher<T> {
        private final int limit; private final List<T> pending = new ArrayList<>(); private final List<List<T>> written = new ArrayList<>();
        Batcher(int limit) { if (limit < 1) throw new IllegalArgumentException(); this.limit = limit; }
        void add(T item) { pending.add(item); if (pending.size() == limit) flush(); }
        void flush() { if (!pending.isEmpty()) { written.add(List.copyOf(pending)); pending.clear(); } }
        List<List<T>> result() { return List.copyOf(written); }
    }
    public static void main(String[] args) {
        Batcher<Integer> batcher = new Batcher<>(2);
        batcher.add(1); batcher.add(2); batcher.add(3); batcher.flush(); batcher.flush();
        if (!batcher.result().equals(List.of(List.of(1,2), List.of(3)))) throw new AssertionError(batcher.result());
        try { new Batcher<String>(0); throw new AssertionError("zero limit"); }
        catch (IllegalArgumentException expected) { System.out.println("Invalid batch limit rejected."); }
        System.out.println("Flushed batches: " + batcher.result());
    }
}