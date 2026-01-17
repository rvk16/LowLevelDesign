package splitwise.model.split;

import splitwise.model.User;

/**
 * Abstract base class for expense splits.
 * Demonstrates oop.abstraction.inheritance and oop.abstraction in OOP.
 * Liskov Substitution Principle: All subclasses can replace this base class.
 */
public abstract class Split {
    protected User user;
    protected double amount;

    protected Split(User user) {
        this.user = user;
        this.amount = 0;
    }

    public User getUser() {
        return user;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Validates if this split is valid.
     * Each subclass implements its own validation logic.
     */
    public abstract boolean validate();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{user=" + user.getName() + ", amount=" + amount + "}";
    }
}
