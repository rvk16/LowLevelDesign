package splitwise.service;

import splitwise.model.Group;
import splitwise.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface for group management operations.
 * Interface Segregation: Only defines group-specific methods.
 */
public interface GroupService {

    /**
     * Creates a new group.
     */
    Group createGroup(String name, User creator);

    /**
     * Creates a new group with description.
     */
    Group createGroup(String name, String description, User creator);

    /**
     * Gets a group by ID.
     */
    Optional<Group> getGroupById(String groupId);

    /**
     * Gets all groups for a user.
     */
    List<Group> getGroupsForUser(User user);

    /**
     * Gets all groups.
     */
    List<Group> getAllGroups();

    /**
     * Adds a member to a group.
     */
    boolean addMember(String groupId, User user);

    /**
     * Removes a member from a group.
     */
    boolean removeMember(String groupId, User user);

    /**
     * Gets all members of a group.
     */
    Set<User> getMembers(String groupId);

    /**
     * Promotes a member to admin.
     */
    boolean addAdmin(String groupId, User user);

    /**
     * Demotes an admin to regular member.
     */
    boolean removeAdmin(String groupId, User user);

    /**
     * Deletes a group.
     */
    boolean deleteGroup(String groupId);

    /**
     * Checks if a group exists.
     */
    boolean groupExists(String groupId);
}
