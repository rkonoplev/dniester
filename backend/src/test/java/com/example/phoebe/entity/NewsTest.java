package com.example.phoebe.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.phoebe.PhoebeApplication;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ContextConfiguration;

/**
 * Unit tests for the News entity.
 * Uses Spring Data JPA Auditing to validate createdAt and updatedAt behavior.
 */
@DataJpaTest
@Import(NewsTest.AuditingTestConfig.class)
@ContextConfiguration(classes = PhoebeApplication.class)
class NewsTest {

    @Resource
    private TestEntityManager em;

    private User author;
    private Term term;

    @BeforeEach
    void setUp() {
        author = buildAndPersistUser("testuser", "test@example.com", "{noop}password");

        term = new Term();
        term.setName("Technology");
        term.setVocabulary("category");
        em.persistAndFlush(term);
    }

    /**
     * Verifies getters and setters assign and return expected values.
     */
    @Test
    void gettersAndSetters() {
        News news = new News();

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
     * Verifies default values for a new instance before it is persisted.
     */
    @Test
    void defaultValuesBeforePersist() {
        News news = new News();
        assertFalse(news.isPublished());
        assertNotNull(news.getTerms());
        assertTrue(news.getTerms().isEmpty());
    }

    /**
     * Verifies that persistence sets publicationDate if it was null and
     * auditing fills createdAt and updatedAt.
     */
    @Test
    void persistSetsPublicationDateAndAuditingTimestamps() {
        News news = buildNews("Title A", author);

        em.persistAndFlush(news);
        em.clear();

        News reloaded = em.find(News.class, news.getId());

        assertNotNull(reloaded.getPublicationDate());
        assertNotNull(reloaded.getCreatedAt());
        assertNotNull(reloaded.getUpdatedAt());
    }

    /**
     * Ensures that an existing publicationDate is not overwritten on persist.
     */
    @Test
    void persistKeepsExistingPublicationDate() {
        LocalDateTime existing = LocalDateTime.of(2023, 1, 1, 12, 0);

        News news = buildNews("Title B", author);
        news.setPublicationDate(existing);

        em.persistAndFlush(news);
        em.clear();

        News reloaded = em.find(News.class, news.getId());
        assertEquals(existing, reloaded.getPublicationDate());
    }

    /**
     * Verifies that updatedAt changes on update and version increases.
     * Some databases store timestamps with second granularity; we assert
     * that the timestamp does not go backward and that version increases.
     */
    @Test
    void updatedAtChangesOnUpdateAndVersionIncrements() throws InterruptedException {
        News news = buildNews("Title C", author);
        em.persistAndFlush(news);

        Long id = news.getId();
        Long v1 = news.getVersion();
        LocalDateTime t1 = news.getUpdatedAt();

        // Small delay to avoid same-second timestamps on some DBs
        Thread.sleep(5);

        News managed = em.find(News.class, id);
        managed.setTitle("Title C Updated");
        em.persistAndFlush(managed);
        em.clear();

        News reloaded = em.find(News.class, id);
        Long v2 = reloaded.getVersion();
        LocalDateTime t2 = reloaded.getUpdatedAt();

        assertTrue(v2 > v1);
        assertNotNull(t1);
        assertNotNull(t2);
        assertTrue(!t2.isBefore(t1));
    }

    /**
     * Tests equality logic based on non-null ID.
     * Objects with null ID are not equal.
     */
    @Test
    void equalsById() {
        News n1 = new News();
        News n2 = new News();

        assertNotEquals(n1, n2);

        n1.setId(1L);
        n2.setId(1L);
        assertEquals(n1, n2);

        n2.setId(2L);
        assertNotEquals(n1, n2);

        n2.setId(null);
        assertNotEquals(n1, n2);
    }

    /**
     * Tests hashCode stability and id-based equality.
     */
    @Test
    void hashCodeContract() {
        News n1 = new News();
        News n2 = new News();

        assertEquals(n1.hashCode(), n2.hashCode());

        n1.setId(1L);
        n2.setId(1L);
        assertEquals(n1.hashCode(), n2.hashCode());
    }

    /**
     * Verifies that toString includes key fields and author info.
     * The test is resilient to formatting differences and author representation.
     */
    @Test
    void toStringIncludesKeyFields() {
        News news = buildNews("Test Title", author);
        news.setId(1L);
        news.setPublished(true);
        news.setPublicationDate(LocalDateTime.of(2023, 1, 1, 12, 0));

        String s = news.toString();

        assertTrue(s.startsWith("News{"), () -> "Unexpected toString: " + s);
        assertTrue(Pattern.compile("\\bid=1\\b").matcher(s).find(),
                () -> "Expected 'id=1' in: " + s);
        // Accept either title='Test Title' or any form containing the title text
        assertTrue(s.contains("Test Title"),
                () -> "Expected title text in: " + s);
        assertTrue(Pattern.compile("\\bpublished=true\\b").matcher(s).find(),
                () -> "Expected 'published=true' in: " + s);
        // Accept author=<id>, author=null, or author=User{...}
        assertTrue(Pattern.compile("author=(?:\\d+|null|User\\{.*\\})").matcher(s).find(),
                () -> "Expected author info in: " + s);
        // If author has id, prefer seeing that exact id somewhere
        if (author.getId() != null) {
            boolean hasExactId =
                    s.contains("author=" + author.getId()) ||
                            (s.contains("User{") && s.contains("id=" + author.getId()));
            assertTrue(hasExactId,
                    () -> "Expected author id " + author.getId() + " in: " + s);
        }
    }

    // === Test helpers ===

    private News buildNews(String title, User authorRef) {
        News n = new News();
        n.setTitle(title);
        n.setAuthor(authorRef);
        return n;
    }

    private User buildAndPersistUser(String username, String email, String password) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(password);
        u.setActive(true);
        em.persistAndFlush(u);
        return u;
    }

    /**
     * Minimal auditing configuration for tests.
     * createdBy/lastModifiedBy are not used, so AuditorAware returns empty.
     */
    @Configuration
    @EnableJpaAuditing
    static class AuditingTestConfig implements AuditorAware<String> {
        @Override
        public java.util.Optional<String> getCurrentAuditor() {
            return java.util.Optional.empty();
        }
    }
}