package splitwise.model;

import splitwise.model.split.Split;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an expense in the system.
 * Single Responsibility: Only stores expense data.
 */
public class Expense {
    private final String id;
    private String description;
    private double amount;
    private Currency currency;
    private User paidBy;
    private List<Split> splits;
    private ExpenseType type;
    private String groupId; // null for non-group expenses
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;

    public Expense(String description, double amount, User paidBy, ExpenseType type) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.amount = amount;
        this.currency = Currency.USD;
        this.paidBy = paidBy;
        this.type = type;
        this.splits = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Expense(String description, double amount, Currency currency, User paidBy, ExpenseType type) {
        this(description, amount, paidBy, type);
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
        this.updatedAt = LocalDateTime.now();
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
        this.updatedAt = LocalDateTime.now();
    }

    public User getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(User paidBy) {
        this.paidBy = paidBy;
        this.updatedAt = LocalDateTime.now();
    }

    public List<Split> getSplits() {
        return new ArrayList<>(splits);
    }

    public void setSplits(List<Split> splits) {
        this.splits = new ArrayList<>(splits);
        this.updatedAt = LocalDateTime.now();
    }

    public void addSplit(Split split) {
        this.splits.add(split);
        this.updatedAt = LocalDateTime.now();
    }

    public ExpenseType getType() {
        return type;
    }

    public void setType(ExpenseType type) {
        this.type = type;
        this.updatedAt = LocalDateTime.now();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if a user is involved in this expense (either as payer or participant).
     */
    public boolean involvesUser(User user) {
        if (paidBy.equals(user)) {
            return true;
        }
        return splits.stream().anyMatch(split -> split.getUser().equals(user));
    }

    /**
     * Gets the share amount for a specific user in this expense.
     */
    public double getShareForUser(User user) {
        return splits.stream()
                .filter(split -> split.getUser().equals(user))
                .mapToDouble(Split::getAmount)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Expense{id='" + id.substring(0, 8) + "...', description='" + description +
               "', amount=" + currency.format(amount) + ", paidBy=" + paidBy.getName() +
               ", type=" + type + "}";
    }
}
