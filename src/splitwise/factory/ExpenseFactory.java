package splitwise.factory;

import splitwise.exception.InvalidSplitException;
import splitwise.model.Currency;
import splitwise.model.Expense;
import splitwise.model.ExpenseType;
import splitwise.model.User;
import splitwise.model.split.Split;
import splitwise.strategy.SplitStrategy;

import java.util.List;

/**
 * Factory for creating Expense objects with validated splits.
 * Factory Pattern: Centralizes complex expense creation logic.
 */
public class ExpenseFactory {

    /**
     * Creates an expense with calculated and validated splits.
     *
     * @param description The expense description
     * @param amount      The total amount
     * @param paidBy      The user who paid
     * @param type        The expense type
     * @param participants The users participating in the expense
     * @return The created expense with splits
     */
    public static Expense createExpense(String description, double amount, User paidBy,
                                         ExpenseType type, List<User> participants)
            throws InvalidSplitException {
        return createExpense(description, amount, Currency.USD, paidBy, type, participants);
    }

    /**
     * Creates an expense with specified currency and calculated splits.
     *
     * @param description  The expense description
     * @param amount       The total amount
     * @param currency     The currency
     * @param paidBy       The user who paid
     * @param type         The expense type
     * @param participants The users participating in the expense
     * @return The created expense with splits
     */
    public static Expense createExpense(String description, double amount, Currency currency,
                                         User paidBy, ExpenseType type, List<User> participants)
            throws InvalidSplitException {
        if (amount <= 0) {
            throw new InvalidSplitException("Expense amount must be positive");
        }
        if (paidBy == null) {
            throw new InvalidSplitException("Payer cannot be null");
        }
        if (participants == null || participants.isEmpty()) {
            throw new InvalidSplitException("Expense must have at least one participant");
        }

        Expense expense = new Expense(description, amount, currency, paidBy, type);

        // Create and validate splits using strategy pattern
        SplitStrategy strategy = SplitFactory.getStrategy(type);
        List<Split> splits = strategy.createSplits(amount, participants);
        strategy.validate(amount, splits);

        expense.setSplits(splits);
        return expense;
    }

    /**
     * Creates an expense with pre-defined splits.
     *
     * @param description The expense description
     * @param amount      The total amount
     * @param currency    The currency
     * @param paidBy      The user who paid
     * @param type        The expense type
     * @param splits      The pre-defined splits
     * @return The created expense with validated splits
     */
    public static Expense createExpenseWithSplits(String description, double amount, Currency currency,
                                                   User paidBy, ExpenseType type, List<Split> splits)
            throws InvalidSplitException {
        if (amount <= 0) {
            throw new InvalidSplitException("Expense amount must be positive");
        }
        if (paidBy == null) {
            throw new InvalidSplitException("Payer cannot be null");
        }
        if (splits == null || splits.isEmpty()) {
            throw new InvalidSplitException("Expense must have at least one split");
        }

        // Validate splits using strategy pattern
        SplitStrategy strategy = SplitFactory.getStrategy(type);
        strategy.calculateSplits(amount, splits);
        strategy.validate(amount, splits);

        Expense expense = new Expense(description, amount, currency, paidBy, type);
        expense.setSplits(splits);
        return expense;
    }

    /**
     * Creates a group expense.
     *
     * @param description  The expense description
     * @param amount       The total amount
     * @param currency     The currency
     * @param paidBy       The user who paid
     * @param type         The expense type
     * @param participants The users participating
     * @param groupId      The group ID
     * @return The created group expense
     */
    public static Expense createGroupExpense(String description, double amount, Currency currency,
                                              User paidBy, ExpenseType type, List<User> participants,
                                              String groupId) throws InvalidSplitException {
        Expense expense = createExpense(description, amount, currency, paidBy, type, participants);
        expense.setGroupId(groupId);
        return expense;
    }
}
