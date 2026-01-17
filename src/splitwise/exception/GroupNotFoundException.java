package splitwise.exception;

/**
 * Exception thrown when a group is not found in the system.
 */
public class GroupNotFoundException extends RuntimeException {

    public GroupNotFoundException(String groupId) {
        super("Group not found with ID: " + groupId);
    }

    public GroupNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
