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


class TermTest {

    private Term term;
    private News news;

    @BeforeEach
    void setUp() {
        term = new Term();
        news = new News();
        news.setId(1L);
        news.setTitle("Test News");
    }

    @Test
    void testDefaultConstructor() {
        Term newTerm = new Term();
        assertNotNull(newTerm.getNewsArticles());
        assertTrue(newTerm.getNewsArticles().isEmpty());
    }

    @Test
    void testParameterizedConstructor() {
        Term newTerm = new Term(1L, "Technology", "category");
        assertEquals(1L, newTerm.getId());
        assertEquals("Technology", newTerm.getName());
        assertEquals("category", newTerm.getVocabulary());
        assertNotNull(newTerm.getNewsArticles());
    }

    @Test
    void testGettersAndSetters() {
        term.setId(1L);
        assertEquals(1L, term.getId());

        term.setName("Technology");
        assertEquals("Technology", term.getName());

        term.setVocabulary("category");
        assertEquals("category", term.getVocabulary());

        Set<News> newsArticles = new HashSet<>();
        newsArticles.add(news);
        term.setNewsArticles(newsArticles);
        assertEquals(newsArticles, term.getNewsArticles());
    }

    @Test
    void testEquals() {
        Term term1 = new Term();
        Term term2 = new Term();
        
        assertEquals(term1, term2);
        
        term1.setId(1L);
        term2.setId(1L);
        assertEquals(term1, term2);
        
        term2.setId(2L);
        assertNotEquals(term1, term2);
        
        assertNotEquals(term1, null);
        assertNotEquals(term1, "not a term");
    }

    @Test
    void testEqualsWithNullId() {
        Term term1 = new Term();
        Term term2 = new Term();
        term1.setId(null);
        term2.setId(1L);
        
        assertNotEquals(term1, term2);
    }

    @Test
    void testHashCode() {
        Term term1 = new Term();
        Term term2 = new Term();
        
        assertEquals(term1.hashCode(), term2.hashCode());
    }

    @Test
    void testToString() {
        term.setId(1L);
        term.setName("Technology");
        term.setVocabulary("category");
        
        String toString = term.toString();
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name='Technology'"));
        assertTrue(toString.contains("vocabulary='category'"));
    }
}