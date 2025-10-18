package com.example.phoebe.service;

import com.example.phoebe.dto.response.PermissionDto;

import java.util.List;

/**
 * Service interface for managing permissions.
 */
public interface PermissionService {

    /**
     * Retrieves a list of all available permissions.
     *
     * @return A list of {@link PermissionDto}s.
     */
    List<PermissionDto> getAllPermissions();
}
