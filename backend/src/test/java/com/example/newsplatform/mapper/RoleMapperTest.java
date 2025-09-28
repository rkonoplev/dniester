package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.request.RoleCreateRequestDto;
import com.example.newsplatform.dto.request.RoleUpdateRequestDto;
import com.example.newsplatform.dto.response.RoleDto;
import com.example.newsplatform.entity.Permission;
import com.example.newsplatform.entity.Role;
import com.example.newsplatform.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoleMapperTest {

    private final RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

    @Test
    void shouldMapRoleToRoleDto() {
        // Given
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        role.setDescription("Administrator");

        Permission p = new Permission();
        p.setName("EDIT_ARTICLE");
        role.setPermissions(Set.of(p)); // Initialize the set

        User u = new User();
        role.setUsers(Set.of(u));

        // When
        RoleDto dto = roleMapper.toDto(role);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("ADMIN", dto.name());
        assertEquals("Administrator", dto.description());
        assertEquals(1, dto.permissionNames().size());
        assertEquals("EDIT_ARTICLE", dto.permissionNames().iterator().next());
    }

    @Test
    void shouldMapCreateRequestToRole() {
        // Given
        RoleCreateRequestDto request = new RoleCreateRequestDto();
        request.setName("EDITOR");
        request.setDescription("Content Editor");

        // When
        Role role = roleMapper.fromCreateRequest(request);

        // Then
        assertNotNull(role);
        assertEquals("EDITOR", role.getName());
        assertEquals("Content Editor", role.getDescription());
    }

    @Test
    void shouldUpdateRoleFromUpdateRequest() {
        // Given
        Role role = new Role();
        role.setName("OriginalName");
        role.setDescription("OriginalDescription");

        RoleUpdateRequestDto request = new RoleUpdateRequestDto();
        request.setName("UpdatedName");
        // Description is left null to test partial update

        // When
        roleMapper.updateEntityFromDto(request, role);

        // Then
        assertEquals("UpdatedName", role.getName());
        assertEquals("OriginalDescription", role.getDescription()); // Description should not change
    }
}