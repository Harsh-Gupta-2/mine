import java.util.Locale;
import java.util.Objects;

public class Main {
    record Money(String currency, long minorUnits) {
        Money {
            Objects.requireNonNull(currency);
            currency = currency.toUpperCase(Locale.ROOT);
            if (!currency.matches("[A-Z]{3}")) throw new IllegalArgumentException("currency must be ISO-like");
        }
        Money plus(Money other) {
            if (!currency.equals(other.currency)) throw new IllegalArgumentException("currency mismatch");
            return new Money(currency, Math.addExact(minorUnits, other.minorUnits));
        }
    }
    public static void main(String[] args) {
        Money total = new Money("inr", 125).plus(new Money("INR", 75));
        if (!total.equals(new Money("INR", 200))) throw new AssertionError(total);
        try { total.plus(new Money("USD", 1)); throw new AssertionError("mixed currency"); }
        catch (IllegalArgumentException expected) { System.out.println("Mixed currency rejected."); }
        try { new Money("IN", 1); throw new AssertionError("invalid currency"); }
        catch (IllegalArgumentException expected) { System.out.println("Invalid currency rejected."); }
        System.out.println("Immutable value object: " + total);
    }
}