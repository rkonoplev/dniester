package com.example.phoebe.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class PermissionTest {

    private Permission permission;

    @BeforeEach
    void setUp() {
        permission = new Permission();
    }

    @Test
    void testGettersAndSetters() {
        permission.setId(1L);
        assertEquals(1L, permission.getId());

        permission.setName("news:read");
        assertEquals("news:read", permission.getName());
    }

    @Test
    void testEquals() {
        Permission permission1 = new Permission();
        Permission permission2 = new Permission();
        
        assertEquals(permission1, permission2);
        
        permission1.setId(1L);
        permission2.setId(1L);
        assertEquals(permission1, permission2);
        
        permission2.setId(2L);
        assertNotEquals(permission1, permission2);
        
        assertNotEquals(permission1, null);
        assertNotEquals(permission1, "not a permission");
    }

    @Test
    void testHashCode() {
        Permission permission1 = new Permission();
        Permission permission2 = new Permission();
        
        assertEquals(permission1.hashCode(), permission2.hashCode());
        
        permission1.setId(1L);
        permission2.setId(1L);
        assertEquals(permission1.hashCode(), permission2.hashCode());
    }
}