package splitwise.util;

import splitwise.model.Balance;
import splitwise.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Utility class for simplifying debts to minimize the number of transactions.
 * Uses a greedy algorithm to match creditors with debtors.
 */
public class BalanceSimplifier {

    /**
     * Simplifies debts among a group of users to minimize transactions.
     * Uses a greedy algorithm with priority queues.
     *
     * @param users List of users with their balances
     * @return List of simplified balances (minimum transactions needed)
     */
    public static List<Balance> simplify(List<User> users) {
        List<Balance> simplifiedBalances = new ArrayList<>();

        // Calculate net balance for each user
        Map<User, Double> netBalances = calculateNetBalances(users);

        // Separate into creditors (positive) and debtors (negative)
        PriorityQueue<UserAmount> creditors = new PriorityQueue<>(
                Comparator.comparingDouble((UserAmount ua) -> ua.amount).reversed());
        PriorityQueue<UserAmount> debtors = new PriorityQueue<>(
                Comparator.comparingDouble(ua -> ua.amount));

        for (Map.Entry<User, Double> entry : netBalances.entrySet()) {
            double balance = entry.getValue();
            if (balance > 0.01) {
                creditors.add(new UserAmount(entry.getKey(), balance));
            } else if (balance < -0.01) {
                debtors.add(new UserAmount(entry.getKey(), balance));
            }
        }

        // Match creditors and debtors greedily
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            UserAmount creditor = creditors.poll();
            UserAmount debtor = debtors.poll();

            double credit = creditor.amount;
            double debt = Math.abs(debtor.amount);

            double settleAmount = Math.min(credit, debt);

            // Create a balance: debtor owes creditor
            simplifiedBalances.add(new Balance(debtor.user, creditor.user,
                    Math.round(settleAmount * 100.0) / 100.0));

            // Update remaining amounts
            double remainingCredit = credit - settleAmount;
            double remainingDebt = debt - settleAmount;

            if (remainingCredit > 0.01) {
                creditors.add(new UserAmount(creditor.user, remainingCredit));
            }
            if (remainingDebt > 0.01) {
                debtors.add(new UserAmount(debtor.user, -remainingDebt));
            }
        }

        return simplifiedBalances;
    }

    /**
     * Calculates the net balance for each user.
     * Positive = net creditor (others owe them)
     * Negative = net debtor (they owe others)
     */
    private static Map<User, Double> calculateNetBalances(List<User> users) {
        Map<User, Double> netBalances = new HashMap<>();

        for (User user : users) {
            double netBalance = 0;
            Map<String, Double> userBalances = user.getBalances();

            for (Double balance : userBalances.values()) {
                netBalance += balance;
            }

            netBalances.put(user, netBalance);
        }

        return netBalances;
    }

    /**
     * Prints a detailed breakdown of how debts can be simplified.
     */
    public static void printSimplification(List<User> users) {
        System.out.println("\n=== Debt Simplification ===");

        List<Balance> simplified = simplify(users);

        if (simplified.isEmpty()) {
            System.out.println("All balances are settled!");
            return;
        }

        System.out.println("Minimum transactions needed: " + simplified.size());
        System.out.println("\nTransactions:");

        int count = 1;
        for (Balance balance : simplified) {
            System.out.printf("%d. %s pays %s: %s%n",
                    count++,
                    balance.getFromUser().getName(),
                    balance.getToUser().getName(),
                    balance.getCurrency().format(balance.getAmount()));
        }
    }

    /**
     * Helper class to associate a user with an amount for priority queue.
     */
    private static class UserAmount {
        final User user;
        final double amount;

        UserAmount(User user, double amount) {
            this.user = user;
            this.amount = amount;
        }
    }
}
