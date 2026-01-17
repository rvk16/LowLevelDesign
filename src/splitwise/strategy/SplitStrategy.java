package splitwise.strategy;

import splitwise.exception.InvalidSplitException;
import splitwise.model.User;
import splitwise.model.split.Split;

import java.util.List;

/**
 * Strategy interface for different split calculation methods.
 * Open/Closed Principle: New split types can be added without modifying existing code.
 * Interface Segregation: Only defines methods needed for split calculation.
 */
public interface SplitStrategy {

    /**
     * Validates that the splits are valid for the given total amount.
     *
     * @param totalAmount The total expense amount
     * @param splits      The list of splits to validate
     * @throws InvalidSplitException if validation fails
     */
    void validate(double totalAmount, List<Split> splits) throws InvalidSplitException;

    /**
     * Calculates and sets the amount for each split based on the total amount.
     *
     * @param totalAmount The total expense amount
     * @param splits      The list of splits to calculate amounts for
     */
    void calculateSplits(double totalAmount, List<Split> splits);

    /**
     * Creates splits for the given users with equal distribution.
     * This is a convenience method for creating initial splits.
     *
     * @param totalAmount The total expense amount
     * @param users       The list of users to split among
     * @return List of calculated splits
     */
    List<Split> createSplits(double totalAmount, List<User> users);
}
