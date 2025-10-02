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
import java.util.Set;
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

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        return roleMapper.toDto(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<RoleDto> findRolesByUserId(Long userId) {
        return roleRepository.findByUsersId(userId).stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public RoleDto createRole(RoleCreateRequestDto createRequest) {
        Role role = roleMapper.fromCreateRequest(createRequest);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    @Override
    @Transactional
    public RoleDto updateRole(Long id, RoleUpdateRequestDto updateRequest) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        roleMapper.updateEntityFromDto(updateRequest, role);
        Role updatedRole = roleRepository.save(role);
        return roleMapper.toDto(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", "id", id);
        }
        roleRepository.deleteById(id);
    }
}