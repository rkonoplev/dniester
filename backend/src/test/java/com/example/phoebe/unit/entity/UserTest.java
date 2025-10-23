package com.example.phoebe.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;



class UserTest {

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        user = new User();
        role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
    }

    @Test
    void testGettersAndSetters() {
        user.setId(1L);
        assertEquals(1L, user.getId());

        user.setUsername("testuser");
        assertEquals("testuser", user.getUsername());

        user.setPassword("password123");
        assertEquals("password123", user.getPassword());

        user.setEmail("test@example.com");
        assertEquals("test@example.com", user.getEmail());

        user.setActive(false);
        assertFalse(user.isActive());

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        assertEquals(roles, user.getRoles());
    }

    @Test
    void testDefaultValues() {
        assertTrue(user.isActive());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void testEquals() {
        User user1 = new User();
        User user2 = new User();
        
        assertEquals(user1, user2);
        
        user1.setId(1L);
        user2.setId(1L);
        assertEquals(user1, user2);
        
        user2.setId(2L);
        assertNotEquals(user1, user2);
        
        assertNotEquals(user1, null);
        assertNotEquals(user1, "not a user");
    }

    @Test
    void testHashCode() {
        User user1 = new User();
        User user2 = new User();
        
        assertEquals(user1.hashCode(), user2.hashCode());
        
        user1.setId(1L);
        user2.setId(1L);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setActive(true);
        
        String toString = user.toString();
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("username='testuser'"));
        assertTrue(toString.contains("email='test@example.com'"));
        assertTrue(toString.contains("active=true"));
    }
}