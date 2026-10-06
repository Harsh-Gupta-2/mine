import java.util.function.LongSupplier;

public class Main {
    static final class Breaker {
        enum State { CLOSED, OPEN, HALF_OPEN }
        private final int threshold; private final long resetAfter; private final LongSupplier clock;
        private State state=State.CLOSED; private int failures; private long openedAt;
        Breaker(int threshold,long resetAfter,LongSupplier clock){if(threshold<1||resetAfter<1)throw new IllegalArgumentException();this.threshold=threshold;this.resetAfter=resetAfter;this.clock=clock;}
        boolean permit(){if(state==State.OPEN && clock.getAsLong()-openedAt>=resetAfter)state=State.HALF_OPEN;return state!=State.OPEN;}
        void success(){state=State.CLOSED;failures=0;}
        void failure(){if(state==State.HALF_OPEN||++failures>=threshold){state=State.OPEN;openedAt=clock.getAsLong();}}
        State state(){return state;}
    }
    public static void main(String[] args){
        long[] now={0}; Breaker b=new Breaker(2,10,()->now[0]); b.failure(); if(!b.permit())throw new AssertionError(); b.failure();
        if(b.permit()||b.state()!=Breaker.State.OPEN)throw new AssertionError("not open"); now[0]=10;
        if(!b.permit()||b.state()!=Breaker.State.HALF_OPEN)throw new AssertionError("not half-open"); b.success();
        if(b.state()!=Breaker.State.CLOSED||!b.permit())throw new AssertionError("not closed");
        System.out.println("Circuit breaker open, probe-success and recovery assertions passed.");
    }
}