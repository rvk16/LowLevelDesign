package splitwise.exception;

/**
 * Exception thrown when a currency conversion or operation is invalid.
 */
public class InvalidCurrencyException extends RuntimeException {

    public InvalidCurrencyException(String message) {
        super(message);
    }

    public InvalidCurrencyException(String fromCurrency, String toCurrency) {
        super("Cannot convert from " + fromCurrency + " to " + toCurrency + ": exchange rate not available");
    }

    public InvalidCurrencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
