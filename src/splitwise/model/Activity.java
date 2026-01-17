package splitwise.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an activity in the system's activity feed.
 * Single Responsibility: Only stores activity/event data.
 */
public class Activity {
    private final String id;
    private final ActivityType type;
    private final User actor;
    private final String description;
    private final LocalDateTime timestamp;
    private final Map<String, Object> metadata;
    private String groupId; // null for non-group activities

    public Activity(ActivityType type, User actor, String description) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.actor = actor;
        this.description = description;
        this.timestamp = LocalDateTime.now();
        this.metadata = new HashMap<>();
    }

    public Activity(ActivityType type, User actor, String description, String groupId) {
        this(type, actor, description);
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public ActivityType getType() {
        return type;
    }

    public User getActor() {
        return actor;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }

    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Checks if this activity involves a specific user.
     */
    public boolean involvesUser(User user) {
        if (actor.equals(user)) {
            return true;
        }
        // Check metadata for involved users
        Object involvedUsers = metadata.get("involvedUsers");
        if (involvedUsers instanceof Iterable) {
            for (Object u : (Iterable<?>) involvedUsers) {
                if (user.equals(u)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a formatted activity message.
     */
    public String getFormattedMessage() {
        return actor.getName() + " " + type.getDescription() + ": " + description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return Objects.equals(id, activity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Activity{id='" + id.substring(0, 8) + "...', type=" + type +
               ", actor=" + actor.getName() + ", timestamp=" + timestamp + "}";
    }
}
