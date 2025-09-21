package com.example.newsplatform.service;

import com.example.newsplatform.dto.request.RoleCreateRequestDto;
import com.example.newsplatform.dto.response.RoleDto;

import java.util.List;

/**
 * Service interface for managing roles.
 */
public interface RoleService {

    /**
     * Retrieves all roles.
     *
     * @return A list of {@link RoleDto}s.
     */
    List<RoleDto> getAllRoles();

    /**
     * Retrieves a role by its ID.
     *
     * @param id The ID of the role.
     * @return The {@link RoleDto}.
     */
    RoleDto getRoleById(Long id);

    /**
     * Creates a new role.
     *
     * @param roleDto The DTO containing the data for the new role.
     * @return The created {@link RoleDto}.
     */
    RoleDto createRole(RoleCreateRequestDto roleDto);

    /**
     * Updates an existing role.
     *
     * @param id The ID of the role to update.
     * @param roleDto The DTO containing the updated data.
     * @return The updated {@link RoleDto}.
     */
    RoleDto updateRole(Long id, RoleCreateRequestDto roleDto);

    /**
     * Deletes a role by its ID.
     *
     * @param id The ID of the role to delete.
     */
    void deleteRole(Long id);

    /**
     * Adds a permission to a role.
     *
     * @param roleId The ID of the role.
     * @param permissionId The ID of the permission to add.
     * @return The updated {@link RoleDto}.
     */
    RoleDto addPermissionToRole(Long roleId, Long permissionId);

    /**
     * Removes a permission from a role.
     *
     * @param roleId The ID of the role.
     * @param permissionId The ID of the permission to remove.
     * @return The updated {@link RoleDto}.
     */
    RoleDto removePermissionFromRole(Long roleId, Long permissionId);
}
