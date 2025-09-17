package com.example.newsplatform.value;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AuthorInfo value object.
 * Tests immutable record for author metadata in news articles.
 * Verifies proper data encapsulation and value object behavior.
 */
class AuthorInfoTest {

    /**
     * Test AuthorInfo record construction with author details.
     * Verifies ID, username, and display name are properly stored.
     */
    @Test
    void constructor_ShouldCreateAuthorInfo() {
        AuthorInfo info = new AuthorInfo(1L, "john_doe", "John Doe");

        assertEquals(1L, info.id());
        assertEquals("john_doe", info.username());
        assertEquals("John Doe", info.displayName());
    }

    /**
     * Test equality comparison for identical AuthorInfo objects.
     * Value objects should be equal when all fields match.
     */
    @Test
    void equals_WithSameValues_ShouldReturnTrue() {
        AuthorInfo info1 = new AuthorInfo(1L, "john_doe", "John Doe");
        AuthorInfo info2 = new AuthorInfo(1L, "john_doe", "John Doe");

        assertEquals(info1, info2);
        assertEquals(info1.hashCode(), info2.hashCode());
    }

    /**
     * Test equality comparison for different AuthorInfo objects.
     * Should not be equal when any field differs.
     */
    @Test
    void equals_WithDifferentValues_ShouldReturnFalse() {
        AuthorInfo info1 = new AuthorInfo(1L, "john_doe", "John Doe");
        AuthorInfo info2 = new AuthorInfo(2L, "jane_doe", "Jane Doe");

        assertNotEquals(info1, info2);
    }

    /**
     * Test string representation includes all author fields.
     * Useful for debugging and logging author information.
     */
    @Test
    void toString_ShouldContainAllFields() {
        AuthorInfo info = new AuthorInfo(1L, "john_doe", "John Doe");
        String result = info.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("username=john_doe"));
        assertTrue(result.contains("displayName=John Doe"));
    }
}