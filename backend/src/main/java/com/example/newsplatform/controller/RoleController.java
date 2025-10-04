package com.example.newsplatform.controller;

import com.example.newsplatform.dto.request.RoleCreateRequestDto;
import com.example.newsplatform.dto.request.RoleUpdateRequestDto;
import com.example.newsplatform.dto.response.RoleDto;
import com.example.newsplatform.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * REST controller for managing user roles.
 * Endpoints are secured and require ADMIN privileges.
 */
@RestController
@RequestMapping("/api/admin/roles")
@Tag(name = "Role Management API", description = "Endpoints for managing user roles")
@SecurityRequirement(name = "basicAuth")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Retrieves a list of all available roles.
     * Pagination is omitted as the number of roles is expected to be small.
     *
     * @return A list of all roles.
     */
    @GetMapping
    @Operation(summary = "Get all roles")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    /**
     * Retrieves a single role by its unique ID.
     *
     * @param id The ID of the role.
     * @return The found role DTO.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        RoleDto role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    /**
     * Creates a new role.
     *
     * @param createRequest The DTO containing the data for the new role.
     * @return The created role DTO with a 201 Created status.
     */
    @PostMapping
    @Operation(summary = "Create a new role")
    public ResponseEntity<RoleDto> createRole(@RequestBody @Valid RoleCreateRequestDto createRequest) {
        RoleDto savedRole = roleService.createRole(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);
    }

    /**
     * Updates an existing role.
     *
     * @param id            The ID of the role to update.
     * @param updateRequest The DTO containing the fields to update.
     * @return The updated role DTO.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing role")
    public ResponseEntity<RoleDto> updateRole(@PathVariable Long id,
                                              @RequestBody @Valid RoleUpdateRequestDto updateRequest) {
        RoleDto updatedRole = roleService.updateRole(id, updateRequest);
        return ResponseEntity.ok(updatedRole);
    }

    /**
     * Deletes a role by its ID.
     *
     * @param id The ID of the role to delete.
     * @return A 204 No Content response on success.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all roles assigned to a specific user.
     *
     * @param userId The ID of the user.
     * @return A set of roles assigned to the user.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get roles for a specific user")
    public ResponseEntity<Set<RoleDto>> getRolesByUserId(@PathVariable Long userId) {
        Set<RoleDto> roles = roleService.findRolesByUserId(userId);
        return ResponseEntity.ok(roles);
    }
}