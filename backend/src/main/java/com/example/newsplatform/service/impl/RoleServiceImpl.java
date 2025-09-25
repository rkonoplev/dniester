package com.example.newsplatform.service.impl;

import com.example.newsplatform.dto.request.RoleCreateRequestDto;
import com.example.newsplatform.dto.request.RoleUpdateRequestDto;
import com.example.newsplatform.dto.response.RoleDto;
import com.example.newsplatform.entity.Role;
import com.example.newsplatform.exception.ResourceNotFoundException;
import com.example.newsplatform.mapper.RoleMapper;
import com.example.newsplatform.repository.RoleRepository;
import com.example.newsplatform.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing user roles.
 * This service handles the business logic for creating, reading, updating,
 * and deleting roles, ensuring data integrity and consistency.
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    /**
     * Retrieves all roles from the database.
     * @return A list of {@link RoleDto} representing all roles.
     */
    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single role by its unique ID.
     * @param id The ID of the role to find.
     * @return The found {@link RoleDto}.
     * @throws ResourceNotFoundException if no role with the given ID exists.
     */
    @Override
    @Transactional(readOnly = true)
    public RoleDto getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        return roleMapper.toDto(role);
    }

    /**
     * Creates a new role based on the provided request data.
     * @param createRequest The DTO containing the data for the new role.
     * @return The created {@link RoleDto}.
     */
    @Override
    @Transactional
    public RoleDto createRole(RoleCreateRequestDto createRequest) {
        Role role = roleMapper.fromCreateRequest(createRequest);
        // In a real app, you would also handle setting permissions here
        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    /**
     * Updates an existing role. Only non-null fields from the request are updated.
     * @param id The ID of the role to update.
     * @param updateRequest The DTO containing the fields to update.
     * @return The updated {@link RoleDto}.
     * @throws ResourceNotFoundException if the role is not found.
     */
    @Override
    @Transactional
    public RoleDto updateRole(Long id, RoleUpdateRequestDto updateRequest) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        // Use the mapper to apply partial updates
        roleMapper.updateEntityFromDto(updateRequest, role);
        // Logic to update permissions would go here

        Role updatedRole = roleRepository.save(role);
        return roleMapper.toDto(updatedRole);
    }

    /**
     * Deletes a role by its ID.
     * @param id The ID of the role to delete.
     * @throws ResourceNotFoundException if the role does not exist.
     */
    @Override
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", "id", id);
        }
        roleRepository.deleteById(id);
    }
}