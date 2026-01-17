package splitwise.service;

import splitwise.model.Currency;
import splitwise.model.Transaction;
import splitwise.model.TransactionType;
import splitwise.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for transaction/payment management.
 * Interface Segregation: Only defines transaction-specific methods.
 */
public interface TransactionService {

    /**
     * Records a payment between two users.
     */
    Transaction recordPayment(User fromUser, User toUser, double amount);

    /**
     * Records a payment with specified currency.
     */
    Transaction recordPayment(User fromUser, User toUser, double amount, Currency currency);

    /**
     * Records a settlement (clearing all debt) between two users.
     */
    Transaction recordSettlement(User fromUser, User toUser, double amount);

    /**
     * Gets a transaction by ID.
     */
    Optional<Transaction> getTransactionById(String transactionId);

    /**
     * Gets all transactions for a user.
     */
    List<Transaction> getTransactionHistory(User user);

    /**
     * Gets all transactions between two users.
     */
    List<Transaction> getTransactionsBetween(User user1, User user2);

    /**
     * Gets all transactions for a group.
     */
    List<Transaction> getTransactionsForGroup(String groupId);

    /**
     * Gets transactions by type.
     */
    List<Transaction> getTransactionsByType(TransactionType type);

    /**
     * Gets all transactions.
     */
    List<Transaction> getAllTransactions();
}
