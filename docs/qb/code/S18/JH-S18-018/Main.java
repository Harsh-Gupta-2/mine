public class Main {
    static final class RetryPolicy {
        private final int maxAttempts; private final long initial, maximum;
        RetryPolicy(int maxAttempts,long initial,long maximum){if(maxAttempts<1||initial<0||maximum<initial)throw new IllegalArgumentException();this.maxAttempts=maxAttempts;this.initial=initial;this.maximum=maximum;}
        long delayBeforeAttempt(int attempt){if(attempt<2||attempt>maxAttempts)throw new IllegalArgumentException("not a retry attempt");long delay=initial;for(int i=2;i<attempt&&delay<maximum;i++)delay=delay>maximum/2?maximum:Math.min(maximum,delay*2);return delay;}
    }
    public static void main(String[] args){RetryPolicy policy=new RetryPolicy(5,100,350);
        if(policy.delayBeforeAttempt(2)!=100||policy.delayBeforeAttempt(3)!=200||policy.delayBeforeAttempt(4)!=350||policy.delayBeforeAttempt(5)!=350)throw new AssertionError("backoff");
        try{policy.delayBeforeAttempt(1);throw new AssertionError("first attempt has retry delay");}catch(IllegalArgumentException expected){System.out.println("Invalid retry attempt rejected.");}
        try{new RetryPolicy(0,10,5);throw new AssertionError("bad policy accepted");}catch(IllegalArgumentException expected){System.out.println("Invalid retry policy rejected.");}
        System.out.println("Capped retry delays: 100, 200, 350, 350 ms");}
}