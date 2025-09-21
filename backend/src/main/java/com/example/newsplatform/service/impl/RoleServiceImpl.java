package com.example.newsplatform.service.impl;

import com.example.newsplatform.dto.request.RoleCreateRequestDto;
import com.example.newsplatform.dto.response.RoleDto;
import com.example.newsplatform.entity.Permission;
import com.example.newsplatform.entity.Role;
import com.example.newsplatform.exception.ResourceNotFoundException;
import com.example.newsplatform.mapper.RoleMapper;
import com.example.newsplatform.repository.PermissionRepository;
import com.example.newsplatform.repository.RoleRepository;
import com.example.newsplatform.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the {@link RoleService} interface.
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        return roleMapper.toDto(roleRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        return roleMapper.toDto(role);
    }

    @Override
    public RoleDto createRole(RoleCreateRequestDto roleDto) {
        // Optional: Check if role with the same name already exists
        // roleRepository.findByName(roleDto.getName()).ifPresent(r -> {
        //     throw new IllegalStateException("Role with name '" + r.getName() + "' already exists.");
        // });
        Role role = roleMapper.toEntity(roleDto);
        return roleMapper.toDto(roleRepository.save(role));
    }

    @Override
    public RoleDto updateRole(Long id, RoleCreateRequestDto roleDto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());
        return roleMapper.toDto(roleRepository.save(role));
    }

    @Override
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        // Consider implications: what happens to users with this role?
        // For now, we'll just delete the role.
        roleRepository.delete(role);
    }

    @Override
    public RoleDto addPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", permissionId));

        role.getPermissions().add(permission);
        return roleMapper.toDto(roleRepository.save(role));
    }

    @Override
    public RoleDto removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", permissionId));

        role.getPermissions().remove(permission);
        return roleMapper.toDto(roleRepository.save(role));
    }
}
