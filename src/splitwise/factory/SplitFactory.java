package splitwise.factory;

import splitwise.model.ExpenseType;
import splitwise.model.User;
import splitwise.model.split.EqualSplit;
import splitwise.model.split.ExactSplit;
import splitwise.model.split.PercentageSplit;
import splitwise.model.split.Split;
import splitwise.strategy.EqualSplitStrategy;
import splitwise.strategy.ExactSplitStrategy;
import splitwise.strategy.PercentageSplitStrategy;
import splitwise.strategy.SplitStrategy;

import java.util.List;

/**
 * Factory for creating Split objects based on expense type.
 * Factory Pattern: Centralizes object creation logic.
 * Open/Closed Principle: New split types can be added without modifying client code.
 */
public class SplitFactory {

    /**
     * Creates a single split of the specified type.
     *
     * @param type   The expense type determining the split type
     * @param user   The user for the split
     * @param amount The amount or percentage (depending on type)
     * @return The created split
     */
    public static Split createSplit(ExpenseType type, User user, double amount) {
        switch (type) {
            case EQUAL:
                return new EqualSplit(user);
            case EXACT:
                return new ExactSplit(user, amount);
            case PERCENTAGE:
                return new PercentageSplit(user, amount);
            default:
                throw new IllegalArgumentException("Unknown expense type: " + type);
        }
    }

    /**
     * Creates an equal split for a user.
     */
    public static EqualSplit createEqualSplit(User user) {
        return new EqualSplit(user);
    }

    /**
     * Creates an exact split for a user with a specified amount.
     */
    public static ExactSplit createExactSplit(User user, double amount) {
        return new ExactSplit(user, amount);
    }

    /**
     * Creates a percentage split for a user with a specified percentage.
     */
    public static PercentageSplit createPercentageSplit(User user, double percentage) {
        return new PercentageSplit(user, percentage);
    }

    /**
     * Gets the appropriate split strategy for the expense type.
     *
     * @param type The expense type
     * @return The corresponding split strategy
     */
    public static SplitStrategy getStrategy(ExpenseType type) {
        switch (type) {
            case EQUAL:
                return new EqualSplitStrategy();
            case EXACT:
                return new ExactSplitStrategy();
            case PERCENTAGE:
                return new PercentageSplitStrategy();
            default:
                throw new IllegalArgumentException("Unknown expense type: " + type);
        }
    }

    /**
     * Creates splits for multiple users with equal distribution.
     *
     * @param type        The expense type
     * @param totalAmount The total amount to split
     * @param users       The users to split among
     * @return List of splits
     */
    public static List<Split> createSplits(ExpenseType type, double totalAmount, List<User> users) {
        SplitStrategy strategy = getStrategy(type);
        return strategy.createSplits(totalAmount, users);
    }
}
