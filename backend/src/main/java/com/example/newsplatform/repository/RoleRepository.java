package com.example.newsplatform.repository;

import com.example.newsplatform.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Role} entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find a role by its name.
     *
     * @param name The name of the role.
     * @return An {@link Optional} containing the role if found, or empty otherwise.
     */
    Optional<Role> findByName(String name);
}
