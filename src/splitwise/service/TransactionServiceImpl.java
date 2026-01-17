package splitwise.service;

import splitwise.model.Currency;
import splitwise.model.Transaction;
import splitwise.model.TransactionType;
import splitwise.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of TransactionService.
 * Single Responsibility: Only handles transaction recording and retrieval.
 */
public class TransactionServiceImpl implements TransactionService {
    private final Map<String, Transaction> transactionsById;
    private final BalanceService balanceService;

    public TransactionServiceImpl(BalanceService balanceService) {
        this.transactionsById = new HashMap<>();
        this.balanceService = balanceService;
    }

    @Override
    public Transaction recordPayment(User fromUser, User toUser, double amount) {
        return recordPayment(fromUser, toUser, amount, Currency.USD);
    }

    @Override
    public Transaction recordPayment(User fromUser, User toUser, double amount, Currency currency) {
        Transaction transaction = new Transaction(fromUser, toUser, amount, currency, TransactionType.PAYMENT);
        transactionsById.put(transaction.getId(), transaction);

        // Update balances - fromUser pays toUser
        balanceService.settleBalance(fromUser, toUser, amount);

        return transaction;
    }

    @Override
    public Transaction recordSettlement(User fromUser, User toUser, double amount) {
        Transaction transaction = new Transaction(fromUser, toUser, amount, Currency.USD, TransactionType.SETTLEMENT);
        transactionsById.put(transaction.getId(), transaction);

        // Update balances
        balanceService.settleBalance(fromUser, toUser, amount);

        return transaction;
    }

    @Override
    public Optional<Transaction> getTransactionById(String transactionId) {
        return Optional.ofNullable(transactionsById.get(transactionId));
    }

    @Override
    public List<Transaction> getTransactionHistory(User user) {
        return transactionsById.values().stream()
                .filter(t -> t.involvesUser(user))
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getTransactionsBetween(User user1, User user2) {
        return transactionsById.values().stream()
                .filter(t -> t.involvesUser(user1) && t.involvesUser(user2))
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getTransactionsForGroup(String groupId) {
        return transactionsById.values().stream()
                .filter(t -> groupId.equals(t.getGroupId()))
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getTransactionsByType(TransactionType type) {
        return transactionsById.values().stream()
                .filter(t -> t.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactionsById.values());
    }
}
