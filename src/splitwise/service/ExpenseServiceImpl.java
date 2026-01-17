package splitwise.service;

import splitwise.exception.InvalidSplitException;
import splitwise.factory.ExpenseFactory;
import splitwise.model.Currency;
import splitwise.model.Expense;
import splitwise.model.ExpenseType;
import splitwise.model.User;
import splitwise.model.split.Split;
import splitwise.observer.ExpenseObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ExpenseService.
 * Single Responsibility: Only handles expense CRUD operations.
 * Observer Pattern: Notifies observers when expenses change.
 */
public class ExpenseServiceImpl implements ExpenseService {
    private final Map<String, Expense> expensesById;
    private final List<ExpenseObserver> observers;
    private final BalanceService balanceService;

    public ExpenseServiceImpl(BalanceService balanceService) {
        this.expensesById = new HashMap<>();
        this.observers = new ArrayList<>();
        this.balanceService = balanceService;
    }

    @Override
    public Expense createExpense(String description, double amount, User paidBy,
                                  ExpenseType type, List<User> participants) throws InvalidSplitException {
        return createExpense(description, amount, Currency.USD, paidBy, type, participants);
    }

    @Override
    public Expense createExpense(String description, double amount, Currency currency,
                                  User paidBy, ExpenseType type, List<User> participants)
            throws InvalidSplitException {
        Expense expense = ExpenseFactory.createExpense(description, amount, currency, paidBy, type, participants);
        expensesById.put(expense.getId(), expense);

        // Update balances
        balanceService.updateBalancesForExpense(expense);

        // Notify observers
        notifyExpenseAdded(expense);

        return expense;
    }

    @Override
    public Expense createExpenseWithSplits(String description, double amount, Currency currency,
                                            User paidBy, ExpenseType type, List<Split> splits)
            throws InvalidSplitException {
        Expense expense = ExpenseFactory.createExpenseWithSplits(description, amount, currency, paidBy, type, splits);
        expensesById.put(expense.getId(), expense);

        // Update balances
        balanceService.updateBalancesForExpense(expense);

        // Notify observers
        notifyExpenseAdded(expense);

        return expense;
    }

    @Override
    public Expense createGroupExpense(String description, double amount, Currency currency,
                                       User paidBy, ExpenseType type, List<User> participants,
                                       String groupId) throws InvalidSplitException {
        Expense expense = ExpenseFactory.createGroupExpense(description, amount, currency,
                                                            paidBy, type, participants, groupId);
        expensesById.put(expense.getId(), expense);

        // Update balances
        balanceService.updateBalancesForExpense(expense);

        // Notify observers
        notifyExpenseAdded(expense);

        return expense;
    }

    @Override
    public Optional<Expense> getExpenseById(String expenseId) {
        return Optional.ofNullable(expensesById.get(expenseId));
    }

    @Override
    public List<Expense> getExpensesForUser(User user) {
        return expensesById.values().stream()
                .filter(expense -> expense.involvesUser(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Expense> getExpensesForGroup(String groupId) {
        return expensesById.values().stream()
                .filter(expense -> groupId.equals(expense.getGroupId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expensesById.values());
    }

    @Override
    public boolean deleteExpense(String expenseId) {
        Expense expense = expensesById.remove(expenseId);
        if (expense != null) {
            // Reverse the balance updates
            balanceService.reverseBalancesForExpense(expense);

            // Notify observers
            notifyExpenseDeleted(expense);
            return true;
        }
        return false;
    }

    @Override
    public void addObserver(ExpenseObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(ExpenseObserver observer) {
        observers.remove(observer);
    }

    private void notifyExpenseAdded(Expense expense) {
        for (ExpenseObserver observer : observers) {
            observer.onExpenseAdded(expense);
        }
    }

    private void notifyExpenseUpdated(Expense expense) {
        for (ExpenseObserver observer : observers) {
            observer.onExpenseUpdated(expense);
        }
    }

    private void notifyExpenseDeleted(Expense expense) {
        for (ExpenseObserver observer : observers) {
            observer.onExpenseDeleted(expense);
        }
    }
}
