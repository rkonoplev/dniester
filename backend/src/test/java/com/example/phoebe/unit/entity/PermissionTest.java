package com.example.phoebe.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Unit tests for the Permission entity, focusing on its business key-based equals and hashCode.
 */
class PermissionTest {

    @Test
    void constructorShouldNormalizeName() {
        // Given
        Permission permission = new Permission("  news:READ  ");

        // Then
        assertEquals("news:read", permission.getName());
    }

    @Test
    void testEquals() {
        // Given
        Permission permission1 = new Permission("news:read");
        Permission permission2 = new Permission("  news:READ  "); // Same business key
        Permission permission3 = new Permission("news:write");

        // Then
        assertEquals(permission1, permission2);
        assertNotEquals(permission1, permission3);
    }

    @Test
    void testHashCode() {
        // Given
        Permission permission1 = new Permission("news:read");
        Permission permission2 = new Permission("  news:READ  "); // Same business key

        // Then
        assertEquals(permission1.hashCode(), permission2.hashCode());
    }
}
