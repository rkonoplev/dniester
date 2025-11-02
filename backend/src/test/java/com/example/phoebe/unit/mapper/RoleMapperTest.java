package com.example.phoebe.mapper;

import com.example.phoebe.dto.response.RoleDto;
import com.example.phoebe.entity.Permission;
import com.example.phoebe.entity.Role;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleMapperTest {

    private final RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

    @Test
    void shouldMapRoleToRoleDto() {
        // Given
        Permission p = new Permission("EDIT_ARTICLE");
        Role role = new Role("ADMIN", "Administrator");
        role.setPermissions(Set.of(p));

        // When
        RoleDto dto = roleMapper.toDto(role);

        // Then
        assertNotNull(dto);
        assertEquals(role.getName(), dto.name());
        assertEquals(role.getDescription(), dto.description());
        assertNotNull(dto.permissionNames());
        assertEquals(1, dto.permissionNames().size());
        assertTrue(dto.permissionNames().contains("edit_article")); // Permission names are normalized to lowercase
    }
}
