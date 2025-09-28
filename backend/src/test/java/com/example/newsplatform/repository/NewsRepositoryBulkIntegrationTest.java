package com.example.newsplatform.repository;

import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.Term;
import com.example.newsplatform.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class NewsRepositoryBulkIntegrationTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TermRepository termRepository;

    private User author1;
    private User author2;
    private Term termTech;
    private Term termSports;

    @BeforeEach
    void setUp() {
        // Clear repositories to ensure a clean state for each test
        newsRepository.deleteAll();
        userRepository.deleteAll();
        termRepository.deleteAll();

        // Create authors
        author1 = new User();
        author1.setUsername("author1");
        author1.setPassword("pass");
        author1.setActive(true);
        userRepository.save(author1);

        author2 = new User();
        author2.setUsername("author2");
        author2.setPassword("pass");
        author2.setActive(true);
        userRepository.save(author2);

        // Create terms
        termTech = new Term();
        termTech.setName("Technology");
        termRepository.save(termTech);

        termSports = new Term();
        termSports.setName("Sports");
        termRepository.save(termSports);

        // Create news articles
        News news1 = new News();
        news1.setTitle("Tech News 1");
        news1.setBody("Technology content");
        news1.setAuthor(author1);
        news1.setPublished(true);
        news1.getTerms().add(termTech);
        newsRepository.save(news1);

        News news2 = new News();
        news2.setTitle("Sports News");
        news2.setBody("Sports content");
        news2.setAuthor(author2);
        news2.setPublished(true);
        news2.getTerms().add(termSports);
        newsRepository.save(news2);

        News news3 = new News();
        news3.setTitle("Tech News 2");
        news3.setBody("More technology content");
        news3.setAuthor(author1);
        news3.setPublished(true);
        news3.getTerms().add(termTech);
        newsRepository.save(news3);
    }

    @Test
    void testUnpublishByIds() {
        // Given
        List<Long> allIds = newsRepository.findAllIds();
        assertEquals(3, allIds.size());

        // When
        List<Long> idsToUnpublish = Arrays.asList(allIds.get(0), allIds.get(2));
        newsRepository.unpublishByIds(idsToUnpublish);

        // Then
        Optional<News> unpublishedNews1 = newsRepository.findById(idsToUnpublish.get(0));
        Optional<News> unpublishedNews2 = newsRepository.findById(idsToUnpublish.get(1));
        Optional<News> stillPublishedNews = newsRepository.findById(allIds.get(1));

        assertTrue(unpublishedNews1.isPresent());
        assertFalse(unpublishedNews1.get().isPublished());

        assertTrue(unpublishedNews2.isPresent());
        assertFalse(unpublishedNews2.get().isPublished());

        assertTrue(stillPublishedNews.isPresent());
        assertTrue(stillPublishedNews.get().isPublished());
    }
}