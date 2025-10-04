package com.example.newsplatform.value;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for BulkOperationResult value object.
 * Tests immutable record for bulk operation outcomes.
 * Tracks successful, failed operations and provides operation summary.
 */
class BulkOperationResultTest {

    /**
     * Test BulkOperationResult construction with operation details.
     * Verifies affected count, processed IDs, failed IDs, and message storage.
     */
    @Test
    void constructorShouldCreateBulkOperationResult() {
        Set<Long> processedIds = Set.of(1L, 2L, 3L);
        Set<Long> failedIds = Set.of();
        BulkOperationResult result = new BulkOperationResult(3, processedIds, failedIds, "DELETE completed");

        assertEquals(3, result.affectedCount());
        assertEquals(processedIds, result.processedIds());
        assertEquals(failedIds, result.failedIds());
        assertEquals("DELETE completed", result.message());
    }

    /**
     * Test equality for identical bulk operation results.
     * Results should be equal when all operation data matches.
     */
    @Test
    void equalsWithSameValuesShouldReturnTrue() {
        Set<Long> ids = Set.of(1L, 2L);
        Set<Long> failed = Set.of();
        BulkOperationResult result1 = new BulkOperationResult(2, ids, failed, "DELETE");
        BulkOperationResult result2 = new BulkOperationResult(2, ids, failed, "DELETE");

        assertEquals(result1, result2);
        assertEquals(result1.hashCode(), result2.hashCode());
    }

    /**
     * Test equality for different bulk operation results.
     * Should not be equal when operation outcomes differ.
     */
    @Test
    void equalsWithDifferentValuesShouldReturnFalse() {
        BulkOperationResult result1 = new BulkOperationResult(2, Set.of(1L, 2L), Set.of(), "DELETE");
        BulkOperationResult result2 = new BulkOperationResult(3, Set.of(1L, 2L, 3L), Set.of(), "UNPUBLISH");

        assertNotEquals(result1, result2);
    }

    /**
     * Test string representation includes operation summary.
     * Useful for logging bulk operation outcomes and debugging.
     */
    @Test
    void toStringShouldContainAllFields() {
        BulkOperationResult result = new BulkOperationResult(2, Set.of(1L, 2L), Set.of(), "DELETE");
        String resultString = result.toString();

        assertTrue(resultString.contains("affectedCount=2"));
        assertTrue(resultString.contains("message=DELETE"));
    }
}