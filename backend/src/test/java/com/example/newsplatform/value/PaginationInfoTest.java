package com.example.newsplatform.value;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for PaginationInfo value object.
 * Tests immutable record for pagination metadata in API responses.
 * Verifies proper construction, equality, and string representation.
 */
class PaginationInfoTest {

    /**
     * Test PaginationInfo record construction with all fields.
     * Verifies all pagination metadata is properly stored.
     */
    @Test
    void constructorShouldCreatePaginationInfo() {
        PaginationInfo info = new PaginationInfo(0, 10, 100L, 10, true, false);

        assertEquals(0, info.currentPage());
        assertEquals(10, info.totalPages());
        assertEquals(100L, info.totalElements());
        assertEquals(10, info.pageSize());
        assertTrue(info.hasNext());
        assertFalse(info.hasPrevious());
    }

    /**
     * Test equality comparison for identical PaginationInfo objects.
     * Records should be equal when all fields match.
     */
    @Test
    void equalsWithSameValuesShouldReturnTrue() {
        PaginationInfo info1 = new PaginationInfo(0, 10, 100L, 10, true, false);
        PaginationInfo info2 = new PaginationInfo(0, 10, 100L, 10, true, false);

        assertEquals(info1, info2);
        assertEquals(info1.hashCode(), info2.hashCode());
    }

    /**
     * Test equality comparison for different PaginationInfo objects.
     * Records should not be equal when any field differs.
     */
    @Test
    void equalsWithDifferentValuesShouldReturnFalse() {
        PaginationInfo info1 = new PaginationInfo(0, 10, 100L, 10, true, false);
        PaginationInfo info2 = new PaginationInfo(1, 10, 100L, 10, true, true);

        assertNotEquals(info1, info2);
    }

    /**
     * Test string representation includes all field names and values.
     * Useful for debugging and logging pagination state.
     */
    @Test
    void toStringShouldContainAllFields() {
        PaginationInfo info = new PaginationInfo(0, 10, 100L, 10, true, false);
        String result = info.toString();

        assertTrue(result.contains("currentPage=0"));
        assertTrue(result.contains("totalPages=10"));
        assertTrue(result.contains("totalElements=100"));
        assertTrue(result.contains("pageSize=10"));
    }
}