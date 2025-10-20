package com.example.phoebe.service.impl;

import com.example.phoebe.dto.response.PermissionDto;
import com.example.phoebe.mapper.PermissionMapper;
import com.example.phoebe.repository.PermissionRepository;
import com.example.phoebe.service.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing system permissions.
 * <p>
 * Provides methods to retrieve permissions, check existence by name, and map entities to DTOs.
 * All operations are read-only transactional.
 */
@Service
@Transactional(readOnly = true)
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    /**
     * Constructs the PermissionServiceImpl with the required repository and mapper.
     *
     * @param permissionRepository repository for Permission entities
     * @param permissionMapper     mapper for converting between Permission and PermissionDto
     */
    public PermissionServiceImpl(PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    /**
     * Retrieves all permissions in the system.
     *
     * @return list of {@link PermissionDto} representing all permissions
     */
    @Override
    public List<PermissionDto> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a permission by its unique identifier.
     *
     * @param id permission ID
     * @return {@link PermissionDto} if found, otherwise null
     */
    @Override
    public PermissionDto getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .map(permissionMapper::toDto)
                .orElse(null);
    }

    /**
     * Retrieves all permissions assigned to a specific role.
     *
     * @param roleId role ID
     * @return list of {@link PermissionDto} assigned to the role
     */
    @Override
    public List<PermissionDto> getPermissionsByRoleId(Long roleId) {
        // Corrected method name from findByRoles_Id to findByRolesId
        return permissionRepository.findByRolesId(roleId).stream()
                .map(permissionMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Checks if a permission with the given name exists in the system.
     *
     * @param name permission name
     * @return true if exists, false otherwise
     */
    @Override
    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }
}
