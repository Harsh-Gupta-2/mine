import java.util.function.LongSupplier;

public class Main {
    static final class FixedWindow {
        private final int limit; private final long width; private final LongSupplier clock;
        private long window = Long.MIN_VALUE; private int used;
        FixedWindow(int limit,long width,LongSupplier clock) { if(limit<1||width<1) throw new IllegalArgumentException(); this.limit=limit;this.width=width;this.clock=clock; }
        synchronized boolean allow() { long current=Math.floorDiv(clock.getAsLong(),width); if(current!=window){window=current;used=0;} if(used==limit)return false; used++; return true; }
    }
    public static void main(String[] args) {
        long[] now={0}; FixedWindow limit=new FixedWindow(2,10,()->now[0]);
        if(!limit.allow()||!limit.allow()||limit.allow()) throw new AssertionError("limit");
        now[0]=10; if(!limit.allow()) throw new AssertionError("boundary reset");
        now[0]=-1; if(!limit.allow()) throw new AssertionError("previous window reset");
        System.out.println("Fixed-window limit and window-boundary assertions passed.");
    }
}