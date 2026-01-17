package splitwise.exception;

/**
 * Exception thrown when a split operation is invalid.
 */
public class InvalidSplitException extends Exception {

    public InvalidSplitException(String message) {
        super(message);
    }

    public InvalidSplitException(String message, Throwable cause) {
        super(message, cause);
    }
}
