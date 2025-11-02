package com.example.phoebe.service.impl;

import com.example.phoebe.dto.request.RoleCreateRequestDto;
import com.example.phoebe.dto.request.RoleUpdateRequestDto;
import com.example.phoebe.dto.response.RoleDto;
import com.example.phoebe.entity.Permission;
import com.example.phoebe.entity.Role;
import com.example.phoebe.exception.ResourceNotFoundException;
import com.example.phoebe.mapper.RoleMapper;
import com.example.phoebe.repository.PermissionRepository;
import com.example.phoebe.repository.RoleRepository;
import com.example.phoebe.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
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
        return roleRepository.findById(id)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
    }

    @Override
    public RoleDto createRole(RoleCreateRequestDto dto) {
        // Create Role using the constructor to set the immutable business key 'name'.
        Role role = new Role(dto.getName(), dto.getDescription());
        role.setPermissions(resolvePermissions(dto.getPermissionIds()));
        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    @Override
    public RoleDto updateRole(Long id, RoleUpdateRequestDto dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        // The role name (business key) is immutable and cannot be changed.
        // We only update the mutable fields.
        role.setDescription(dto.getDescription());
        role.setPermissions(resolvePermissions(dto.getPermissionIds()));

        // No explicit save() needed due to @Transactional and dirty checking.
        return roleMapper.toDto(role);
    }

    @Override
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", "id", id);
        }
        roleRepository.deleteById(id);
    }

    @Override
    public RoleDto assignPermission(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", permissionId));
        role.addPermission(permission);
        return roleMapper.toDto(role);
    }

    @Override
    public RoleDto removePermission(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "id", permissionId));
        role.removePermission(permission);
        return roleMapper.toDto(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<RoleDto> findRolesByUserId(Long userId) {
        // Corrected method name from findByUsers_Id to findByUsersId
        return roleRepository.findByUsersId(userId).stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toSet());
    }

    private Set<Permission> resolvePermissions(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        List<Permission> permissions = permissionRepository.findAllById(ids);
        // Note: If some permission IDs are not found, they will be silently ignored.
        // A more robust implementation might throw an exception here.
        return new HashSet<>(permissions);
    }
}
