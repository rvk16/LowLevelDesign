package splitwise.service;

import splitwise.model.Balance;
import splitwise.model.Currency;
import splitwise.model.Expense;
import splitwise.model.User;
import splitwise.model.split.Split;
import splitwise.util.BalanceSimplifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of BalanceService.
 * Single Responsibility: Only handles balance calculations and updates.
 */
public class BalanceServiceImpl implements BalanceService {

    public BalanceServiceImpl() {
    }

    @Override
    public void updateBalancesForExpense(Expense expense) {
        User payer = expense.getPaidBy();
        List<Split> splits = expense.getSplits();

        for (Split split : splits) {
            User participant = split.getUser();
            double amount = split.getAmount();

            if (!participant.equals(payer)) {
                // Participant owes the payer
                // From payer's perspective: participant owes me (positive balance)
                payer.updateBalance(participant.getId(), amount);
                // From participant's perspective: I owe payer (negative balance)
                participant.updateBalance(payer.getId(), -amount);
            }
        }
    }

    @Override
    public void reverseBalancesForExpense(Expense expense) {
        User payer = expense.getPaidBy();
        List<Split> splits = expense.getSplits();

        for (Split split : splits) {
            User participant = split.getUser();
            double amount = split.getAmount();

            if (!participant.equals(payer)) {
                // Reverse the balance updates
                payer.updateBalance(participant.getId(), -amount);
                participant.updateBalance(payer.getId(), amount);
            }
        }
    }

    @Override
    public Map<String, Double> getBalancesForUser(User user) {
        return user.getBalances();
    }

    @Override
    public double getBalanceBetween(User user1, User user2) {
        return user1.getBalanceWith(user2.getId());
    }

    @Override
    public List<Balance> getAllBalances() {
        // This would require access to all users, which should be injected
        // For now, return empty list - full implementation needs UserService
        return new ArrayList<>();
    }

    @Override
    public List<Balance> getBalancesForGroup(String groupId) {
        // Would need GroupService and all users - simplified implementation
        return new ArrayList<>();
    }

    @Override
    public void settleBalance(User fromUser, User toUser, double amount) {
        // fromUser pays toUser
        // This reduces what fromUser owes to toUser
        fromUser.updateBalance(toUser.getId(), amount);
        toUser.updateBalance(fromUser.getId(), -amount);
    }

    @Override
    public List<Balance> getSimplifiedBalances(List<User> users) {
        return BalanceSimplifier.simplify(users);
    }

    @Override
    public String getBalanceSummary(User user) {
        StringBuilder summary = new StringBuilder();
        summary.append("Balance Summary for ").append(user.getName()).append(":\n");

        Map<String, Double> balances = user.getBalances();
        if (balances.isEmpty()) {
            summary.append("  All settled up!\n");
            return summary.toString();
        }

        double totalOwed = 0;
        double totalOwedToMe = 0;

        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            double amount = entry.getValue();
            if (amount > 0) {
                summary.append(String.format("  + You are owed $%.2f (from user %s...)\n",
                                             amount, entry.getKey().substring(0, 8)));
                totalOwedToMe += amount;
            } else if (amount < 0) {
                summary.append(String.format("  - You owe $%.2f (to user %s...)\n",
                                             Math.abs(amount), entry.getKey().substring(0, 8)));
                totalOwed += Math.abs(amount);
            }
        }

        summary.append(String.format("\nTotal you owe: $%.2f\n", totalOwed));
        summary.append(String.format("Total owed to you: $%.2f\n", totalOwedToMe));
        summary.append(String.format("Net balance: $%.2f\n", totalOwedToMe - totalOwed));

        return summary.toString();
    }
}
