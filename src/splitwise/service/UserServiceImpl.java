package splitwise.service;

import splitwise.exception.UserNotFoundException;
import splitwise.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of UserService.
 * Single Responsibility: Only handles user CRUD operations.
 * Dependency Inversion: Implements the UserService interface.
 */
public class UserServiceImpl implements UserService {
    private final Map<String, User> usersById;
    private final Map<String, User> usersByEmail;

    public UserServiceImpl() {
        this.usersById = new HashMap<>();
        this.usersByEmail = new HashMap<>();
    }

    @Override
    public User createUser(String name, String email, String phone) {
        if (usersByEmail.containsKey(email)) {
            throw new IllegalArgumentException("User with email already exists: " + email);
        }

        User user = new User(name, email, phone);
        usersById.put(user.getId(), user);
        usersByEmail.put(email, user);
        return user;
    }

    @Override
    public User createUser(String name, String email) {
        return createUser(name, email, null);
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return Optional.ofNullable(usersById.get(userId));
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public User updateUser(User user) {
        if (!usersById.containsKey(user.getId())) {
            throw new UserNotFoundException(user.getId());
        }

        User existingUser = usersById.get(user.getId());

        // If email changed, update email index
        if (!existingUser.getEmail().equals(user.getEmail())) {
            usersByEmail.remove(existingUser.getEmail());
            usersByEmail.put(user.getEmail(), user);
        }

        usersById.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean deleteUser(String userId) {
        User user = usersById.remove(userId);
        if (user != null) {
            usersByEmail.remove(user.getEmail());
            return true;
        }
        return false;
    }

    @Override
    public boolean userExists(String userId) {
        return usersById.containsKey(userId);
    }
}
