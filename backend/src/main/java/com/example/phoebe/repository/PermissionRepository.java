package com.example.phoebe.repository;

import com.example.phoebe.entity.Permission;
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
     * Find permissions by resource type (extracted from name pattern "resource:action").
     */
    @Query("SELECT p FROM Permission p WHERE p.name LIKE CONCAT(:resource, ':%')")
    List<Permission> findByResource(@Param("resource") String resource);

    /**
     * Find permissions by action type (extracted from name pattern "resource:action").
     */
    @Query("SELECT p FROM Permission p WHERE p.name LIKE CONCAT('%:', :action)")
    List<Permission> findByAction(@Param("action") String action);

    /**
     * Search permissions by name.
     */
    @Query("SELECT p FROM Permission p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Permission> searchPermissions(@Param("search") String search, Pageable pageable);

    /**
     * Find permissions by resource and action (exact match for "resource:action" pattern).
     */
    @Query("SELECT p FROM Permission p WHERE p.name = CONCAT(:resource, ':', :action)")
    Optional<Permission> findByResourceAndAction(@Param("resource") String resource, @Param("action") String action);

    /**
     * Check if permission exists by name.
     */
    boolean existsByName(String name);
}