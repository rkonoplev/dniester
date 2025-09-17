package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.request.UserCreateRequestDto;
import com.example.newsplatform.dto.request.UserUpdateRequestDto;
import com.example.newsplatform.dto.response.UserDto;
import com.example.newsplatform.entity.Role;
import com.example.newsplatform.entity.User;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserMapper utility class.
 * Tests mapping between User entities and DTOs for API responses.
 * Includes edge cases like null handling and default role assignment.
 */
class UserMapperTest {

    /**
     * Test mapping User entity to DTO when user has assigned roles.
     * Should properly map all fields including role names.
     */
    @Test
    void toDto_WithRoles_ShouldMapCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setActive(true);

        Role role = new Role();
        role.setName("ADMIN");
        user.setRoles(Set.of(role));

        UserDto dto = UserMapper.toDto(user);

        assertEquals(1L, dto.id());
        assertEquals("testuser", dto.username());
        assertEquals("test@example.com", dto.email());
        assertTrue(dto.active());
        assertTrue(dto.roleNames().contains("ADMIN"));
    }

    /**
     * Test mapping User entity to DTO when user has no roles.
     * Should default to 'Admin' role as fallback behavior.
     */
    @Test
    void toDto_WithoutRoles_ShouldDefaultToAdmin() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setActive(true);
        user.setRoles(Set.of());

        UserDto dto = UserMapper.toDto(user);

        assertTrue(dto.roleNames().contains("Admin"));
    }

    /**
     * Test null safety of toDto method.
     * Should return null when input entity is null.
     */
    @Test
    void toDto_WithNullUser_ShouldReturnNull() {
        UserDto dto = UserMapper.toDto(null);
        assertNull(dto);
    }

    /**
     * Test mapping from UserCreateRequestDto to User entity.
     * Should properly map all creation fields.
     */
    @Test
    void fromCreateRequest_ShouldMapCorrectly() {
        UserCreateRequestDto request = new UserCreateRequestDto();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setActive(true);

        User user = UserMapper.fromCreateRequest(request);

        assertEquals("newuser", user.getUsername());
        assertEquals("new@example.com", user.getEmail());
        assertTrue(user.isActive());
    }

    /**
     * Test null safety of fromCreateRequest method.
     * Should return null when input DTO is null.
     */
    @Test
    void fromCreateRequest_WithNullRequest_ShouldReturnNull() {
        User user = UserMapper.fromCreateRequest(null);
        assertNull(user);
    }

    /**
     * Test updating existing User entity with new data.
     * Should only update non-null fields from update DTO.
     */
    @Test
    void updateEntity_ShouldUpdateFields() {
        User user = new User();
        user.setEmail("old@example.com");
        user.setActive(false);

        UserUpdateRequestDto request = new UserUpdateRequestDto();
        request.setEmail("updated@example.com");
        request.setActive(true);

        UserMapper.updateEntity(user, request);

        assertEquals("updated@example.com", user.getEmail());
        assertTrue(user.isActive());
    }

    /**
     * Test update behavior with null values in DTO.
     * Should preserve original values when DTO fields are null.
     */
    @Test
    void updateEntity_WithNullValues_ShouldNotUpdate() {
        User user = new User();
        user.setEmail("original@example.com");
        user.setActive(true);

        UserUpdateRequestDto request = new UserUpdateRequestDto();
        request.setEmail(null);
        request.setActive(null);

        UserMapper.updateEntity(user, request);

        assertEquals("original@example.com", user.getEmail());
        assertTrue(user.isActive());
    }
}