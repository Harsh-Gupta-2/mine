import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {
    record Order(String customer, long cents, boolean paid) {}

    static Map<String, Long> paidTotals(List<Order> orders) {
        return orders.stream()
                .filter(Order::paid)
                .collect(Collectors.toMap(Order::customer, Order::cents,
                        Math::addExact, TreeMap::new));
    }

    public static void main(String[] args) {
        Map<String, Long> totals = paidTotals(List.of(
                new Order("alpha", 100, true),
                new Order("alpha", 250, true),
                new Order("alpha", 900, false),
                new Order("beta", 200, true)));
        if (!totals.equals(Map.of("alpha", 350L, "beta", 200L))) throw new AssertionError("totals");
        if (!paidTotals(List.of()).isEmpty()) throw new AssertionError("empty input");
        try {
            paidTotals(List.of(new Order("alpha", Long.MAX_VALUE, true), new Order("alpha", 1, true)));
            throw new AssertionError("overflow accepted");
        } catch (ArithmeticException expected) {
            System.out.println("Overflow rejected.");
        }
        System.out.println("Paid totals in cents: " + totals);
        System.out.println("Aggregation assertions passed.");
    }
}