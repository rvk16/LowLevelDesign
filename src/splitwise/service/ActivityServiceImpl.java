package splitwise.service;

import splitwise.model.Activity;
import splitwise.model.ActivityType;
import splitwise.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of ActivityService.
 * Single Responsibility: Only handles activity recording and retrieval.
 * Observer Pattern: Implements ActivityObserver to receive activity notifications.
 */
public class ActivityServiceImpl implements ActivityService {
    private final Map<String, Activity> activitiesById;

    public ActivityServiceImpl() {
        this.activitiesById = new HashMap<>();
    }

    @Override
    public Activity recordActivity(ActivityType type, User actor, String description) {
        Activity activity = new Activity(type, actor, description);
        activitiesById.put(activity.getId(), activity);
        return activity;
    }

    @Override
    public Activity recordActivity(ActivityType type, User actor, String description, String groupId) {
        Activity activity = new Activity(type, actor, description, groupId);
        activitiesById.put(activity.getId(), activity);
        return activity;
    }

    @Override
    public List<Activity> getActivitiesForUser(User user) {
        return activitiesById.values().stream()
                .filter(a -> a.involvesUser(user))
                .sorted(Comparator.comparing(Activity::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Activity> getActivitiesForGroup(String groupId) {
        return activitiesById.values().stream()
                .filter(a -> groupId.equals(a.getGroupId()))
                .sorted(Comparator.comparing(Activity::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Activity> getRecentActivities(int limit) {
        return activitiesById.values().stream()
                .sorted(Comparator.comparing(Activity::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Activity> getActivitiesByType(ActivityType type) {
        return activitiesById.values().stream()
                .filter(a -> a.getType() == type)
                .sorted(Comparator.comparing(Activity::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Activity> getAllActivities() {
        return new ArrayList<>(activitiesById.values());
    }

    @Override
    public void onActivityRecorded(Activity activity) {
        // Store the activity when received through observer pattern
        activitiesById.put(activity.getId(), activity);
    }
}
