public class Main {
    static String smallest(String digits) {
        int[] count = counts(digits);
        StringBuilder sb = new StringBuilder();
        for (int d = 0; d <= 9; d++) sb.append(String.valueOf((char) ('0' + d)).repeat(count[d]));
        return sb.toString();
    }

    static String smallestNoLeadingZero(String digits) {
        int[] count = counts(digits);
        int first = -1;
        for (int d = 1; d <= 9; d++) {
            if (count[d] > 0) { first = d; break; }
        }
        if (first == -1) return "0";
        count[first]--;
        StringBuilder sb = new StringBuilder().append((char) ('0' + first));
        for (int d = 0; d <= 9; d++) sb.append(String.valueOf((char) ('0' + d)).repeat(count[d]));
        return sb.toString();
    }

    static String largest(String digits) {
        int[] count = counts(digits);
        StringBuilder sb = new StringBuilder();
        for (int d = 9; d >= 0; d--) sb.append(String.valueOf((char) ('0' + d)).repeat(count[d]));
        return sb.toString();
    }

    private static int[] counts(String digits) {
        int[] count = new int[10];
        for (char c : digits.toCharArray()) count[c - '0']++;
        return count;
    }

    public static void main(String[] args) {
        System.out.println("501224 smallest (zeros allowed): " + smallest("501224"));
        System.out.println("501224 smallest (no leading zero): " + smallestNoLeadingZero("501224"));
        System.out.println("501224 largest: " + largest("501224"));
        System.out.println("234 smallest: " + smallest("234"));
    }
}
