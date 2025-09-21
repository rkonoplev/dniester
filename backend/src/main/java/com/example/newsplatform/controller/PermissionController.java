package com.example.newsplatform.controller;

import com.example.newsplatform.dto.response.PermissionDto;
import com.example.newsplatform.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing permissions.
 */
@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permissions", description = "API for retrieving permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * GET /api/permissions : Get all available permissions.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of permissions in body.
     */
    @GetMapping
    @Operation(summary = "Get all permissions", 
               description = "Retrieve a list of all available permissions in the system.")
    public ResponseEntity<List<PermissionDto>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }
}
