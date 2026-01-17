package splitwise.service;

import splitwise.model.Activity;
import splitwise.model.ActivityType;
import splitwise.model.User;
import splitwise.observer.ActivityObserver;

import java.util.List;

/**
 * Service interface for activity feed management.
 * Interface Segregation: Only defines activity-specific methods.
 */
public interface ActivityService extends ActivityObserver {

    /**
     * Records a new activity.
     */
    Activity recordActivity(ActivityType type, User actor, String description);

    /**
     * Records a group activity.
     */
    Activity recordActivity(ActivityType type, User actor, String description, String groupId);

    /**
     * Gets all activities for a user.
     */
    List<Activity> getActivitiesForUser(User user);

    /**
     * Gets all activities for a group.
     */
    List<Activity> getActivitiesForGroup(String groupId);

    /**
     * Gets recent activities with a limit.
     */
    List<Activity> getRecentActivities(int limit);

    /**
     * Gets activities by type.
     */
    List<Activity> getActivitiesByType(ActivityType type);

    /**
     * Gets all activities.
     */
    List<Activity> getAllActivities();
}
