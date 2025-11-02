package com.example.phoebe.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;



class UserTest {

    // This test class focuses on the User entity's behavior,
    // especially its business key-based equals and hashCode.

    @Test
    void constructorShouldNormalizeAndSetFields() {
        // Given: input strings with extra spaces and mixed case
        String username = "  TestUser  ";
        String email = "  TestEmail@Example.COM  ";

        // When: creating a new User
        User user = new User(username, "password", email, true);

        // Then: the fields should be normalized and correctly set
        assertEquals("testuser", user.getUsername(), "Username should be trimmed and lowercased.");
        assertEquals("testemail@example.com", user.getEmail(), "Email should be trimmed and lowercased.");
        assertEquals("password", user.getPassword());
        assertTrue(user.isActive());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }



    @Test
    void addRoleShouldMaintainBidirectionalConsistency() {
        // Given
        User user = new User("testuser", "pass", "email@test.com", true);
        Role role = new Role("ADMIN", "Admin role");

        // When
        user.addRole(role);

        // Then
        assertTrue(user.getRoles().contains(role), "User should have the role.");
        assertTrue(role.getUsers().contains(user), "Role should contain the user.");
    }

    @Test
    void removeRoleShouldMaintainBidirectionalConsistency() {
        // Given
        User user = new User("testuser", "pass", "email@test.com", true);
        Role role = new Role("ADMIN", "Admin role");
        user.addRole(role);

        // When
        user.removeRole(role);

        // Then
        assertFalse(user.getRoles().contains(role), "User should not have the role anymore.");
        assertFalse(role.getUsers().contains(user), "Role should not contain the user anymore.");
    }

    @Test
    void equalsAndHashCodeShouldBeBasedOnUsername() {
        // Given: two User objects with the same username but different other fields
        User user1 = new User("testuser", "password123", "email1@test.com", true);
        User user2 = new User("testuser", "password456", "email2@test.com", false);
        User user3 = new User("anotheruser", "password123", "email1@test.com", true);

        // When & Then:
        // 1. Test equals contract
        assertEquals(user1, user2, "Users with the same username should be equal.");
        assertNotEquals(user1, user3, "Users with different usernames should not be equal.");
        assertNotEquals(user1, null, "User should not be equal to null.");
        assertNotEquals(user1, new Object(), "User should not be equal to an object of a different type.");

        // 2. Test hashCode contract
        assertEquals(user1.hashCode(), user2.hashCode(), "Hash codes should be the same for equal objects.");
    }

    @Test
    void equalsShouldReturnFalseForTransientEntityWithNullUsername() {
        // Given: two transient entities created with the default constructor
        User user1 = new User();
        User user2 = new User();

        // When & Then
        assertNotEquals(user1, user2, "Two new entities with null business keys should not be equal.");
    }

    @Test
    void toStringShouldContainKeyFields() {
        // Given
        User user = new User("testuser", "password", "test@example.com", true);
        user.setId(1L); // Simulate being persisted

        // When
        String toString = user.toString();

        // Then
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("username='testuser'"));
        assertTrue(toString.contains("email='test@example.com'"));
        assertTrue(toString.contains("active=true"));
    }
}