import java.util.Arrays;

public class Main {
    static int comparisons;

    static boolean less(int a, int b) {
        comparisons++;
        return a < b;
    }

    static int[] minMax(int[] a) {
        int n = a.length, min, max, i;
        if (n % 2 == 0) {
            if (less(a[0], a[1])) { min = a[0]; max = a[1]; } else { min = a[1]; max = a[0]; }
            i = 2;
        } else {
            min = max = a[0];
            i = 1;
        }
        while (i < n - 1) {
            int lo, hi;
            if (less(a[i], a[i + 1])) { lo = a[i]; hi = a[i + 1]; } else { lo = a[i + 1]; hi = a[i]; }
            if (less(lo, min)) min = lo;
            if (less(max, hi)) max = hi;
            i += 2;
        }
        return new int[]{min, max};
    }

    public static void main(String[] args) {
        int[][] inputs = {{3, 5, 1, 9, 2, 8}, {7, 2, 9, 4, 6, 1, 5}};
        for (int[] input : inputs) {
            comparisons = 0;
            int[] result = minMax(input);
            int n = input.length;
            System.out.println(Arrays.toString(input) + " min=" + result[0] + " max=" + result[1]
                    + " pairwise comparisons=" + comparisons + " naive=" + 2 * (n - 1));
        }
    }
}
