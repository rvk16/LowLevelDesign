package splitwise.model.split;

import splitwise.model.User;

/**
 * Represents an exact split where a specific amount is assigned to each participant.
 * The sum of all exact splits must equal the total expense amount.
 */
public class ExactSplit extends Split {

    public ExactSplit(User user, double amount) {
        super(user);
        this.amount = amount;
    }

    @Override
    public boolean validate() {
        // Exact amount must be non-negative
        return amount >= 0;
    }
}
