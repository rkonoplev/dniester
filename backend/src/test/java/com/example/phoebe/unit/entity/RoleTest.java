package com.example.phoebe.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the Role entity, focusing on its business key-based equals/hashCode and helper methods.
 */
class RoleTest {

    @Test
    void constructorShouldNormalizeName() {
        // Given
        Role role = new Role("  editor  ", "Test Description");

        // Then
        assertEquals("EDITOR", role.getName());
        assertEquals("Test Description", role.getDescription());
    }

    @Test
    void testAddUser() {
        // Given
        Role role = new Role("ADMIN", null);
        User user = new User("testuser", "pass", "email", true);

        // When
        role.addUser(user);

        // Then
        assertTrue(role.getUsers().contains(user));
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    void testAddPermission() {
        // Given
        Role role = new Role("ADMIN", null);
        Permission permission = new Permission("news:create");

        // When
        role.addPermission(permission);

        // Then
        assertTrue(role.getPermissions().contains(permission));
        assertTrue(permission.getRoles().contains(role));
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        Role role1 = new Role("  ADMIN  ", "First description");
        Role role2 = new Role("ADMIN", "Second description"); // Same business key
        Role role3 = new Role("EDITOR", "First description");

        // Then
        assertEquals(role1, role2, "Roles with the same name should be equal.");
        assertEquals(role1.hashCode(), role2.hashCode(), "Hash codes should be the same for equal objects.");
        assertNotEquals(role1, role3, "Roles with different names should not be equal.");
    }
}
