package splitwise.strategy;

import splitwise.exception.InvalidSplitException;
import splitwise.model.User;
import splitwise.model.split.PercentageSplit;
import splitwise.model.split.Split;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for splitting expenses based on percentages.
 * Validates that all percentages sum to 100%.
 */
public class PercentageSplitStrategy implements SplitStrategy {
    private static final double EPSILON = 0.01; // Tolerance for floating point comparison
    private static final double TOTAL_PERCENTAGE = 100.0;

    @Override
    public void validate(double totalAmount, List<Split> splits) throws InvalidSplitException {
        if (splits == null || splits.isEmpty()) {
            throw new InvalidSplitException("Splits list cannot be empty");
        }
        if (totalAmount <= 0) {
            throw new InvalidSplitException("Total amount must be positive");
        }

        double sumOfPercentages = 0;
        for (Split split : splits) {
            if (!(split instanceof PercentageSplit)) {
                throw new InvalidSplitException("All splits must be PercentageSplit for percentage splitting");
            }
            PercentageSplit percentageSplit = (PercentageSplit) split;
            if (!percentageSplit.validate()) {
                throw new InvalidSplitException(
                        "Invalid percentage for user: " + split.getUser().getName() +
                        ". Percentage must be between 0 and 100");
            }
            sumOfPercentages += percentageSplit.getPercentage();
        }

        // Check if percentages sum to 100%
        if (Math.abs(sumOfPercentages - TOTAL_PERCENTAGE) > EPSILON) {
            throw new InvalidSplitException(
                    String.format("Sum of percentages (%.2f%%) does not equal 100%%", sumOfPercentages));
        }
    }

    @Override
    public void calculateSplits(double totalAmount, List<Split> splits) {
        if (splits == null || splits.isEmpty()) {
            return;
        }

        double totalDistributed = 0;
        for (int i = 0; i < splits.size() - 1; i++) {
            PercentageSplit split = (PercentageSplit) splits.get(i);
            double amount = Math.round((totalAmount * split.getPercentage() / 100.0) * 100.0) / 100.0;
            split.setAmount(amount);
            totalDistributed += amount;
        }

        // Last person gets remainder to ensure exact total
        double lastAmount = Math.round((totalAmount - totalDistributed) * 100.0) / 100.0;
        splits.get(splits.size() - 1).setAmount(lastAmount);
    }

    @Override
    public List<Split> createSplits(double totalAmount, List<User> users) {
        // Create equal percentage splits by default
        List<Split> splits = new ArrayList<>();
        double equalPercentage = TOTAL_PERCENTAGE / users.size();
        for (User user : users) {
            splits.add(new PercentageSplit(user, equalPercentage));
        }
        calculateSplits(totalAmount, splits);
        return splits;
    }

    /**
     * Creates percentage splits with specified percentages.
     *
     * @param totalAmount Total expense amount
     * @param users       List of users
     * @param percentages List of percentages corresponding to each user
     * @return List of percentage splits with calculated amounts
     */
    public List<Split> createSplits(double totalAmount, List<User> users, List<Double> percentages)
            throws InvalidSplitException {
        if (users.size() != percentages.size()) {
            throw new InvalidSplitException("Number of users must match number of percentages");
        }

        List<Split> splits = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            splits.add(new PercentageSplit(users.get(i), percentages.get(i)));
        }
        calculateSplits(totalAmount, splits);
        return splits;
    }
}
