package splitwise.manager;

import splitwise.exception.InvalidSplitException;
import splitwise.model.*;
import splitwise.model.split.Split;
import splitwise.observer.ExpenseObserver;
import splitwise.observer.NotificationService;
import splitwise.service.*;
import splitwise.util.BalanceSimplifier;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Singleton manager class that orchestrates all Splitwise operations.
 * Acts as a facade and DI container for all services.
 * Singleton Pattern: Single point of access for the application.
 * Dependency Inversion: Manages service instances and their dependencies.
 */
public class SplitwiseManager {
    private static volatile SplitwiseManager instance;

    private final UserService userService;
    private final GroupService groupService;
    private final ExpenseService expenseService;
    private final BalanceService balanceService;
    private final CurrencyService currencyService;
    private final TransactionService transactionService;
    private final ActivityService activityService;
    private final NotificationService notificationService;

    private SplitwiseManager() {
        // Initialize services with proper dependency injection
        this.balanceService = new BalanceServiceImpl();
        this.userService = new UserServiceImpl();
        this.groupService = new GroupServiceImpl();
        this.currencyService = new CurrencyServiceImpl();
        this.transactionService = new TransactionServiceImpl(balanceService);
        this.activityService = new ActivityServiceImpl();
        this.notificationService = new NotificationService();

        // ExpenseService depends on BalanceService
        this.expenseService = new ExpenseServiceImpl(balanceService);

        // Register notification service as observer
        expenseService.addObserver(notificationService);
    }

    /**
     * Gets the singleton instance of SplitwiseManager.
     * Thread-safe double-checked locking implementation.
     */
    public static SplitwiseManager getInstance() {
        if (instance == null) {
            synchronized (SplitwiseManager.class) {
                if (instance == null) {
                    instance = new SplitwiseManager();
                }
            }
        }
        return instance;
    }

    /**
     * Resets the singleton instance (useful for testing).
     */
    public static void resetInstance() {
        synchronized (SplitwiseManager.class) {
            instance = null;
        }
    }

    // ==================== User Operations ====================

    public User createUser(String name, String email) {
        return userService.createUser(name, email);
    }

    public User createUser(String name, String email, String phone) {
        return userService.createUser(name, email, phone);
    }

    public Optional<User> getUserById(String userId) {
        return userService.getUserById(userId);
    }

    public Optional<User> getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // ==================== Group Operations ====================

    public Group createGroup(String name, User creator) {
        Group group = groupService.createGroup(name, creator);
        activityService.recordActivity(ActivityType.GROUP_CREATED, creator,
                "Created group '" + name + "'", group.getId());
        return group;
    }

    public Group createGroup(String name, String description, User creator) {
        Group group = groupService.createGroup(name, description, creator);
        activityService.recordActivity(ActivityType.GROUP_CREATED, creator,
                "Created group '" + name + "'", group.getId());
        return group;
    }

    public Optional<Group> getGroupById(String groupId) {
        return groupService.getGroupById(groupId);
    }

    public boolean addMemberToGroup(String groupId, User member, User addedBy) {
        boolean result = groupService.addMember(groupId, member);
        if (result) {
            activityService.recordActivity(ActivityType.MEMBER_ADDED, addedBy,
                    "Added " + member.getName() + " to the group", groupId);
        }
        return result;
    }

    public boolean removeMemberFromGroup(String groupId, User member, User removedBy) {
        boolean result = groupService.removeMember(groupId, member);
        if (result) {
            activityService.recordActivity(ActivityType.MEMBER_REMOVED, removedBy,
                    "Removed " + member.getName() + " from the group", groupId);
        }
        return result;
    }

    public Set<User> getGroupMembers(String groupId) {
        return groupService.getMembers(groupId);
    }

    public List<Group> getGroupsForUser(User user) {
        return groupService.getGroupsForUser(user);
    }

    // ==================== Expense Operations ====================

    public Expense addExpense(String description, double amount, User paidBy,
                               ExpenseType type, List<User> participants) throws InvalidSplitException {
        Expense expense = expenseService.createExpense(description, amount, paidBy, type, participants);
        activityService.recordActivity(ActivityType.EXPENSE_ADDED, paidBy,
                description + " - " + Currency.USD.format(amount));
        return expense;
    }

    public Expense addExpense(String description, double amount, Currency currency,
                               User paidBy, ExpenseType type, List<User> participants)
            throws InvalidSplitException {
        Expense expense = expenseService.createExpense(description, amount, currency, paidBy, type, participants);
        activityService.recordActivity(ActivityType.EXPENSE_ADDED, paidBy,
                description + " - " + currency.format(amount));
        return expense;
    }

