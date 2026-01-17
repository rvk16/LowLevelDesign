package splitwise.model;

/**
 * Enum representing different types of activities in the system.
 */
public enum ActivityType {
    EXPENSE_ADDED("added an expense"),
    EXPENSE_DELETED("deleted an expense"),
    EXPENSE_UPDATED("updated an expense"),
    GROUP_CREATED("created a group"),
    MEMBER_ADDED("added a member"),
    MEMBER_REMOVED("removed a member"),
    SETTLED("settled up"),
    PAYMENT_MADE("made a payment");

    private final String description;

    ActivityType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
