import java.util.concurrent.atomic.AtomicReference;

public class Main {
    record Config(long version,String endpoint,int timeoutMillis) {}
    static final class ConfigStore {
        private final AtomicReference<Config> current;
        ConfigStore(Config initial){current=new AtomicReference<>(initial);}
        Config snapshot(){return current.get();}
        boolean replace(long expectedVersion,Config next){Config old=current.get();if(old.version()!=expectedVersion)return false;if(next.version()<=expectedVersion)throw new IllegalArgumentException("invalid version");return current.compareAndSet(old,next);}
    }
    public static void main(String[] args){ConfigStore store=new ConfigStore(new Config(1,"local",100));Config before=store.snapshot();
        if(!store.replace(1,new Config(2,"service",250))||!before.endpoint().equals("local"))throw new AssertionError("snapshot");
        if(store.replace(1,new Config(3,"stale",300)))throw new AssertionError("stale update won");
        try{store.replace(2,new Config(2,"bad",1));throw new AssertionError("nonincreasing version accepted");}catch(IllegalArgumentException expected){System.out.println("Nonincreasing config version rejected.");}
        System.out.println("Atomic config snapshot: "+store.snapshot());}
}