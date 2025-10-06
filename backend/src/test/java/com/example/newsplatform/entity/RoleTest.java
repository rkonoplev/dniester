package com.example.newsplatform.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


class RoleTest {

    private Role role;
    private User user;
    private Permission permission;

    @BeforeEach
    void setUp() {
        role = new Role();
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        
        permission = new Permission();
        permission.setId(1L);
        permission.setName("news:read");
    }

    @Test
    void testDefaultConstructor() {
        Role newRole = new Role();
        assertNotNull(newRole.getUsers());
        assertNotNull(newRole.getPermissions());
        assertTrue(newRole.getUsers().isEmpty());
        assertTrue(newRole.getPermissions().isEmpty());
    }

    @Test
    void testParameterizedConstructor() {
        Role newRole = new Role("ADMIN", "Administrator role");
        assertEquals("ADMIN", newRole.getName());
        assertEquals("Administrator role", newRole.getDescription());
        assertNotNull(newRole.getUsers());
        assertNotNull(newRole.getPermissions());
    }

    @Test
    void testGettersAndSetters() {
        role.setId(1L);
        assertEquals(1L, role.getId());

        role.setName("ADMIN");
        assertEquals("ADMIN", role.getName());

        role.setDescription("Administrator role");
        assertEquals("Administrator role", role.getDescription());

        Set<User> users = new HashSet<>();
        users.add(user);
        role.setUsers(users);
        assertEquals(users, role.getUsers());

        Set<Permission> permissions = new HashSet<>();
        permissions.add(permission);
        role.setPermissions(permissions);
        assertEquals(permissions, role.getPermissions());
    }

    @Test
    void testAddUser() {
        role.addUser(user);
        
        assertTrue(role.getUsers().contains(user));
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    void testRemoveUser() {
        role.addUser(user);
        role.removeUser(user);
        
        assertFalse(role.getUsers().contains(user));
        assertFalse(user.getRoles().contains(role));
    }

    @Test
    void testEquals() {
        Role role1 = new Role();
        Role role2 = new Role();
        
        assertEquals(role1, role2);
        
        role1.setId(1L);
        role2.setId(1L);
        assertEquals(role1, role2);
        
        role2.setId(2L);
        assertNotEquals(role1, role2);
        
        assertNotEquals(role1, null);
        assertNotEquals(role1, "not a role");
    }

    @Test
    void testHashCode() {
        Role role1 = new Role();
        Role role2 = new Role();
        
        assertEquals(role1.hashCode(), role2.hashCode());
        
        role1.setId(1L);
        role2.setId(1L);
        assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void testToString() {
        role.setId(1L);
        role.setName("ADMIN");
        role.setDescription("Administrator role");
        
        String toString = role.toString();
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name='ADMIN'"));
        assertTrue(toString.contains("description='Administrator role'"));
    }
}