package com.example.newsplatform.repository;

import com.example.newsplatform.entity.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Repository for managing {@link Role} entities.
 * Provides standard CRUD operations and custom query methods for finding roles.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by its unique name.
     * This is a standard lookup and does not eagerly fetch relationships.
     *
     * @param name The name of the role (e.g., "ADMIN").
     * @return An Optional containing the found role, or empty if not found.
     */
    Optional<Role> findByName(String name);

    /**
     * Finds a role by its unique name and eagerly fetches its associated permissions.
     * This method is optimized to prevent N+1 query problems when checking permissions.
     * Use this when you need to access the role's permissions immediately after fetching.
     *
     * @param name The name of the role (e.g., "ADMIN").
     * @return An Optional containing the found role with its permissions initialized, or empty if not found.
     */
    @EntityGraph(attributePaths = "permissions")
    Optional<Role> findByNameWithPermissions(String name);

    /**
     * Finds all roles associated with a given user ID and eagerly fetches their permissions.
     * This is essential for loading user authorities during authentication in an optimized way.
     *
     * @param userId The ID of the user.
     * @return A set of roles for the specified user, with permissions initialized.
     */
    @EntityGraph(attributePaths = "permissions")
    Set<Role> findByUsers_Id(Long userId);
}