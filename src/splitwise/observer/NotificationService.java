package splitwise.observer;

import splitwise.model.Activity;
import splitwise.model.Expense;
import splitwise.model.User;
import splitwise.model.split.Split;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that implements both ExpenseObserver and ActivityObserver
 * to send notifications to users about relevant events.
 * Observer Pattern: Receives and processes notifications.
 */
public class NotificationService implements ExpenseObserver, ActivityObserver {
    private final List<String> notificationLog;

    public NotificationService() {
        this.notificationLog = new ArrayList<>();
    }

    @Override
    public void onExpenseAdded(Expense expense) {
        String message = String.format("[NOTIFICATION] New expense '%s' for %s added by %s",
                expense.getDescription(),
                expense.getCurrency().format(expense.getAmount()),
                expense.getPaidBy().getName());

        notificationLog.add(message);
        notifyUsers(expense, message);
    }

    @Override
    public void onExpenseUpdated(Expense expense) {
        String message = String.format("[NOTIFICATION] Expense '%s' was updated",
                expense.getDescription());

        notificationLog.add(message);
        notifyUsers(expense, message);
    }

    @Override
    public void onExpenseDeleted(Expense expense) {
        String message = String.format("[NOTIFICATION] Expense '%s' was deleted by %s",
                expense.getDescription(),
                expense.getPaidBy().getName());

        notificationLog.add(message);
        notifyUsers(expense, message);
    }

    @Override
    public void onActivityRecorded(Activity activity) {
        String message = String.format("[ACTIVITY] %s", activity.getFormattedMessage());
        notificationLog.add(message);
        System.out.println(message);
    }

    /**
     * Notifies all users involved in an expense.
     * In a real application, this would send push notifications, emails, etc.
     */
    private void notifyUsers(Expense expense, String message) {
        System.out.println(message);

        // Notify the payer
        notifyUser(expense.getPaidBy(), message);

        // Notify all participants
        for (Split split : expense.getSplits()) {
            if (!split.getUser().equals(expense.getPaidBy())) {
                notifyUser(split.getUser(), "You owe " + expense.getPaidBy().getName() + " " +
                        expense.getCurrency().format(split.getAmount()) + " for " + expense.getDescription());
            }
        }
    }

    /**
     * Sends a notification to a specific user.
     * In a real application, this would integrate with notification providers.
     */
    private void notifyUser(User user, String message) {
        // Simulate notification - in real app would send push/email
        String notification = String.format("  -> Notifying %s: %s", user.getName(), message);
        notificationLog.add(notification);
    }

    /**
     * Gets the notification log for testing/debugging purposes.
     */
    public List<String> getNotificationLog() {
        return new ArrayList<>(notificationLog);
    }

    /**
     * Clears the notification log.
     */
    public void clearLog() {
        notificationLog.clear();
    }
}
