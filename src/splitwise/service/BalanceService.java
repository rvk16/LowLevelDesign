package splitwise.service;

import splitwise.model.Balance;
import splitwise.model.Expense;
import splitwise.model.User;

import java.util.List;
import java.util.Map;

/**
 * Service interface for balance management operations.
 * Interface Segregation: Only defines balance-specific methods.
 */
public interface BalanceService {

    /**
     * Updates balances based on a new expense.
     */
    void updateBalancesForExpense(Expense expense);

    /**
     * Reverses balance updates for a deleted expense.
     */
    void reverseBalancesForExpense(Expense expense);

    /**
     * Gets all balances for a user.
     */
    Map<String, Double> getBalancesForUser(User user);

    /**
     * Gets the balance between two users.
     */
    double getBalanceBetween(User user1, User user2);

    /**
     * Gets all non-zero balances in the system.
     */
    List<Balance> getAllBalances();

    /**
     * Gets all balances for a specific group.
     */
    List<Balance> getBalancesForGroup(String groupId);

    /**
     * Settles the debt between two users.
     */
    void settleBalance(User fromUser, User toUser, double amount);

    /**
     * Gets simplified balances (minimum transactions to settle all debts).
     */
    List<Balance> getSimplifiedBalances(List<User> users);

    /**
     * Prints a summary of balances for a user.
     */
    String getBalanceSummary(User user);
}
