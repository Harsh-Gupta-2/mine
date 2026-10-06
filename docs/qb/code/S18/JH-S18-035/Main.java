import java.util.HashMap;
import java.util.Map;

public class Main {
    record Entry(String id,String account,long delta) {}
    static final class Ledger {
        private final Map<String,Long> balances=new HashMap<>();private final Map<String,Entry> entries=new HashMap<>();
        synchronized boolean post(Entry entry){Entry prior=entries.get(entry.id());if(prior!=null){if(!prior.equals(entry))throw new IllegalArgumentException("idempotency key conflict");return false;}long old=balances.getOrDefault(entry.account(),0L);long updated=Math.addExact(old,entry.delta());if(updated<0)throw new IllegalStateException("overdraft");balances.put(entry.account(),updated);entries.put(entry.id(),entry);return true;}
        synchronized long balance(String account){return balances.getOrDefault(account,0L);}
    }
    public static void main(String[] args){Ledger ledger=new Ledger();Entry credit=new Entry("1","acct",500),debit=new Entry("2","acct",-120);
        if(!ledger.post(credit)||!ledger.post(debit)||ledger.post(debit)||ledger.balance("acct")!=380)throw new AssertionError("posting/idempotency");
        try{ledger.post(new Entry("2","other",-120));throw new AssertionError("conflicting idempotency key");}catch(IllegalArgumentException expected){System.out.println("Conflicting idempotency key rejected.");}
        try{ledger.post(new Entry("3","acct",-500));throw new AssertionError("overdraft");}catch(IllegalStateException expected){System.out.println("Overdraft rejected without journal entry.");}
        if(ledger.balance("acct")!=380||!ledger.post(new Entry("3","acct",20)))throw new AssertionError("rollback");
        System.out.println("Ledger balance after replay-safe posting: "+ledger.balance("acct"));}
}