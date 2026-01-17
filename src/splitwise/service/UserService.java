package splitwise.service;

import splitwise.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for user management operations.
 * Interface Segregation: Only defines user-specific methods.
 * Dependency Inversion: High-level modules depend on this oop.abstraction.
 */
public interface UserService {

    /**
     * Creates a new user.
     */
    User createUser(String name, String email, String phone);

    /**
     * Creates a new user without phone number.
     */
    User createUser(String name, String email);

    /**
     * Gets a user by ID.
     */
    Optional<User> getUserById(String userId);

    /**
     * Gets a user by email.
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Gets all users.
     */
    List<User> getAllUsers();

    /**
     * Updates a user's information.
     */
    User updateUser(User user);

    /**
     * Deletes a user by ID.
     */
    boolean deleteUser(String userId);

    /**
     * Checks if a user exists.
     */
    boolean userExists(String userId);
}
