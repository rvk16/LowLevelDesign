package splitwise;

import splitwise.exception.InvalidSplitException;
import splitwise.factory.SplitFactory;
import splitwise.manager.SplitwiseManager;
import splitwise.model.*;
import splitwise.model.split.ExactSplit;
import splitwise.model.split.PercentageSplit;
import splitwise.model.split.Split;
import splitwise.util.BalanceSimplifier;

import java.util.Arrays;
import java.util.List;

/**
 * oop.abstraction.Main demonstration class for the Splitwise Low-Level Design.
 *
 * This demo showcases:
 * - Core OOP concepts (Inheritance, Abstraction, Encapsulation, Polymorphism)
 * - SOLID Principles throughout
 * - Design Patterns (Strategy, Factory, Singleton, Observer)
 * - Clean service layer architecture
 *
 * @author Splitwise Demo
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           SPLITWISE LOW-LEVEL DESIGN DEMONSTRATION           ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        // Get the singleton manager instance
        SplitwiseManager manager = SplitwiseManager.getInstance();

        try {
            // 1. User Creation
            demonstrateUserCreation(manager);

            // 2. Group Creation
            demonstrateGroupCreation(manager);

            // 3. Equal Split Expense
            demonstrateEqualSplit(manager);

            // 4. Exact Split Expense
            demonstrateExactSplit(manager);

            // 5. Percentage Split Expense
            demonstratePercentageSplit(manager);

            // 6. Multi-Currency Expense
            demonstrateMultiCurrency(manager);

            // 7. Group Expense
            demonstrateGroupExpense(manager);

            // 8. Balance Display
            demonstrateBalances(manager);

            // 9. Debt Simplification
            demonstrateDebtSimplification(manager);

            // 10. Payment Recording
            demonstratePayments(manager);

            // 11. Transaction History
            demonstrateTransactionHistory(manager);

            // 12. Activity Feed
            demonstrateActivityFeed(manager);

            System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║              DEMONSTRATION COMPLETED SUCCESSFULLY            ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");

        } catch (InvalidSplitException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void demonstrateUserCreation(SplitwiseManager manager) {
        printSection("1. USER CREATION");

        User alice = manager.createUser("Alice", "alice@email.com", "555-0101");
        User bob = manager.createUser("Bob", "bob@email.com", "555-0102");
        User charlie = manager.createUser("Charlie", "charlie@email.com", "555-0103");
        User diana = manager.createUser("Diana", "diana@email.com", "555-0104");

        System.out.println("Created users:");
        manager.getAllUsers().forEach(user ->
            System.out.println("  - " + user.getName() + " (" + user.getEmail() + ")")
        );
    }

    private static void demonstrateGroupCreation(SplitwiseManager manager) {
        printSection("2. GROUP CREATION");

        User alice = manager.getUserByEmail("alice@email.com").orElseThrow();
        User bob = manager.getUserByEmail("bob@email.com").orElseThrow();
        User charlie = manager.getUserByEmail("charlie@email.com").orElseThrow();

        Group tripGroup = manager.createGroup("Weekend Trip", "Trip to the mountains", alice);
        manager.addMemberToGroup(tripGroup.getId(), bob, alice);
        manager.addMemberToGroup(tripGroup.getId(), charlie, alice);

        System.out.println("Created group: " + tripGroup.getName());
        System.out.println("Members:");
        manager.getGroupMembers(tripGroup.getId()).forEach(member ->
            System.out.println("  - " + member.getName())
        );
    }

    private static void demonstrateEqualSplit(SplitwiseManager manager) throws InvalidSplitException {
        printSection("3. EQUAL SPLIT EXPENSE");

        User alice = manager.getUserByEmail("alice@email.com").orElseThrow();
        User bob = manager.getUserByEmail("bob@email.com").orElseThrow();
        User charlie = manager.getUserByEmail("charlie@email.com").orElseThrow();

        List<User> participants = Arrays.asList(alice, bob, charlie);

        Expense expense = manager.addExpense(
            "Dinner at Restaurant",
            90.00,
            alice,  // Alice paid
            ExpenseType.EQUAL,
            participants
        );

        System.out.println("Expense: " + expense.getDescription());
        System.out.println("Total: " + expense.getCurrency().format(expense.getAmount()));
        System.out.println("Paid by: " + expense.getPaidBy().getName());
        System.out.println("Split type: " + expense.getType());
        System.out.println("\nSplits:");
        expense.getSplits().forEach(split ->
            System.out.println("  - " + split.getUser().getName() + ": " +
                              expense.getCurrency().format(split.getAmount()))
        );
    }

    private static void demonstrateExactSplit(SplitwiseManager manager) throws InvalidSplitException {
        printSection("4. EXACT SPLIT EXPENSE");

        User alice = manager.getUserByEmail("alice@email.com").orElseThrow();
        User bob = manager.getUserByEmail("bob@email.com").orElseThrow();
        User charlie = manager.getUserByEmail("charlie@email.com").orElseThrow();

        // Create exact splits with specific amounts
        List<Split> splits = Arrays.asList(
            new ExactSplit(alice, 50.00),   // Alice's share
            new ExactSplit(bob, 30.00),     // Bob's share
            new ExactSplit(charlie, 20.00)  // Charlie's share
        );

        Expense expense = manager.addExpenseWithSplits(
            "Shopping",
            100.00,
            Currency.USD,
            bob,  // Bob paid
            ExpenseType.EXACT,
            splits
        );

        System.out.println("Expense: " + expense.getDescription());
        System.out.println("Total: " + expense.getCurrency().format(expense.getAmount()));
        System.out.println("Paid by: " + expense.getPaidBy().getName());
        System.out.println("Split type: " + expense.getType());
        System.out.println("\nExact Splits:");
        expense.getSplits().forEach(split ->
            System.out.println("  - " + split.getUser().getName() + ": " +
                              expense.getCurrency().format(split.getAmount()))
        );
    }

    private static void demonstratePercentageSplit(SplitwiseManager manager) throws InvalidSplitException {
        printSection("5. PERCENTAGE SPLIT EXPENSE");

        User alice = manager.getUserByEmail("alice@email.com").orElseThrow();
        User bob = manager.getUserByEmail("bob@email.com").orElseThrow();
        User charlie = manager.getUserByEmail("charlie@email.com").orElseThrow();

        // Create percentage splits
        List<Split> splits = Arrays.asList(
            new PercentageSplit(alice, 50.0),   // 50%
            new PercentageSplit(bob, 30.0),     // 30%
            new PercentageSplit(charlie, 20.0)  // 20%
        );

        Expense expense = manager.addExpenseWithSplits(
            "Hotel Booking",
            200.00,
            Currency.USD,
            charlie,  // Charlie paid
            ExpenseType.PERCENTAGE,
            splits
        );

        System.out.println("Expense: " + expense.getDescription());
        System.out.println("Total: " + expense.getCurrency().format(expense.getAmount()));
        System.out.println("Paid by: " + expense.getPaidBy().getName());
        System.out.println("Split type: " + expense.getType());
        System.out.println("\nPercentage Splits:");
        expense.getSplits().forEach(split -> {
            PercentageSplit pSplit = (PercentageSplit) split;
            System.out.println("  - " + split.getUser().getName() + ": " +
                              pSplit.getPercentage() + "% = " +
                              expense.getCurrency().format(split.getAmount()));
        });
    }

    private static void demonstrateMultiCurrency(SplitwiseManager manager) throws InvalidSplitException {
        printSection("6. MULTI-CURRENCY EXPENSE");

        User alice = manager.getUserByEmail("alice@email.com").orElseThrow();
        User diana = manager.getUserByEmail("diana@email.com").orElseThrow();

        List<User> participants = Arrays.asList(alice, diana);

        // Create expense in EUR
        Expense euroExpense = manager.addExpense(
            "Paris Dinner",
            50.00,
            Currency.EUR,
            alice,
            ExpenseType.EQUAL,
            participants
        );

        System.out.println("Expense: " + euroExpense.getDescription());
        System.out.println("Total: " + euroExpense.getCurrency().format(euroExpense.getAmount()));

        // Show currency conversion
        double amountInUSD = manager.convertCurrency(50.00, Currency.EUR, Currency.USD);
        double amountInINR = manager.convertCurrency(50.00, Currency.EUR, Currency.INR);

        System.out.println("\nCurrency Conversions:");
        System.out.println("  " + Currency.EUR.format(50.00) + " = " + Currency.USD.format(amountInUSD));
        System.out.println("  " + Currency.EUR.format(50.00) + " = " + Currency.INR.format(amountInINR));
    }

    private static void demonstrateGroupExpense(SplitwiseManager manager) throws InvalidSplitException {
        printSection("7. GROUP EXPENSE");

        User alice = manager.getUserByEmail("alice@email.com").orElseThrow();
        User bob = manager.getUserByEmail("bob@email.com").orElseThrow();
        User charlie = manager.getUserByEmail("charlie@email.com").orElseThrow();

        List<Group> aliceGroups = manager.getGroupsForUser(alice);
        if (!aliceGroups.isEmpty()) {
            Group tripGroup = aliceGroups.get(0);
            List<User> groupMembers = Arrays.asList(alice, bob, charlie);

            Expense groupExpense = manager.addGroupExpense(
                "Gas for Trip",
                60.00,
                Currency.USD,
                alice,
                ExpenseType.EQUAL,
                groupMembers,
                tripGroup.getId()
            );

            System.out.println("Group: " + tripGroup.getName());
            System.out.println("Expense: " + groupExpense.getDescription());
            System.out.println("Total: " + groupExpense.getCurrency().format(groupExpense.getAmount()));
            System.out.println("Each person's share: " +
                              groupExpense.getCurrency().format(groupExpense.getAmount() / 3));
        }
    }

    private static void demonstrateBalances(SplitwiseManager manager) {
        printSection("8. BALANCE DISPLAY");

        User alice = manager.getUserByEmail("alice@email.com").orElseThrow();
        User bob = manager.getUserByEmail("bob@email.com").orElseThrow();
        User charlie = manager.getUserByEmail("charlie@email.com").orElseThrow();

        System.out.println(manager.getBalanceSummary(alice));
        System.out.println(manager.getBalanceSummary(bob));
        System.out.println(manager.getBalanceSummary(charlie));
    }

    private static void demonstrateDebtSimplification(SplitwiseManager manager) {
        printSection("9. DEBT SIMPLIFICATION");

        List<User> users = manager.getAllUsers();

        System.out.println("Before simplification - all individual balances exist between users.");
        System.out.println("After simplification - minimum number of transactions needed:");

        manager.printSimplifiedBalances(users);
    }

    private static void demonstratePayments(SplitwiseManager manager) {
        printSection("10. PAYMENT RECORDING");

        User alice = manager.getUserByEmail("alice@email.com").orElseThrow();
        User bob = manager.getUserByEmail("bob@email.com").orElseThrow();

        double bobOwesAlice = manager.getBalanceBetween(alice, bob);

        if (bobOwesAlice > 0) {
            System.out.println("Before payment:");
            System.out.println("  Bob owes Alice: " + Currency.USD.format(bobOwesAlice));

            // Bob pays Alice $20
            Transaction payment = manager.recordPayment(bob, alice, 20.00);

            double newBalance = manager.getBalanceBetween(alice, bob);
            System.out.println("\nPayment recorded: " + payment.getDescription());
            System.out.println("\nAfter payment:");
            System.out.println("  Bob owes Alice: " + Currency.USD.format(newBalance));
        } else {
            System.out.println("No outstanding balance from Bob to Alice");
        }
    }

    private static void demonstrateTransactionHistory(SplitwiseManager manager) {
        printSection("11. TRANSACTION HISTORY");

        User alice = manager.getUserByEmail("alice@email.com").orElseThrow();
        User bob = manager.getUserByEmail("bob@email.com").orElseThrow();

        System.out.println("Transaction history for Alice:");
        List<Transaction> aliceTransactions = manager.getTransactionHistory(alice);
        if (aliceTransactions.isEmpty()) {
            System.out.println("  No transactions yet.");
        } else {
            aliceTransactions.forEach(t ->
                System.out.println("  - " + t.getDescription() + " at " + t.getTimestamp())
            );
        }

        System.out.println("\nTransactions between Alice and Bob:");
        List<Transaction> abTransactions = manager.getTransactionsBetween(alice, bob);
        if (abTransactions.isEmpty()) {
            System.out.println("  No transactions between them.");
        } else {
            abTransactions.forEach(t ->
                System.out.println("  - " + t.getDescription())
            );
        }
    }

    private static void demonstrateActivityFeed(SplitwiseManager manager) {
        printSection("12. ACTIVITY FEED");

        System.out.println("Recent Activities (last 10):");
        List<Activity> activities = manager.getRecentActivities(10);

        if (activities.isEmpty()) {
            System.out.println("  No activities yet.");
        } else {
            activities.forEach(activity ->
                System.out.println("  [" + activity.getType() + "] " +
                                  activity.getFormattedMessage())
            );
        }
    }

    private static void printSection(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println(title);
        System.out.println("=".repeat(60));
    }
}
