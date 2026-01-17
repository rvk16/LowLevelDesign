package splitwise.model;

/**
 * Enum representing supported currencies.
 */
public enum Currency {
    USD("US Dollar", "$"),
    EUR("Euro", "€"),
    INR("Indian Rupee", "₹"),
    GBP("British Pound", "£"),
    JPY("Japanese Yen", "¥");

    private final String displayName;
    private final String symbol;

    Currency(String displayName, String symbol) {
        this.displayName = displayName;
        this.symbol = symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSymbol() {
        return symbol;
    }

    public String format(double amount) {
        return symbol + String.format("%.2f", amount);
    }
}
