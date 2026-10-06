import java.util.ArrayList;
import java.util.List;

public final class LoadingLab {
    private static final List<String> events = new ArrayList<>();

    static final class Plugin {
        static {
            events.add("initialized");
        }
    }

    static final class BrokenPlugin {
        static final int CONFIG = readConfig();

        private static int readConfig() {
            throw new IllegalStateException("missing teaching configuration");
        }
    }

    public static void main(String[] args) throws Exception {
        ClassLoader loader = LoadingLab.class.getClassLoader();
        Class<?> loaded = Class.forName("LoadingLab$Plugin", false, loader);
        if (!events.isEmpty()) {
            throw new AssertionError("Loading alone must not initialize Plugin");
        }
        Class<?> initialized = Class.forName("LoadingLab$Plugin", true, loader);
        if (loaded != initialized || !events.equals(List.of("initialized"))) {
            throw new AssertionError("One class identity and one initialization");
        }
        Class.forName("LoadingLab$Plugin", true, loader);
        if (events.size() != 1) {
            throw new AssertionError("Initialization must not repeat");
        }
        try {
            Class.forName("LoadingLab$BrokenPlugin", true, loader);
            throw new AssertionError("First initialization must fail");
        } catch (ExceptionInInitializerError failure) {
            if (!(failure.getCause() instanceof IllegalStateException)) {
                throw new AssertionError("Original failure cause is preserved", failure);
            }
        }
        try {
            Class.forName("LoadingLab$BrokenPlugin", true, loader);
            throw new AssertionError("Erroneous class must not initialize again");
        } catch (NoClassDefFoundError expected) {
            System.out.println("LoadingLab: all checks passed");
        }
    }
}