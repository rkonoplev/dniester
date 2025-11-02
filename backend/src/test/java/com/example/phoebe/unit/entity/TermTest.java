package com.example.phoebe.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the Term entity, focusing on its business key-based equals and hashCode.
 */
class TermTest {

    @Test
    void constructorShouldNormalizeAndSetFields() {
        // Given: input strings with extra spaces and mixed case
        String name = "  Technology  ";
        String vocabulary = "  Category  ";

        // When: creating a new Term
        Term term = new Term(name, vocabulary);

        // Then: the fields should be normalized and correctly set
        assertEquals("Technology", term.getName(), "Name should be trimmed.");
        assertEquals("category", term.getVocabulary(), "Vocabulary should be trimmed and lowercased.");
    }

    @Test
    void equalsAndHashCodeShouldBeBasedOnBusinessKey() {
        // Given: two Term objects with the same business key (name + vocabulary)
        Term term1 = new Term("  Technology  ", "  CATEGORY  ");
        Term term2 = new Term("Technology", "category");

        // And a term with a different name
        Term term3 = new Term("Sports", "category");

        // And a term with a different vocabulary
        Term term4 = new Term("Technology", "tag");

        // When & Then: test equals and hashCode contracts
        assertEquals(term1, term2, "Terms with the same name and vocabulary should be equal.");
        assertEquals(term1.hashCode(), term2.hashCode(), "Hash codes should be the same for equal objects.");

        assertNotEquals(term1, term3, "Terms with different names should not be equal.");
        assertNotEquals(term1, term4, "Terms with different vocabularies should not be equal.");
    }

    @Test
    void equalsShouldReturnFalseForTransientEntityWithNullKeys() {
        // Given: two transient entities created with the default constructor
        Term term1 = new Term();
        Term term2 = new Term();

        // When & Then
        assertNotEquals(term1, term2, "Two new entities with null business keys should not be equal.");
    }

    @Test
    void toStringShouldContainKeyFields() {
        // Given
        Term term = new Term("Technology", "category");
        term.setId(1L); // Simulate persistence

        // When
        String toString = term.toString();

        // Then
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name='Technology'"));
        assertTrue(toString.contains("vocabulary='category'"));
    }
}