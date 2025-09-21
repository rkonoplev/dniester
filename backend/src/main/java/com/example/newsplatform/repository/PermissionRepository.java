package com.example.newsplatform.repository;

import com.example.newsplatform.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Permission entity operations.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Find permission by name.
     */
    Optional<Permission> findByName(String name);

    /**
     * Find permissions by resource type.
     */
    List<Permission> findByResource(String resource);

    /**
     * Find permissions by action type.
     */
    List<Permission> findByAction(String action);

    /**
     * Search permissions by name or description.
     */
    @Query("SELECT p FROM Permission p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Permission> searchPermissions(@Param("search") String search, Pageable pageable);

    /**
     * Find permissions by resource and action.
     */
    Optional<Permission> findByResourceAndAction(String resource, String action);

    /**
     * Check if permission exists by name.
     */
    boolean existsByName(String name);
}