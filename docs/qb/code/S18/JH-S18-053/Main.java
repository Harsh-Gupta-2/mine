import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    static int iterations;

    static List<Integer> bruteForce(int n) {
        List<Integer> result = new ArrayList<>();
        iterations = 0;
        for (int i = 1; i <= n; i++) {
            iterations++;
            if (n % i == 0) result.add(i);
        }
        return result;
    }

    static List<Integer> viaSquareRoot(int n) {
        List<Integer> small = new ArrayList<>(), large = new ArrayList<>();
        iterations = 0;
        for (int i = 1; (long) i * i <= n; i++) {
            iterations++;
            if (n % i == 0) {
                small.add(i);
                if (i != n / i) large.add(n / i);
            }
        }
        Collections.reverse(large);
        small.addAll(large);
        return small;
    }

    public static void main(String[] args) {
        for (int n : new int[]{36, 28, 97}) {
            List<Integer> slow = bruteForce(n);
            int slowIterations = iterations;
            List<Integer> fast = viaSquareRoot(n);
            System.out.println(n + " -> " + fast + " | same result: " + slow.equals(fast)
                    + " | loop iterations " + slowIterations + " vs " + iterations);
        }
    }
}