    public Expense addExpenseWithSplits(String description, double amount, Currency currency,
                                         User paidBy, ExpenseType type, List<Split> splits)
            throws InvalidSplitException {
        Expense expense = expenseService.createExpenseWithSplits(description, amount, currency, paidBy, type, splits);
        activityService.recordActivity(ActivityType.EXPENSE_ADDED, paidBy,
                description + " - " + currency.format(amount));
        return expense;
    }

    public Expense addGroupExpense(String description, double amount, Currency currency,
                                    User paidBy, ExpenseType type, List<User> participants,
                                    String groupId) throws InvalidSplitException {
        Expense expense = expenseService.createGroupExpense(description, amount, currency,
                                                            paidBy, type, participants, groupId);
        activityService.recordActivity(ActivityType.EXPENSE_ADDED, paidBy,
                description + " - " + currency.format(amount), groupId);
        return expense;
    }

    public boolean deleteExpense(String expenseId, User deletedBy) {
        Optional<Expense> expense = expenseService.getExpenseById(expenseId);
        boolean result = expenseService.deleteExpense(expenseId);
        if (result && expense.isPresent()) {
            activityService.recordActivity(ActivityType.EXPENSE_DELETED, deletedBy,
                    "Deleted expense: " + expense.get().getDescription());
        }
        return result;
    }

    public List<Expense> getExpensesForUser(User user) {
        return expenseService.getExpensesForUser(user);
    }

    public List<Expense> getExpensesForGroup(String groupId) {
        return expenseService.getExpensesForGroup(groupId);
    }

    // ==================== Balance Operations ====================

    public String getBalanceSummary(User user) {
        return balanceService.getBalanceSummary(user);
    }

    public double getBalanceBetween(User user1, User user2) {
        return balanceService.getBalanceBetween(user1, user2);
    }

    public List<Balance> getSimplifiedBalances(List<User> users) {
        return balanceService.getSimplifiedBalances(users);
    }

    public void printSimplifiedBalances(List<User> users) {
        BalanceSimplifier.printSimplification(users);
    }

    // ==================== Transaction Operations ====================

    public Transaction recordPayment(User fromUser, User toUser, double amount) {
        Transaction transaction = transactionService.recordPayment(fromUser, toUser, amount);
        activityService.recordActivity(ActivityType.PAYMENT_MADE, fromUser,
                "Paid " + toUser.getName() + " " + Currency.USD.format(amount));
        return transaction;
    }

    public Transaction recordPayment(User fromUser, User toUser, double amount, Currency currency) {
        Transaction transaction = transactionService.recordPayment(fromUser, toUser, amount, currency);
        activityService.recordActivity(ActivityType.PAYMENT_MADE, fromUser,
                "Paid " + toUser.getName() + " " + currency.format(amount));
        return transaction;
    }

    public Transaction settleUp(User fromUser, User toUser) {
        double balance = balanceService.getBalanceBetween(toUser, fromUser);
        if (balance > 0) {
            Transaction transaction = transactionService.recordSettlement(fromUser, toUser, balance);
            activityService.recordActivity(ActivityType.SETTLED, fromUser,
                    "Settled up with " + toUser.getName() + " - " + Currency.USD.format(balance));
            return transaction;
        }
        return null;
    }

    public List<Transaction> getTransactionHistory(User user) {
        return transactionService.getTransactionHistory(user);
    }

    public List<Transaction> getTransactionsBetween(User user1, User user2) {
        return transactionService.getTransactionsBetween(user1, user2);
    }

    // ==================== Currency Operations ====================

    public double convertCurrency(double amount, Currency from, Currency to) {
        return currencyService.convert(amount, from, to);
    }

    public void updateExchangeRate(Currency from, Currency to, double rate) {
        currencyService.updateExchangeRate(from, to, rate);
    }

    // ==================== Activity Operations ====================

    public List<Activity> getRecentActivities(int limit) {
        return activityService.getRecentActivities(limit);
    }

    public List<Activity> getActivitiesForUser(User user) {
        return activityService.getActivitiesForUser(user);
    }

    public List<Activity> getActivitiesForGroup(String groupId) {
        return activityService.getActivitiesForGroup(groupId);
    }

    // ==================== Observer Management ====================

    public void addExpenseObserver(ExpenseObserver observer) {
        expenseService.addObserver(observer);
    }

    public void removeExpenseObserver(ExpenseObserver observer) {
        expenseService.removeObserver(observer);
    }

    // ==================== Service Access (for advanced usage) ====================

    public UserService getUserService() {
        return userService;
    }

    public GroupService getGroupService() {
        return groupService;
    }

    public ExpenseService getExpenseService() {
        return expenseService;
    }

    public BalanceService getBalanceService() {
        return balanceService;
    }

    public CurrencyService getCurrencyService() {
        return currencyService;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public ActivityService getActivityService() {
        return activityService;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }
}
