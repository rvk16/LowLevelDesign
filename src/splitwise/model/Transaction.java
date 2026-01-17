package splitwise.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a financial transaction (payment or settlement) between users.
 * Single Responsibility: Only stores transaction data.
 */
public class Transaction {
    private final String id;
    private final User fromUser;
    private final User toUser;
    private final double amount;
    private final Currency currency;
    private final TransactionType type;
    private final LocalDateTime timestamp;
    private String notes;
    private String groupId; // null for non-group transactions

    public Transaction(User fromUser, User toUser, double amount, Currency currency, TransactionType type) {
        this.id = UUID.randomUUID().toString();
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(User fromUser, User toUser, double amount, TransactionType type) {
        this(fromUser, toUser, amount, Currency.USD, type);
    }

    public String getId() {
        return id;
    }

    public User getFromUser() {
        return fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public double getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Checks if this transaction involves a specific user.
     */
    public boolean involvesUser(User user) {
        return fromUser.equals(user) || toUser.equals(user);
    }

    /**
     * Returns a formatted description of this transaction.
     */
    public String getDescription() {
        String action = type == TransactionType.PAYMENT ? "paid" : "settled with";
        return fromUser.getName() + " " + action + " " + toUser.getName() + " " + currency.format(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{id='" + id.substring(0, 8) + "...', " + getDescription() +
               ", type=" + type + ", timestamp=" + timestamp + "}";
    }
}
