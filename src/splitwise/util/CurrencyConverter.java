package splitwise.util;

import splitwise.exception.InvalidCurrencyException;
import splitwise.model.Currency;

import java.util.EnumMap;
import java.util.Map;

/**
 * Utility class for currency conversion operations.
 * Maintains exchange rates and provides conversion functionality.
 */
public class CurrencyConverter {
    // Exchange rates relative to USD (base currency)
    private final Map<Currency, Map<Currency, Double>> exchangeRates;

    public CurrencyConverter() {
        this.exchangeRates = new EnumMap<>(Currency.class);
        initializeDefaultRates();
    }

    /**
     * Initializes default exchange rates.
     * Rates are relative - e.g., 1 USD = 0.85 EUR, 1 USD = 83 INR
     */
    private void initializeDefaultRates() {
        // Initialize rate maps for each currency
        for (Currency currency : Currency.values()) {
            exchangeRates.put(currency, new EnumMap<>(Currency.class));
        }

        // Set default rates (as of typical market rates)
        // USD base rates
        setRate(Currency.USD, Currency.USD, 1.0);
        setRate(Currency.USD, Currency.EUR, 0.85);
        setRate(Currency.USD, Currency.INR, 83.0);
        setRate(Currency.USD, Currency.GBP, 0.73);
        setRate(Currency.USD, Currency.JPY, 110.0);

        // EUR base rates
        setRate(Currency.EUR, Currency.USD, 1.18);
        setRate(Currency.EUR, Currency.EUR, 1.0);
        setRate(Currency.EUR, Currency.INR, 97.65);
        setRate(Currency.EUR, Currency.GBP, 0.86);
        setRate(Currency.EUR, Currency.JPY, 129.4);

        // INR base rates
        setRate(Currency.INR, Currency.USD, 0.012);
        setRate(Currency.INR, Currency.EUR, 0.0102);
        setRate(Currency.INR, Currency.INR, 1.0);
        setRate(Currency.INR, Currency.GBP, 0.0088);
        setRate(Currency.INR, Currency.JPY, 1.33);

        // GBP base rates
        setRate(Currency.GBP, Currency.USD, 1.37);
        setRate(Currency.GBP, Currency.EUR, 1.16);
        setRate(Currency.GBP, Currency.INR, 113.7);
        setRate(Currency.GBP, Currency.GBP, 1.0);
        setRate(Currency.GBP, Currency.JPY, 150.7);

        // JPY base rates
        setRate(Currency.JPY, Currency.USD, 0.0091);
        setRate(Currency.JPY, Currency.EUR, 0.0077);
        setRate(Currency.JPY, Currency.INR, 0.75);
        setRate(Currency.JPY, Currency.GBP, 0.0066);
        setRate(Currency.JPY, Currency.JPY, 1.0);
    }

    private void setRate(Currency from, Currency to, double rate) {
        exchangeRates.get(from).put(to, rate);
    }

    /**
     * Converts an amount from one currency to another.
     *
     * @param amount The amount to convert
     * @param from   The source currency
     * @param to     The target currency
     * @return The converted amount
     */
    public double convert(double amount, Currency from, Currency to) {
        if (from == to) {
            return amount;
        }

        double rate = getExchangeRate(from, to);
        return Math.round(amount * rate * 100.0) / 100.0;
    }

    /**
     * Gets the exchange rate from one currency to another.
     *
     * @param from The source currency
     * @param to   The target currency
     * @return The exchange rate
     */
    public double getExchangeRate(Currency from, Currency to) {
        Map<Currency, Double> fromRates = exchangeRates.get(from);
        if (fromRates == null || !fromRates.containsKey(to)) {
            throw new InvalidCurrencyException(from.name(), to.name());
        }
        return fromRates.get(to);
    }

    /**
     * Sets or updates an exchange rate.
     *
     * @param from The source currency
     * @param to   The target currency
     * @param rate The exchange rate
     */
    public void setExchangeRate(Currency from, Currency to, double rate) {
        if (rate <= 0) {
            throw new IllegalArgumentException("Exchange rate must be positive");
        }
        exchangeRates.get(from).put(to, rate);
    }

    /**
     * Formats an amount with its currency symbol.
     *
     * @param amount   The amount to format
     * @param currency The currency
     * @return Formatted string (e.g., "$100.00")
     */
    public String format(double amount, Currency currency) {
        return currency.format(amount);
    }

    /**
     * Prints all available exchange rates for a currency.
     */
    public void printRates(Currency from) {
        System.out.println("Exchange rates from " + from.getDisplayName() + ":");
        Map<Currency, Double> rates = exchangeRates.get(from);
        for (Map.Entry<Currency, Double> entry : rates.entrySet()) {
            if (entry.getKey() != from) {
                System.out.printf("  1 %s = %.4f %s%n",
                        from.name(), entry.getValue(), entry.getKey().name());
            }
        }
    }
}
