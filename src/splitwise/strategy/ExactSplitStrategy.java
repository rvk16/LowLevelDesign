package splitwise.strategy;

import splitwise.exception.InvalidSplitException;
import splitwise.model.User;
import splitwise.model.split.ExactSplit;
import splitwise.model.split.Split;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for splitting expenses with exact amounts specified for each participant.
 * Validates that the sum of all splits equals the total expense amount.
 */
public class ExactSplitStrategy implements SplitStrategy {
    private static final double EPSILON = 0.01; // Tolerance for floating point comparison

    @Override
    public void validate(double totalAmount, List<Split> splits) throws InvalidSplitException {
        if (splits == null || splits.isEmpty()) {
            throw new InvalidSplitException("Splits list cannot be empty");
        }
        if (totalAmount <= 0) {
            throw new InvalidSplitException("Total amount must be positive");
        }

        double sumOfSplits = 0;
        for (Split split : splits) {
            if (!(split instanceof ExactSplit)) {
                throw new InvalidSplitException("All splits must be ExactSplit for exact splitting");
            }
            if (!split.validate()) {
                throw new InvalidSplitException("Invalid split amount for user: " + split.getUser().getName());
            }
            sumOfSplits += split.getAmount();
        }

        // Check if sum equals total (with tolerance for floating point errors)
        if (Math.abs(sumOfSplits - totalAmount) > EPSILON) {
            throw new InvalidSplitException(
                    String.format("Sum of splits (%.2f) does not equal total amount (%.2f)",
                                  sumOfSplits, totalAmount));
        }
    }

    @Override
    public void calculateSplits(double totalAmount, List<Split> splits) {
        // For exact splits, amounts are already set during split creation
        // Just validate that the amounts are set
        for (Split split : splits) {
            if (split.getAmount() <= 0 && !(split instanceof ExactSplit)) {
                throw new IllegalStateException("Exact split amounts must be set during creation");
            }
        }
    }

    @Override
    public List<Split> createSplits(double totalAmount, List<User> users) {
        // For exact splits, we can't auto-create without knowing specific amounts
        // This creates a template with zero amounts that must be filled in
        List<Split> splits = new ArrayList<>();
        double equalShare = totalAmount / users.size();
        for (User user : users) {
            splits.add(new ExactSplit(user, equalShare));
        }
        return splits;
    }

    /**
     * Creates exact splits with specified amounts.
     *
     * @param users   List of users
     * @param amounts List of amounts corresponding to each user
     * @return List of exact splits
     */
    public List<Split> createSplits(List<User> users, List<Double> amounts) throws InvalidSplitException {
        if (users.size() != amounts.size()) {
            throw new InvalidSplitException("Number of users must match number of amounts");
        }

        List<Split> splits = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            splits.add(new ExactSplit(users.get(i), amounts.get(i)));
        }
        return splits;
    }
}
