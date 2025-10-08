package com.example.newsplatform.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Unit tests for the News entity.
 * Covers field accessors, default values, lifecycle methods, equality, hashing, and string output.
 */
class NewsTest {

    private News news;
    private User author;
    private Term term;

    @BeforeEach
    void setUp() {
        news = new News();
        author = new User();
        author.setId(1L);
        author.setUsername("testuser");

        term = new Term();
        term.setId(1L);
        term.setName("Technology");
    }

    /**
     * Tests all getters and setters to ensure field values are correctly assigned and retrieved.
     */
    @Test
    void testGettersAndSetters() {
        news.setId(1L);
        assertEquals(1L, news.getId());

        news.setTitle("Test Title");
        assertEquals("Test Title", news.getTitle());

        news.setBody("Test body content");
        assertEquals("Test body content", news.getBody());

        news.setTeaser("Test teaser");
        assertEquals("Test teaser", news.getTeaser());

        LocalDateTime now = LocalDateTime.now();
        news.setPublicationDate(now);
        assertEquals(now, news.getPublicationDate());

        news.setPublished(true);
        assertTrue(news.isPublished());

        news.setAuthor(author);
        assertEquals(author, news.getAuthor());

        Set<Term> terms = new HashSet<>();
        terms.add(term);
        news.setTerms(terms);
        assertEquals(terms, news.getTerms());
    }

    /**
     * Verifies default values for a newly instantiated News object.
     */
    @Test
    void testDefaultValues() {
        assertFalse(news.isPublished());
        assertNotNull(news.getTerms());
        assertTrue(news.getTerms().isEmpty());
    }

    /**
     * Verifies that onCreate sets timestamps and publication date correctly.
     */
    @Test
    void testOnCreate() {
        assertNull(news.getCreatedAt());
        assertNull(news.getUpdatedAt());

        news.onCreate();

        assertNotNull(news.getCreatedAt());
        assertNotNull(news.getUpdatedAt());
        assertNotNull(news.getPublicationDate());
    }

    /**
     * Ensures that onCreate does not overwrite an existing publication date.
     */
    @Test
    void testOnCreateWithExistingPublicationDate() {
        LocalDateTime existingDate = LocalDateTime.of(2023, 1, 1, 12, 0);
        news.setPublicationDate(existingDate);

        news.onCreate();

        assertEquals(existingDate, news.getPublicationDate());
    }

    /**
     * Verifies that onUpdate updates the updatedAt timestamp to a later value.
     */
    @Test
    void testOnUpdate() {
        news.onCreate();
        LocalDateTime originalUpdatedAt = news.getUpdatedAt();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        news.onUpdate();

        assertTrue(news.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    /**
     * Tests equality logic based on ID.
     * With current equals() implementation, objects with null ID are not equal.
     */
    @Test
    void testEquals() {
        News news1 = new News();
        News news2 = new News();

        // Objects with null ID are not equal
        assertNotEquals(news1, news2);

        news1.setId(1L);
        news2.setId(1L);
        assertEquals(news1, news2);

        news2.setId(2L);
        assertNotEquals(news1, news2);

        news2.setId(null);
        assertNotEquals(news1, news2);
    }

    /**
     * Tests hashCode consistency based on ID.
     */
    @Test
    void testHashCode() {
        News news1 = new News();
        News news2 = new News();

        assertEquals(news1.hashCode(), news2.hashCode());

        news1.setId(1L);
        news2.setId(1L);
        assertEquals(news1.hashCode(), news2.hashCode());
    }

    /**
     * Verifies that toString includes key fields and author ID when present.
     */
    @Test
    void testToString() {
        news.setId(1L);
        news.setTitle("Test Title");
        news.setPublished(true);
        news.setPublicationDate(LocalDateTime.of(2023, 1, 1, 12, 0));
        news.setAuthor(author);

        String toString = news.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("title='Test Title'"));
        assertTrue(toString.contains("published=true"));
        assertTrue(toString.contains("author=1"));
    }

    /**
     * Verifies that toString handles null author gracefully.
     */
    @Test
    void testToStringWithNullAuthor() {
        news.setId(1L);
        news.setTitle("Test Title");

        String toString = news.toString();

        assertTrue(toString.contains("author=null"));
    }
}