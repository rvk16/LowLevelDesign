package splitwise.strategy;

import splitwise.exception.InvalidSplitException;
import splitwise.model.User;
import splitwise.model.split.EqualSplit;
import splitwise.model.split.Split;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for splitting expenses equally among all participants.
 * Implements Open/Closed Principle: Can be swapped with other strategies.
 */
public class EqualSplitStrategy implements SplitStrategy {

    @Override
    public void validate(double totalAmount, List<Split> splits) throws InvalidSplitException {
        if (splits == null || splits.isEmpty()) {
            throw new InvalidSplitException("Splits list cannot be empty");
        }
        if (totalAmount <= 0) {
            throw new InvalidSplitException("Total amount must be positive");
        }
        for (Split split : splits) {
            if (!(split instanceof EqualSplit)) {
                throw new InvalidSplitException("All splits must be EqualSplit for equal splitting");
            }
        }
    }

    @Override
    public void calculateSplits(double totalAmount, List<Split> splits) {
        if (splits == null || splits.isEmpty()) {
            return;
        }

        int numberOfSplits = splits.size();
        double equalShare = Math.round((totalAmount / numberOfSplits) * 100.0) / 100.0;

        // Handle rounding - last person might pay slightly different amount
        double totalDistributed = 0;
        for (int i = 0; i < splits.size() - 1; i++) {
            splits.get(i).setAmount(equalShare);
            totalDistributed += equalShare;
        }

        // Last person gets the remainder to ensure exact total
        double lastShare = Math.round((totalAmount - totalDistributed) * 100.0) / 100.0;
        splits.get(splits.size() - 1).setAmount(lastShare);
    }

    @Override
    public List<Split> createSplits(double totalAmount, List<User> users) {
        List<Split> splits = new ArrayList<>();
        for (User user : users) {
            splits.add(new EqualSplit(user));
        }
        calculateSplits(totalAmount, splits);
        return splits;
    }
}
