package splitwise.service;

import splitwise.exception.InvalidSplitException;
import splitwise.model.Currency;
import splitwise.model.Expense;
import splitwise.model.ExpenseType;
import splitwise.model.User;
import splitwise.model.split.Split;
import splitwise.observer.ExpenseObserver;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for expense management operations.
 * Interface Segregation: Only defines expense-specific methods.
 */
public interface ExpenseService {

    /**
     * Creates a new expense with auto-calculated splits.
     */
    Expense createExpense(String description, double amount, User paidBy,
                          ExpenseType type, List<User> participants) throws InvalidSplitException;

    /**
     * Creates a new expense with specified currency.
     */
    Expense createExpense(String description, double amount, Currency currency,
                          User paidBy, ExpenseType type, List<User> participants) throws InvalidSplitException;

    /**
     * Creates an expense with pre-defined splits.
     */
    Expense createExpenseWithSplits(String description, double amount, Currency currency,
                                     User paidBy, ExpenseType type, List<Split> splits) throws InvalidSplitException;

    /**
     * Creates a group expense.
     */
    Expense createGroupExpense(String description, double amount, Currency currency,
                                User paidBy, ExpenseType type, List<User> participants,
                                String groupId) throws InvalidSplitException;

    /**
     * Gets an expense by ID.
     */
    Optional<Expense> getExpenseById(String expenseId);

    /**
     * Gets all expenses for a user.
     */
    List<Expense> getExpensesForUser(User user);

    /**
     * Gets all expenses for a group.
     */
    List<Expense> getExpensesForGroup(String groupId);

    /**
     * Gets all expenses.
     */
    List<Expense> getAllExpenses();

    /**
     * Deletes an expense.
     */
    boolean deleteExpense(String expenseId);

    /**
     * Registers an observer for expense events.
     */
    void addObserver(ExpenseObserver observer);

    /**
     * Removes an expense observer.
     */
    void removeObserver(ExpenseObserver observer);
}
