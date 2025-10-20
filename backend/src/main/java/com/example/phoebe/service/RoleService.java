package com.example.phoebe.service;

import com.example.phoebe.dto.request.RoleCreateRequestDto;
import com.example.phoebe.dto.request.RoleUpdateRequestDto;
import com.example.phoebe.dto.response.RoleDto;

import java.util.List;
import java.util.Set;

/**
 * Service interface defining business operations related to Role entities.
 * This contract ensures that the service layer always works with DTOs for external communication.
 */
public interface RoleService {

    /**
     * Retrieves all roles in the system.
     *
     * @return A list of all roles as {@link RoleDto}.
     */
    List<RoleDto> getAllRoles();

    /**
     * Retrieves a single role by its unique ID.
     *
     * @param id The ID of the role.
     * @return The found role as a {@link RoleDto}.
     * @throws com.example.phoebe.exception.ResourceNotFoundException if the role is not found.
     */
    RoleDto getRoleById(Long id);

    /**
     * Creates a new role based on the provided data.
     *
     * @param createRequest DTO containing data for the new role.
     * @return The newly created role as a {@link RoleDto}.
     */
    RoleDto createRole(RoleCreateRequestDto createRequest);

    /**
     * Updates an existing role.
     *
     * @param id            The ID of the role to update.
     * @param updateRequest DTO containing the updated data.
     * @return The updated role as a {@link RoleDto}.
     */
    RoleDto updateRole(Long id, RoleUpdateRequestDto updateRequest);

    /**
     * Deletes a role by its ID.
     *
     * @param id The ID of the role to delete.
     */
    void deleteRole(Long id);

    /**
     * Assigns a permission to a role.
     *
     * @param roleId       The ID of the role.
     * @param permissionId The ID of the permission to assign.
     * @return The updated role as a {@link RoleDto}.
     */
    RoleDto assignPermission(Long roleId, Long permissionId);

    /**
     * Removes a permission from a role.
     *
     * @param roleId       The ID of the role.
     * @param permissionId The ID of the permission to remove.
     * @return The updated role as a {@link RoleDto}.
     */
    RoleDto removePermission(Long roleId, Long permissionId);

    /**
     * Finds all roles associated with a given user ID.
     *
     * @param userId The ID of the user.
     * @return A set of roles for the specified user as {@link RoleDto}.
     */
    Set<RoleDto> findRolesByUserId(Long userId);
}
