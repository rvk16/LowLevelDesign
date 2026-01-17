package splitwise.service;

import splitwise.model.Currency;

/**
 * Service interface for currency conversion operations.
 * Open/Closed: New currencies can be added without modifying existing code.
 */
public interface CurrencyService {

    /**
     * Converts an amount from one currency to another.
     */
    double convert(double amount, Currency from, Currency to);

    /**
     * Updates the exchange rate between two currencies.
     */
    void updateExchangeRate(Currency from, Currency to, double rate);

    /**
     * Gets the exchange rate between two currencies.
     */
    double getExchangeRate(Currency from, Currency to);

    /**
     * Checks if conversion is available between two currencies.
     */
    boolean isConversionAvailable(Currency from, Currency to);

    /**
     * Formats an amount in a specific currency.
     */
    String format(double amount, Currency currency);
}
