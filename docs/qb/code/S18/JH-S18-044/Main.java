import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class Main {
    static List<Integer> removeAdjacentDuplicates(int[] values) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int value : values) {
            if (!stack.isEmpty() && stack.peek() == value) stack.pop();
            else stack.push(value);
        }
        List<Integer> result = new ArrayList<>(stack);
        Collections.reverse(result);
        return result;
    }

    public static void main(String[] args) {
        int[][] inputs = {{1, 2, 2, 3, 3, 1}, {1, 2, 5, 4, 2, 3}, {1, 1, 1}, {}};
        for (int[] input : inputs) {
            System.out.println(Arrays.toString(input) + " -> " + removeAdjacentDuplicates(input));
        }
    }
}
