import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {
    record Employee(String name, String department, double salary) {}

    public static void main(String[] args) {
        List<Employee> employees = List.of(
                new Employee("Asha", "IT", 90000),
                new Employee("Ravi", "IT", 70000),
                new Employee("Meera", "HR", 50000),
                new Employee("Kabir", "Finance", 80000),
                new Employee("Nina", "HR", 55000));

        double itTotal = employees.stream()
                .filter(e -> "IT".equals(e.department()))
                .mapToDouble(Employee::salary)
                .sum();
        System.out.println("IT total: " + String.format(Locale.ROOT, "%.1f", itTotal));

        Map<String, Double> byDepartment = employees.stream()
                .collect(Collectors.groupingBy(Employee::department, TreeMap::new,
                        Collectors.summingDouble(Employee::salary)));
        byDepartment.forEach((dept, total) -> System.out.println(dept + " total: " + String.format(Locale.ROOT, "%.1f", total)));

        Map<String, DoubleSummaryStatistics> stats = employees.stream()
                .collect(Collectors.groupingBy(Employee::department, TreeMap::new,
                        Collectors.summarizingDouble(Employee::salary)));
        DoubleSummaryStatistics hr = stats.get("HR");
        System.out.println("HR count=" + hr.getCount() + " min=" + String.format(Locale.ROOT, "%.1f", hr.getMin())
                + " avg=" + String.format(Locale.ROOT, "%.1f", hr.getAverage()) + " max=" + String.format(Locale.ROOT, "%.1f", hr.getMax()));
    }
}
