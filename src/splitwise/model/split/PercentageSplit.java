package splitwise.model.split;

import splitwise.model.User;

/**
 * Represents a percentage-based split where each participant pays a percentage of the total.
 * The sum of all percentages must equal 100%.
 */
public class PercentageSplit extends Split {
    private double percentage;

    public PercentageSplit(User user, double percentage) {
        super(user);
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public boolean validate() {
        // Percentage must be between 0 and 100
        return percentage >= 0 && percentage <= 100;
    }

    @Override
    public String toString() {
        return "PercentageSplit{user=" + user.getName() + ", percentage=" + percentage + "%, amount=" + amount + "}";
    }
}
