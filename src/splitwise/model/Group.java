package splitwise.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a group of users who share expenses.
 * Single Responsibility: Only manages group membership and metadata.
 */
public class Group {
    private final String id;
    private String name;
    private String description;
    private final User createdBy;
    private final LocalDateTime createdAt;
    private final Set<User> members;
    private final Set<User> admins;
    private final List<String> expenseIds;
    private Currency defaultCurrency;

    public Group(String name, User createdBy) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = "";
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.members = new HashSet<>();
        this.admins = new HashSet<>();
        this.expenseIds = new ArrayList<>();
        this.defaultCurrency = Currency.USD;

        // Creator is automatically a member and admin
        this.members.add(createdBy);
        this.admins.add(createdBy);
    }

    public Group(String name, String description, User createdBy) {
        this(name, createdBy);
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<User> getMembers() {
        return new HashSet<>(members);
    }

    public Set<User> getAdmins() {
        return new HashSet<>(admins);
    }

    public List<String> getExpenseIds() {
        return new ArrayList<>(expenseIds);
    }

    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(Currency defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public boolean addMember(User user) {
        return members.add(user);
    }

    public boolean removeMember(User user) {
        // Cannot remove the creator
        if (user.equals(createdBy)) {
            return false;
        }
        admins.remove(user);
        return members.remove(user);
    }

    public boolean addAdmin(User user) {
        if (!members.contains(user)) {
            return false;
        }
        return admins.add(user);
    }

    public boolean removeAdmin(User user) {
        // Cannot remove creator as admin
        if (user.equals(createdBy)) {
            return false;
        }
        return admins.remove(user);
    }

    public boolean isMember(User user) {
        return members.contains(user);
    }

    public boolean isAdmin(User user) {
        return admins.contains(user);
    }

    public void addExpenseId(String expenseId) {
        expenseIds.add(expenseId);
    }

    public boolean removeExpenseId(String expenseId) {
        return expenseIds.remove(expenseId);
    }

    public int getMemberCount() {
        return members.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Group{id='" + id.substring(0, 8) + "...', name='" + name + "', members=" + members.size() + "}";
    }
}
