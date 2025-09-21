package com.example.newsplatform.controller;

import com.example.newsplatform.dto.request.RoleCreateRequestDto;
import com.example.newsplatform.dto.response.RoleDto;
import com.example.newsplatform.security.RequireRole;
import com.example.newsplatform.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing roles.
 */
@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "API for managing user roles and permissions")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @Operation(summary = "Get all roles")
    @RequireRole("ADMIN")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a role by ID")
    @RequireRole("ADMIN")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new role")
    @RequireRole("ADMIN")
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleCreateRequestDto roleDto) {
        RoleDto createdRole = roleService.createRole(roleDto);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing role")
    @RequireRole("ADMIN")
    public ResponseEntity<RoleDto> updateRole(@PathVariable Long id, @Valid @RequestBody RoleCreateRequestDto roleDto) {
        return ResponseEntity.ok(roleService.updateRole(id, roleDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a role")
    @RequireRole("ADMIN")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    @Operation(summary = "Add a permission to a role")
    @RequireRole("ADMIN")
    public ResponseEntity<RoleDto> addPermissionToRole(@PathVariable Long roleId, @PathVariable Long permissionId) {
        return ResponseEntity.ok(roleService.addPermissionToRole(roleId, permissionId));
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @Operation(summary = "Remove a permission from a role")
    @RequireRole("ADMIN")
    public ResponseEntity<RoleDto> removePermissionFromRole(@PathVariable Long roleId, 
                                                            @PathVariable Long permissionId) {
        return ResponseEntity.ok(roleService.removePermissionFromRole(roleId, permissionId));
    }
}
