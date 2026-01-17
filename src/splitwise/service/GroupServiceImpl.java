package splitwise.service;

import splitwise.exception.GroupNotFoundException;
import splitwise.model.Group;
import splitwise.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of GroupService.
 * Single Responsibility: Only handles group management operations.
 */
public class GroupServiceImpl implements GroupService {
    private final Map<String, Group> groupsById;

    public GroupServiceImpl() {
        this.groupsById = new HashMap<>();
    }

    @Override
    public Group createGroup(String name, User creator) {
        Group group = new Group(name, creator);
        groupsById.put(group.getId(), group);
        return group;
    }

    @Override
    public Group createGroup(String name, String description, User creator) {
        Group group = new Group(name, description, creator);
        groupsById.put(group.getId(), group);
        return group;
    }

    @Override
    public Optional<Group> getGroupById(String groupId) {
        return Optional.ofNullable(groupsById.get(groupId));
    }

    @Override
    public List<Group> getGroupsForUser(User user) {
        return groupsById.values().stream()
                .filter(group -> group.isMember(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Group> getAllGroups() {
        return new ArrayList<>(groupsById.values());
    }

    @Override
    public boolean addMember(String groupId, User user) {
        Group group = getGroupOrThrow(groupId);
        return group.addMember(user);
    }

    @Override
    public boolean removeMember(String groupId, User user) {
        Group group = getGroupOrThrow(groupId);
        return group.removeMember(user);
    }

    @Override
    public Set<User> getMembers(String groupId) {
        Group group = getGroupOrThrow(groupId);
        return group.getMembers();
    }

    @Override
    public boolean addAdmin(String groupId, User user) {
        Group group = getGroupOrThrow(groupId);
        return group.addAdmin(user);
    }

    @Override
    public boolean removeAdmin(String groupId, User user) {
        Group group = getGroupOrThrow(groupId);
        return group.removeAdmin(user);
    }

    @Override
    public boolean deleteGroup(String groupId) {
        return groupsById.remove(groupId) != null;
    }

    @Override
    public boolean groupExists(String groupId) {
        return groupsById.containsKey(groupId);
    }

    private Group getGroupOrThrow(String groupId) {
        return getGroupById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(groupId));
    }
}
