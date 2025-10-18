package com.example.phoebe.mapper;

import com.example.phoebe.dto.request.UserCreateRequestDto;
import com.example.phoebe.dto.request.UserUpdateRequestDto;
import com.example.phoebe.dto.response.UserDto;
import com.example.phoebe.entity.Role;
import com.example.phoebe.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapUserToUserDto() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setActive(true);

        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        user.setRoles(Set.of(adminRole));

        // When
        UserDto dto = userMapper.toDto(user);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("testuser", dto.username());
        assertEquals("test@example.com", dto.email());
        assertTrue(dto.active());
        assertTrue(dto.roleNames().contains("ADMIN"));
    }

    @Test
    void shouldMapUserWithNoRolesToDtoWithDefaultRole() {
        // Given
        User user = new User();
        user.setUsername("editor_user");
        user.setRoles(Set.of()); // No roles

        // When
        UserDto dto = userMapper.toDto(user);

        // Then
        assertNotNull(dto);
        assertEquals(1, dto.roleNames().size());
        assertTrue(dto.roleNames().contains("EDITOR"));
    }

    @Test
    void shouldReturnNullDtoForNullUser() {
        // When
        UserDto dto = userMapper.toDto(null);
        // Then
        assertNull(dto);
    }

    @Test
    void shouldMapCreateRequestToUser() {
        // Given
        UserCreateRequestDto request = new UserCreateRequestDto();
        request.setUsername("newuser");
        request.setEmail("new@example.com");

        // When
        User user = userMapper.fromCreateRequest(request);

        // Then
        assertNotNull(user);
        assertEquals("newuser", user.getUsername());
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    void shouldReturnNullUserForNullCreateRequest() {
        // When
        User user = userMapper.fromCreateRequest(null);
        // Then
        assertNull(user);
    }

    @Test
    void shouldUpdateUserFromUpdateRequest() {
        // Given
        User user = new User();
        user.setUsername("original");
        user.setEmail("original@example.com");
        user.setActive(false);

        UserUpdateRequestDto request = new UserUpdateRequestDto();
        request.setEmail("updated@example.com");
        request.setActive(true);

        // When
        userMapper.updateEntity(user, request);

        // Then
        assertEquals("original", user.getUsername()); // Username should not change
        assertEquals("updated@example.com", user.getEmail());
        assertTrue(user.isActive());
    }

    @Test
    void shouldIgnoreNullsInUpdateRequest() {
        // Given
        User user = new User();
        user.setUsername("original");
        user.setEmail("original@example.com");
        user.setActive(false);

        UserUpdateRequestDto request = new UserUpdateRequestDto();
        request.setEmail(null); // Email is null
        request.setActive(true); // Active is not null

        // When
        userMapper.updateEntity(user, request);

        // Then
        assertEquals("original@example.com", user.getEmail()); // Should not be updated to null
        assertTrue(user.isActive()); // Should be updated
    }
}