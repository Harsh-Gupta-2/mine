import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

public class Main {
    record Job(String id,int attempts) {}
    static final class Worker {
        private final int maxAttempts;private final Queue<Job> ready=new ArrayDeque<>(),dead=new ArrayDeque<>();
        Worker(int maxAttempts){if(maxAttempts<1)throw new IllegalArgumentException();this.maxAttempts=maxAttempts;}
        void submit(String id){ready.add(new Job(id,0));}
        void drain(Predicate<Job> succeeds){while(!ready.isEmpty()){Job job=ready.remove();if(succeeds.test(job))continue;Job failed=new Job(job.id(),job.attempts()+1);if(failed.attempts()<maxAttempts)ready.add(failed);else dead.add(failed);}}
        List<Job> deadLetters(){return List.copyOf(dead);}
    }
    public static void main(String[] args){Worker worker=new Worker(3);worker.submit("event-a");worker.submit("event-b");worker.drain(job->job.id().equals("event-b"));
        if(!worker.deadLetters().equals(List.of(new Job("event-a",3))))throw new AssertionError(worker.deadLetters());
        try{new Worker(0);throw new AssertionError("zero retries");}catch(IllegalArgumentException expected){System.out.println("Invalid retry bound rejected.");}
        System.out.println("Dead-lettered after bounded attempts: "+worker.deadLetters());}
}