package com.example.newsplatform.dto;

import com.example.newsplatform.entity.Term;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.repository.TermRepository;
import com.example.newsplatform.repository.UserRepository;
import com.example.newsplatform.service.NewsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NewsCreateRequestDtoIntegrationTest {

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TermRepository termRepository;

    @Test
    void createNews_WithValidDto_ShouldCreateNewsSuccessfully() {
        User author = createTestUser("author", "author@test.com");
        Term category = createTestTerm("Technology", "category");

        NewsCreateRequestDto dto = new NewsCreateRequestDto();
        dto.setTitle("Test News Article");
        dto.setBody("This is a test news article body");
        dto.setTeaser("Test teaser");
        dto.setAuthorId(author.getId());
        dto.setCategoryId(category.getId());
        dto.setPublicationDate(LocalDateTime.now());
        dto.setPublished(true);

        NewsDto result = newsService.create(dto);

        assertNotNull(result.id());
        assertEquals("Test News Article", result.title());
        assertEquals("This is a test news article body", result.body());
        assertEquals("Test teaser", result.teaser());
        assertEquals(author.getId(), result.authorId());
        assertEquals(category.getId(), result.categoryId());
        assertTrue(result.published());
    }

    @Test
    void createNews_WithOptionalFields_ShouldCreateNewsWithDefaults() {
        User author = createTestUser("author2", "author2@test.com");
        Term category = createTestTerm("Sports", "category");

        NewsCreateRequestDto dto = new NewsCreateRequestDto();
        dto.setTitle("Minimal News");
        dto.setBody("Minimal body");
        dto.setAuthorId(author.getId());
        dto.setCategoryId(category.getId());
        dto.setPublicationDate(LocalDateTime.now());
        dto.setPublished(false);

        NewsDto result = newsService.create(dto);

        assertNotNull(result.id());
        assertEquals("Minimal News", result.title());
        assertEquals("Minimal body", result.body());
        assertNull(result.teaser());
        assertFalse(result.published());
    }

    private User createTestUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setActive(true);
        return userRepository.save(user);
    }

    private Term createTestTerm(String name, String vocabulary) {
        Term term = new Term();
        term.setName(name);
        term.setVocabulary(vocabulary);
        return termRepository.save(term);
    }
}