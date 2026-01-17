package splitwise.observer;

import splitwise.model.Expense;

/**
 * Observer interface for expense-related events.
 * Observer Pattern: Allows objects to be notified of expense changes.
 * Interface Segregation: Only defines expense-specific methods.
 */
public interface ExpenseObserver {

    /**
     * Called when a new expense is added.
     *
     * @param expense The newly added expense
     */
    void onExpenseAdded(Expense expense);

    /**
     * Called when an expense is updated.
     *
     * @param expense The updated expense
     */
    void onExpenseUpdated(Expense expense);

    /**
     * Called when an expense is deleted.
     *
     * @param expense The deleted expense
     */
    void onExpenseDeleted(Expense expense);
}
