import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Main {
    static final class Dispatcher<T> {
        private final CopyOnWriteArrayList<Consumer<T>> listeners=new CopyOnWriteArrayList<>();
        void add(Consumer<T> listener){listeners.add(listener);}void remove(Consumer<T> listener){listeners.remove(listener);}
        int publish(T event){int failures=0;for(Consumer<T> listener:listeners)try{listener.accept(event);}catch(RuntimeException failure){failures++;}return failures;}
        int size(){return listeners.size();}
    }
    public static void main(String[] args){Dispatcher<String> bus=new Dispatcher<>();StringBuilder seen=new StringBuilder();Consumer<String> later=seen::append;
        bus.add(event->{seen.append("first-");bus.remove(later);});bus.add(event->{throw new IllegalStateException("subscriber failure");});bus.add(later);
        if(bus.publish("event")!=1||!seen.toString().equals("first-event")||bus.size()!=2)throw new AssertionError(seen);
        if(bus.publish("next")!=1||!seen.toString().equals("first-eventfirst-"))throw new AssertionError("snapshot semantics");
        System.out.println("Listener failure isolated; active listeners: "+bus.size());}
}