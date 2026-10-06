import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.LongSupplier;

public class Main {
    record Task(String key, long due, long sequence, Runnable action) {}
    static final class Scheduler {
        private final LongSupplier clock; private long sequence;
        private final Map<String, Task> current = new HashMap<>();
        private final PriorityQueue<Task> queue = new PriorityQueue<>((a,b) -> a.due()!=b.due()?Long.compare(a.due(),b.due()):Long.compare(a.sequence(),b.sequence()));
        Scheduler(LongSupplier clock) { this.clock = clock; }
        void schedule(String key, long delay, Runnable action) { if (delay < 0) throw new IllegalArgumentException(); Task t=new Task(key,clock.getAsLong()+delay,sequence++,action); current.put(key,t); queue.add(t); }
        int runDue() { int ran=0; while (!queue.isEmpty() && queue.peek().due()<=clock.getAsLong()) { Task t=queue.remove(); if (current.remove(t.key(),t)) { t.action().run(); ran++; } } return ran; }
    }
    public static void main(String[] args) {
        long[] now={0}; int[] calls={0}; Scheduler scheduler=new Scheduler(()->now[0]);
        scheduler.schedule("job",5,()->calls[0]=1); scheduler.schedule("job",8,()->calls[0]=2);
        now[0]=5; if(scheduler.runDue()!=0) throw new AssertionError("stale task ran");
        now[0]=8; if(scheduler.runDue()!=1 || calls[0]!=2) throw new AssertionError("latest task missing");
        if(scheduler.runDue()!=0) throw new AssertionError("duplicate run");
        System.out.println("Deduplicated schedule ran latest task once: " + calls[0]);
    }
}