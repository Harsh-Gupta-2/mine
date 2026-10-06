import java.util.ArrayDeque;
import java.util.List;

public class Main {
    static final class EventBuffer<T> {
        private final int capacity;private final ArrayDeque<T> events=new ArrayDeque<>();private long dropped;
        EventBuffer(int capacity){if(capacity<1)throw new IllegalArgumentException();this.capacity=capacity;}
        synchronized void add(T event){if(event==null)throw new NullPointerException();if(events.size()==capacity){events.removeFirst();dropped++;}events.addLast(event);}
        synchronized List<T> snapshot(){return List.copyOf(events);}synchronized long dropped(){return dropped;}
    }
    public static void main(String[] args){EventBuffer<String> buffer=new EventBuffer<>(2);buffer.add("a");buffer.add("b");List<String> snapshot=buffer.snapshot();buffer.add("c");
        if(!buffer.snapshot().equals(List.of("b","c"))||buffer.dropped()!=1||!snapshot.equals(List.of("a","b")))throw new AssertionError("retention");
        try{new EventBuffer<String>(0);throw new AssertionError("zero capacity");}catch(IllegalArgumentException expected){System.out.println("Invalid buffer capacity rejected.");}
        System.out.println("Retained events: "+buffer.snapshot()+"; dropped: "+buffer.dropped());}
}