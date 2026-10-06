import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Main {
    record Event(String id,String body) {}
    static final class Outbox {
        private final Map<Long,Event> pending=new java.util.TreeMap<>();private long next=1;
        synchronized long append(Event event){long id=next++;pending.put(id,event);return id;}
        synchronized int relay(Set<String> receiver,boolean loseFirstAck){int delivered=0;boolean lose=loseFirstAck;Iterator<Map.Entry<Long,Event>> iterator=pending.entrySet().iterator();while(iterator.hasNext()){Event e=iterator.next().getValue();receiver.add(e.id());if(lose){lose=false;continue;}delivered++;iterator.remove();}return delivered;}
        synchronized int size(){return pending.size();}
    }
    public static void main(String[] args){Outbox outbox=new Outbox();outbox.append(new Event("evt-1","created"));outbox.append(new Event("evt-2","paid"));Set<String> receiver=new HashSet<>();
        int firstDelivery=outbox.relay(receiver,true);if(receiver.size()!=2||firstDelivery!=1||outbox.size()!=1)throw new AssertionError("lost acknowledgement");outbox.append(new Event("evt-3","shipped"));int replayDelivery=outbox.relay(receiver,false);
        if(replayDelivery!=2||receiver.size()!=3||outbox.size()!=0)throw new AssertionError("relay replay");
        System.out.println("Idempotent receiver events after ack-loss replay: "+new java.util.TreeSet<>(receiver)+"; deliveries="+firstDelivery+"+"+replayDelivery);}
}