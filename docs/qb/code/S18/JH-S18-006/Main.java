public class Main {
    enum RuntimeConfig {
        INSTANCE;
        private final String mode = "interview";
        String mode() { return mode; }
    }
    public static void main(String[] args) {
        RuntimeConfig first = RuntimeConfig.INSTANCE;
        RuntimeConfig second = RuntimeConfig.INSTANCE;
        if (first != second || !"interview".equals(first.mode())) throw new AssertionError("singleton");
        System.out.println("Enum singleton identity and state assertions passed.");
    }
}