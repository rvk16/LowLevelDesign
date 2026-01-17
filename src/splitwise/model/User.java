package splitwise.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a user in the Splitwise system.
 * Single Responsibility: Only manages user data and personal balances.
 * Encapsulation: All fields are private with controlled access.
 */
public class User {
    private final String id;
    private String name;
    private String email;
    private String phone;
    private Currency preferredCurrency;
    // Maps userId to balance amount (positive = they owe me, negative = I owe them)
    private final Map<String, Double> balances;

    public User(String name, String email, String phone) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.preferredCurrency = Currency.USD;
        this.balances = new HashMap<>();
    }

    public User(String name, String email) {
        this(name, email, null);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Currency getPreferredCurrency() {
        return preferredCurrency;
    }

    public void setPreferredCurrency(Currency preferredCurrency) {
        this.preferredCurrency = preferredCurrency;
    }

    public Map<String, Double> getBalances() {
        return new HashMap<>(balances);
    }

    /**
     * Updates the balance with another user.
     * Positive amount means the other user owes this user.
     * Negative amount means this user owes the other user.
     */
    public void updateBalance(String otherUserId, double amount) {
        double currentBalance = balances.getOrDefault(otherUserId, 0.0);
        double newBalance = currentBalance + amount;

        // Remove entry if balance is zero (or very close to zero)
        if (Math.abs(newBalance) < 0.01) {
            balances.remove(otherUserId);
        } else {
            balances.put(otherUserId, newBalance);
        }
    }

    /**
     * Gets the balance with a specific user.
     * Positive = they owe me, Negative = I owe them
     */
    public double getBalanceWith(String otherUserId) {
        return balances.getOrDefault(otherUserId, 0.0);
    }

    /**
     * Gets the total amount this user owes to others.
     */
    public double getTotalOwed() {
        return balances.values().stream()
                .filter(amount -> amount < 0)
                .mapToDouble(Math::abs)
                .sum();
    }

    /**
     * Gets the total amount others owe this user.
     */
    public double getTotalOwedToMe() {
        return balances.values().stream()
                .filter(amount -> amount > 0)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    /**
     * Gets the net balance (positive = net creditor, negative = net debtor)
     */
    public double getNetBalance() {
        return balances.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{id='" + id.substring(0, 8) + "...', name='" + name + "', email='" + email + "'}";
    }
}
