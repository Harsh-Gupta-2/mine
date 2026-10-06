import java.util.ArrayDeque;
import java.util.function.LongSupplier;

public class Main {
    static final class SlidingWindow {
        private final int limit; private final long width; private final LongSupplier clock;
        private final ArrayDeque<Long> accepted = new ArrayDeque<>(); private long lastTime=Long.MIN_VALUE;
        SlidingWindow(int limit,long width,LongSupplier clock){if(limit<1||width<1)throw new IllegalArgumentException();this.limit=limit;this.width=width;this.clock=clock;}
        synchronized boolean allow(){long now=clock.getAsLong();if(now<lastTime)throw new IllegalStateException("clock moved backwards");lastTime=now;while(!accepted.isEmpty()&&now-accepted.peekFirst()>=width)accepted.removeFirst();if(accepted.size()==limit)return false;accepted.addLast(now);return true;}
    }
    public static void main(String[] args){long[] now={0};SlidingWindow limit=new SlidingWindow(2,10,()->now[0]);
        if(!limit.allow()||!limit.allow()||limit.allow())throw new AssertionError("limit");now[0]=10;if(!limit.allow())throw new AssertionError("expiry boundary");
        now[0]=9;try{limit.allow();throw new AssertionError("clock rollback accepted");}catch(IllegalStateException expected){System.out.println("Clock rollback rejected.");}
        System.out.println("Sliding-window expiry and capacity assertions passed.");}
}