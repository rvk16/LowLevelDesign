package splitwise.exception;

/**
 * Exception thrown when a user is not found in the system.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("User not found with ID: " + userId);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
