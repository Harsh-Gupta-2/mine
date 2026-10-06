import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {
    record Employee(int empId, int deptId, String designation) {}
    record Department(int deptId, String departmentName) {}

    public static void main(String[] args) {
        List<Department> departments = List.of(
                new Department(1, "Engineering"), new Department(2, "HR"), new Department(3, "Finance"));
        List<Employee> employees = List.of(
                new Employee(101, 1, "Developer"), new Employee(102, 1, "Developer"), new Employee(103, 1, "Tester"),
                new Employee(104, 2, "Recruiter"), new Employee(105, 2, "Manager"),
                new Employee(106, 3, "Analyst"));

        Map<Integer, Long> countByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::deptId, TreeMap::new, Collectors.counting()));
        System.out.println("headcount by department id: " + countByDept);

        int topDeptId = countByDept.entrySet().stream()
                .max(Map.Entry.<Integer, Long>comparingByValue().thenComparing(Map.Entry.<Integer, Long>comparingByKey().reversed()))
                .map(Map.Entry::getKey)
                .orElseThrow();

        String name = departments.stream().filter(d -> d.deptId() == topDeptId)
                .map(Department::departmentName).findFirst().orElse("UNKNOWN");
        List<String> designations = employees.stream().filter(e -> e.deptId() == topDeptId)
                .map(Employee::designation).distinct().toList();
        System.out.println("largest department: " + name + " with designations " + designations);
    }
}
