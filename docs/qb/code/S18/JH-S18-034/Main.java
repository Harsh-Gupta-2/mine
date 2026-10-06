import java.util.concurrent.Executor;

public class Main {
    static final ThreadLocal<String> TRACE=new ThreadLocal<>();
    static Runnable capture(Runnable task){String captured=TRACE.get();return ()->{String previous=TRACE.get();try{if(captured==null)TRACE.remove();else TRACE.set(captured);task.run();}finally{if(previous==null)TRACE.remove();else TRACE.set(previous);}};}
    static final class ContextExecutor implements Executor {private final Executor delegate;ContextExecutor(Executor delegate){this.delegate=delegate;}public void execute(Runnable task){delegate.execute(capture(task));}}
    public static void main(String[] args){String[] observed={null};TRACE.set("request-7");new ContextExecutor(Runnable::run).execute(()->observed[0]=TRACE.get());
        if(!"request-7".equals(observed[0])||!"request-7".equals(TRACE.get()))throw new AssertionError("context");TRACE.remove();
        new ContextExecutor(Runnable::run).execute(()->observed[0]=TRACE.get());if(observed[0]!=null||TRACE.get()!=null)throw new AssertionError("stale context");
        System.out.println("Captured trace restored and absent context cleared.");}
}