package com.example.newsplatform.repository;

import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.Term;
import com.example.newsplatform.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for bulk operation repository methods.
 * Tests custom query methods for bulk operations.
 */
@DataJpaTest
@ActiveProfiles("test")
class NewsRepositoryBulkIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NewsRepository newsRepository;

    private User author1;
    private User author2;
    private Term term1;
    private Term term2;
    private News news1;
    private News news2;
    private News news3;

    @BeforeEach
    void setUp() {
        // Create test users
        author1 = new User();
        author1.setUsername("author1");
        author1.setEmail("author1@test.com");
        author1.setActive(true);
        author1 = entityManager.persistAndFlush(author1);

        author2 = new User();
        author2.setUsername("author2");
        author2.setEmail("author2@test.com");
        author2.setActive(true);
        author2 = entityManager.persistAndFlush(author2);

        // Create test terms
        term1 = new Term();
        term1.setName("Technology");
        term1.setVocabulary("category");
        term1 = entityManager.persistAndFlush(term1);

        term2 = new Term();
        term2.setName("Sports");
        term2.setVocabulary("category");
        term2 = entityManager.persistAndFlush(term2);

        // Create test news articles
        news1 = new News();
        news1.setTitle("Tech News 1");
        news1.setBody("Technology content");
        news1.setTeaser("Tech teaser");
        news1.setPublished(true);
        news1.setPublicationDate(LocalDateTime.now());
        news1.setAuthor(author1);
        news1.setTerms(new HashSet<>(Set.of(term1)));
        news1 = entityManager.persistAndFlush(news1);

        news2 = new News();
        news2.setTitle("Sports News 1");
        news2.setBody("Sports content");
        news2.setTeaser("Sports teaser");
        news2.setPublished(true);
        news2.setPublicationDate(LocalDateTime.now());
        news2.setAuthor(author2);
        news2.setTerms(new HashSet<>(Set.of(term2)));
        news2 = entityManager.persistAndFlush(news2);

        news3 = new News();
        news3.setTitle("Tech News 2");
        news3.setBody("More technology content");
        news3.setTeaser("Another tech teaser");
        news3.setPublished(false);
        news3.setPublicationDate(LocalDateTime.now());
        news3.setAuthor(author1);
        news3.setTerms(new HashSet<>(Set.of(term1)));
        news3 = entityManager.persistAndFlush(news3);

        entityManager.clear();
    }

    @Test
    void findAllIds_ShouldReturnAllNewsIds() {
        // When
        List<Long> ids = newsRepository.findAllIds();

        // Then
        assertThat(ids).hasSize(3);
        assertThat(ids).containsExactlyInAnyOrder(news1.getId(), news2.getId(), news3.getId());
    }

    @Test
    void findIdsByTermId_ShouldReturnNewsIdsForSpecificTerm() {
        // When
        List<Long> techIds = newsRepository.findIdsByTermId(term1.getId());
        List<Long> sportsIds = newsRepository.findIdsByTermId(term2.getId());

        // Then
        assertThat(techIds).hasSize(2);
        assertThat(techIds).containsExactlyInAnyOrder(news1.getId(), news3.getId());
        
        assertThat(sportsIds).hasSize(1);
        assertThat(sportsIds).contains(news2.getId());
    }

    @Test
    void findIdsByAuthorId_ShouldReturnNewsIdsForSpecificAuthor() {
        // When
        List<Long> author1Ids = newsRepository.findIdsByAuthorId(author1.getId());
        List<Long> author2Ids = newsRepository.findIdsByAuthorId(author2.getId());

        // Then
        assertThat(author1Ids).hasSize(2);
        assertThat(author1Ids).containsExactlyInAnyOrder(news1.getId(), news3.getId());
        
        assertThat(author2Ids).hasSize(1);
        assertThat(author2Ids).contains(news2.getId());
    }

    @Test
    void unpublishByIds_ShouldUnpublishSpecifiedArticles() {
        // Given
        List<Long> idsToUnpublish = List.of(news1.getId(), news2.getId());
        
        // Verify initial state
        assertThat(newsRepository.findById(news1.getId()).get().isPublished()).isTrue();
        assertThat(newsRepository.findById(news2.getId()).get().isPublished()).isTrue();
        assertThat(newsRepository.findById(news3.getId()).get().isPublished()).isFalse();

        // When
        newsRepository.unpublishByIds(idsToUnpublish);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(newsRepository.findById(news1.getId()).get().isPublished()).isFalse();
        assertThat(newsRepository.findById(news2.getId()).get().isPublished()).isFalse();
        assertThat(newsRepository.findById(news3.getId()).get().isPublished()).isFalse(); // unchanged
    }

    @Test
    void findIdsByTermId_NonExistentTerm_ShouldReturnEmptyList() {
        // When
        List<Long> ids = newsRepository.findIdsByTermId(999L);

        // Then
        assertThat(ids).isEmpty();
    }

    @Test
    void findIdsByAuthorId_NonExistentAuthor_ShouldReturnEmptyList() {
        // When
        List<Long> ids = newsRepository.findIdsByAuthorId(999L);

        // Then
        assertThat(ids).isEmpty();
    }

    @Test
    void unpublishByIds_EmptyList_ShouldNotAffectAnyArticles() {
        // Given
        boolean initialNews1Published = newsRepository.findById(news1.getId()).get().isPublished();
        boolean initialNews2Published = newsRepository.findById(news2.getId()).get().isPublished();

        // When
        newsRepository.unpublishByIds(List.of());
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(newsRepository.findById(news1.getId()).get().isPublished()).isEqualTo(initialNews1Published);
        assertThat(newsRepository.findById(news2.getId()).get().isPublished()).isEqualTo(initialNews2Published);
    }
}