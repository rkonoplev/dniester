package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.response.RoleDto;
import com.example.newsplatform.entity.Role;
import com.example.newsplatform.entity.User;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RoleMapper utility class.
 * Tests bidirectional mapping between Role entities and DTOs.
 * Includes user count calculation and null safety checks.
 */
class RoleMapperTest {

    /**
     * Test mapping Role entity to DTO with associated users.
     * Should correctly calculate user count from entity relationships.
     */
    @Test
    void toDto_ShouldMapCorrectly() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        
        User user1 = new User();
        User user2 = new User();
        role.setUsers(Set.of(user1, user2));

        RoleDto dto = RoleMapper.toDto(role);

        assertEquals(1L, dto.id());
        assertEquals("ADMIN", dto.name());
        assertEquals(2, dto.userCount());
    }

    /**
     * Test mapping Role entity when users collection is null.
     * Should return zero user count instead of throwing exception.
     */
    @Test
    void toDto_WithNullUsers_ShouldReturnZeroCount() {
        Role role = new Role();
        role.setId(1L);
        role.setName("EDITOR");
        role.setUsers(null);

        RoleDto dto = RoleMapper.toDto(role);

        assertEquals(1L, dto.id());
        assertEquals("EDITOR", dto.name());
        assertEquals(0, dto.userCount());
    }

    /**
     * Test null safety of toDto method.
     * Should return null when input entity is null.
     */
    @Test
    void toDto_WithNullRole_ShouldReturnNull() {
        RoleDto dto = RoleMapper.toDto(null);
        assertNull(dto);
    }

    /**
     * Test mapping from RoleDto to Role entity.
     * Should properly map ID and name fields.
     */
    @Test
    void fromDto_ShouldMapCorrectly() {
        RoleDto dto = new RoleDto(1L, "USER", 5);

        Role role = RoleMapper.fromDto(dto);

        assertEquals(1L, role.getId());
        assertEquals("USER", role.getName());
    }

    /**
     * Test null safety of fromDto method.
     * Should return null when input DTO is null.
     */
    @Test
    void fromDto_WithNullDto_ShouldReturnNull() {
        Role role = RoleMapper.fromDto(null);
        assertNull(role);
    }
}