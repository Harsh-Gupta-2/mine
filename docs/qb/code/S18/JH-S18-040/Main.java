import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Main {
    record Event(long sequence,String payload) {}
    static final class Sequencer {
        private final AtomicLong next=new AtomicLong();
        Event create(String payload){if(payload==null||payload.isBlank())throw new IllegalArgumentException("payload required");return new Event(next.incrementAndGet(),payload);}
    }
    public static void main(String[] args){Sequencer sequencer=new Sequencer();Event first=sequencer.create("created"),second=sequencer.create("paid");
        if(first.sequence()!=1||second.sequence()!=2)throw new AssertionError("sequence");
        try{sequencer.create(" ");throw new AssertionError("blank accepted");}catch(IllegalArgumentException expected){System.out.println("Blank event rejected.");}
        if(sequencer.create("shipped").sequence()!=3)throw new AssertionError("rejected event consumed sequence");
        System.out.println("Ordered events: "+List.of(first,second));}
}