package com.example.newsplatform.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TermTest {

    private Term term;
    private News newsArticle;

    @BeforeEach
    void setUp() {
        term = new Term();
        newsArticle = new News();
        newsArticle.setId(1L);
    }

    @Test
    void testDefaultConstructor() {
        Term newTerm = new Term();
        assertNull(newTerm.getId());
        assertNull(newTerm.getName());
        assertNull(newTerm.getVocabulary());
        assertNotNull(newTerm.getNewsArticles());
        assertTrue(newTerm.getNewsArticles().isEmpty());
    }

    @Test
    void testParameterizedConstructor() {
        Term newTerm = new Term(1L, "Sports", "category");
        assertEquals(1L, newTerm.getId());
        assertEquals("Sports", newTerm.getName());
        assertEquals("category", newTerm.getVocabulary());
        assertNotNull(newTerm.getNewsArticles());
        assertTrue(newTerm.getNewsArticles().isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        term.setId(1L);
        assertEquals(1L, term.getId());

        term.setName("Technology");
        assertEquals("Technology", term.getName());

        term.setVocabulary("category");
        assertEquals("category", term.getVocabulary());

        Set<News> newsSet = new HashSet<>();
        newsSet.add(newsArticle);
        term.setNewsArticles(newsSet);
        assertEquals(newsSet, term.getNewsArticles());
    }

    @Test
    void testEquals() {
        Term term1 = new Term();
        Term term2 = new Term();

        // Two new, distinct instances with null IDs should not be equal
        assertNotEquals(term1, term2);
        // An object should always be equal to itself
        assertEquals(term1, term1);

        term1.setId(1L);
        term2.setId(1L);
        assertEquals(term1, term2);

        term2.setId(2L);
        assertNotEquals(term1, term2);

        assertNotEquals(term1, null);
        assertNotEquals(term1, "not a term");
    }

    @Test
    void testHashCode() {
        Term term1 = new Term();
        Term term2 = new Term();

        assertEquals(term1.hashCode(), term2.hashCode());

        term1.setId(1L);
        term2.setId(1L);
        assertEquals(term1.hashCode(), term2.hashCode());
    }

    @Test
    void testToString() {
        term.setId(1L);
        term.setName("Politics");
        term.setVocabulary("category");

        String toString = term.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name='Politics'"));
        assertTrue(toString.contains("vocabulary='category'"));
    }
}
