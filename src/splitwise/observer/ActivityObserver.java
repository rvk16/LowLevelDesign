package splitwise.observer;

import splitwise.model.Activity;

/**
 * Observer interface for activity-related events.
 * Observer Pattern: Allows objects to receive activity notifications.
 * Interface Segregation: Only defines activity-specific methods.
 */
public interface ActivityObserver {

    /**
     * Called when a new activity is recorded.
     *
     * @param activity The new activity
     */
    void onActivityRecorded(Activity activity);
}
