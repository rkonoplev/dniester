package com.example.phoebe.service;

import com.example.phoebe.dto.request.RoleCreateRequestDto;
import com.example.phoebe.dto.request.RoleUpdateRequestDto;
import com.example.phoebe.dto.response.RoleDto;

import java.util.List;
import java.util.Set;

/**
 * Service interface for managing user roles.
 * Defines the contract for all business logic related to roles,
 * including creation, retrieval, updates, and deletion.
 */
public interface RoleService {

    /**
     * Retrieves a list of all roles in the system.
     * @return A list of {@link RoleDto}.
     */
    List<RoleDto> getAllRoles();

    /**
     * Retrieves a single role by its ID.
     * @param id The ID of the role.
     * @return The found {@link RoleDto}.
     */
    RoleDto getRoleById(Long id);

    /**
     * Finds all roles assigned to a specific user.
     * @param userId The ID of the user.
     * @return A set of {@link RoleDto} assigned to the user.
     */
    Set<RoleDto> findRolesByUserId(Long userId);

    /**
     * Creates a new role.
     * @param createRequest The DTO with data for the new role.
     * @return The created {@link RoleDto}.
     */
    RoleDto createRole(RoleCreateRequestDto createRequest);

    /**
     * Updates an existing role.
     * @param id The ID of the role to update.
     * @param updateRequest The DTO with the updated data.
     * @return The updated {@link RoleDto}.
     */
    RoleDto updateRole(Long id, RoleUpdateRequestDto updateRequest);

    /**
     * Deletes a role by its ID.
     * @param id The ID of the role to delete.
     */
    void deleteRole(Long id);
}