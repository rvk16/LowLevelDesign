package splitwise.model;

import java.util.Objects;

/**
 * Represents a balance/debt between two users.
 * This is a value object representing how much one user owes another.
 */
public class Balance {
    private final User fromUser;  // The debtor (who owes money)
    private final User toUser;    // The creditor (who is owed money)
    private double amount;
    private Currency currency;

    public Balance(User fromUser, User toUser, double amount) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.currency = Currency.USD;
    }

    public Balance(User fromUser, User toUser, double amount, Currency currency) {
        this(fromUser, toUser, amount);
        this.currency = currency;
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

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * Adds to the existing balance amount.
     */
    public void addAmount(double additionalAmount) {
        this.amount += additionalAmount;
    }

    /**
     * Checks if this balance is effectively zero (settled).
     */
    public boolean isSettled() {
        return Math.abs(amount) < 0.01;
    }

    /**
     * Returns a formatted string describing this balance.
     */
    public String getDescription() {
        return fromUser.getName() + " owes " + toUser.getName() + " " + currency.format(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Balance balance = (Balance) o;
        return Objects.equals(fromUser, balance.fromUser) &&
               Objects.equals(toUser, balance.toUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromUser, toUser);
    }

    @Override
    public String toString() {
        return "Balance{" + fromUser.getName() + " owes " + toUser.getName() +
               " " + currency.format(amount) + "}";
    }
}
