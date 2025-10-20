package com.example.phoebe.service;

import com.example.phoebe.dto.response.PermissionDto;
import java.util.List;

/**
 * Service interface for managing system permissions.
 */
public interface PermissionService {

    List<PermissionDto> getAllPermissions();

    PermissionDto getPermissionById(Long id);

    List<PermissionDto> getPermissionsByRoleId(Long roleId);

    boolean existsByName(String name);
}
