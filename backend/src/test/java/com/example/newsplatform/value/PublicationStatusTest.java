package com.example.newsplatform.value;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PublicationStatus value object.
 * Tests immutable record for article publication state and scheduling.
 * Includes factory methods for common publication states.
 */
class PublicationStatusTest {

    /**
     * Test PublicationStatus construction with all fields.
     * Verifies publication flag, scheduled date, and status are stored.
     */
    @Test
    void constructor_ShouldCreatePublicationStatus() {
        LocalDateTime now = LocalDateTime.now();
        PublicationStatus status = new PublicationStatus(true, now, "PUBLISHED");

        assertTrue(status.published());
        assertEquals(now, status.scheduledDate());
        assertEquals("PUBLISHED", status.status());
    }

    /**
     * Test factory method for creating draft status.
     * Should create unpublished status with null date and DRAFT status.
     */
    @Test
    void draft_ShouldCreateDraftStatus() {
        PublicationStatus status = PublicationStatus.draft();

        assertFalse(status.published());
        assertNull(status.scheduledDate());
        assertEquals("DRAFT", status.status());
    }

    /**
     * Test factory method for creating published status.
     * Should create published status with specified date and PUBLISHED status.
     */
    @Test
    void published_ShouldCreatePublishedStatus() {
        LocalDateTime now = LocalDateTime.now();
        PublicationStatus status = PublicationStatus.published(now);

        assertTrue(status.published());
        assertEquals(now, status.scheduledDate());
        assertEquals("PUBLISHED", status.status());
    }

    /**
     * Test factory method for creating scheduled status.
     * Should create unpublished status with future date and SCHEDULED status.
     */
    @Test
    void scheduled_ShouldCreateScheduledStatus() {
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        PublicationStatus status = PublicationStatus.scheduled(future);

        assertFalse(status.published());
        assertEquals(future, status.scheduledDate());
        assertEquals("SCHEDULED", status.status());
    }

    /**
     * Test equality for identical publication statuses.
     * Should be equal when all publication fields match.
     */
    @Test
    void equals_WithSameValues_ShouldReturnTrue() {
        LocalDateTime now = LocalDateTime.now();
        PublicationStatus status1 = new PublicationStatus(true, now, "PUBLISHED");
        PublicationStatus status2 = new PublicationStatus(true, now, "PUBLISHED");

        assertEquals(status1, status2);
        assertEquals(status1.hashCode(), status2.hashCode());
    }
}