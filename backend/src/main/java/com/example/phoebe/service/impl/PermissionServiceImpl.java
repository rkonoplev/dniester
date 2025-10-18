package com.example.phoebe.service.impl;

import com.example.phoebe.dto.response.PermissionDto;
import com.example.phoebe.mapper.PermissionMapper;
import com.example.phoebe.repository.PermissionRepository;
import com.example.phoebe.service.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing permissions.
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    public PermissionServiceImpl(PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    /**
     * Retrieves all permissions from the database and maps them to DTOs.
     *
     * @return A list of all permissions.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PermissionDto> getAllPermissions() {
        // Correctly maps the list of entities to a list of DTOs
        return permissionMapper.toDto(permissionRepository.findAll());
    }
}