import java.util.function.LongSupplier;

public class Main {
    record Deadline(long expiresAt,LongSupplier clock) {
        Deadline { if(expiresAt<0)throw new IllegalArgumentException("negative deadline"); }
        long remainingNanos(){return Math.max(0,expiresAt-clock.getAsLong());}
        Deadline child(long maxNanos){if(maxNanos<0)throw new IllegalArgumentException("negative child budget");return new Deadline(clock.getAsLong()+Math.min(remainingNanos(),maxNanos),clock);}
        void requireTime(){if(remainingNanos()==0)throw new IllegalStateException("deadline expired");}
    }
    public static void main(String[] args){long[] now={10};Deadline request=new Deadline(100,()->now[0]);Deadline child=request.child(30);
        if(child.remainingNanos()!=30||request.remainingNanos()!=90)throw new AssertionError("budget clamp");now[0]=100;
        if(request.remainingNanos()!=0)throw new AssertionError("expiry");try{request.requireTime();throw new AssertionError("expired request allowed");}catch(IllegalStateException expected){System.out.println("Expired request rejected.");}
        try{request.child(-1);throw new AssertionError("negative budget");}catch(IllegalArgumentException expected){System.out.println("Negative child budget rejected.");}
        System.out.println("Child deadline remaining: "+child.remainingNanos()+" ns");}
}