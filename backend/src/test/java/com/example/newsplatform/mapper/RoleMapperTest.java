package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.request.RoleCreateRequestDto;
import com.example.newsplatform.dto.response.RoleDto;
import com.example.newsplatform.entity.Permission;
import com.example.newsplatform.entity.Role;
import com.example.newsplatform.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RoleMapperTest {

    // Use Spy to allow both mocked and real method calls if needed
    @Spy
    private PermissionMapper permissionMapper = Mappers.getMapper(PermissionMapper.class);

    @InjectMocks
    private RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

    @Test
    void toDto_shouldMapRoleToRoleDto() {
        // Given
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        role.setDescription("Administrator role");
        role.setActive(true);

        User user = new User();
        role.setUsers(new HashSet<>(Collections.singletonList(user)));

        Permission perm = new Permission();
        perm.setId(100L);
        perm.setName("EDIT_ARTICLE");
        role.setPermissions(new HashSet<>(Collections.singletonList(perm)));

        // When
        RoleDto dto = roleMapper.toDto(role);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("ADMIN", dto.name());
        assertEquals("Administrator role", dto.description());
        assertTrue(dto.active());
        assertEquals(1, dto.userCount());
        assertEquals(1, dto.permissions().size());
        assertNotNull(dto.permissions().iterator().next());
        assertEquals("EDIT_ARTICLE", dto.permissions().iterator().next().name());
    }

    @Test
    void toDto_shouldReturnNull_whenRoleIsNull() {
        assertNull(roleMapper.toDto((Role) null));
    }

    @Test
    void toEntity_shouldMapRoleCreateRequestDtoToRole() {
        // Given
        RoleCreateRequestDto createDto = new RoleCreateRequestDto();
        createDto.setName("EDITOR");
        createDto.setDescription("Editor role");

        // When
        Role role = roleMapper.toEntity(createDto);

        // Then
        assertNotNull(role);
        assertNull(role.getId()); // ID should be null before saving
        assertEquals("EDITOR", role.getName());
        assertEquals("Editor role", role.getDescription());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        assertNull(roleMapper.toEntity(null));
    }
}
