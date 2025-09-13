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
class NewsUpdateRequestDtoIntegrationTest {

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TermRepository termRepository;

    @Test
    void updateNews_WithPartialDto_ShouldUpdateOnlyProvidedFields() {
        User author = createTestUser("author", "author@test.com");
        Term category = createTestTerm("Technology", "category");

        NewsCreateRequestDto createDto = new NewsCreateRequestDto();
        createDto.setTitle("Original Title");
        createDto.setBody("Original Body");
        createDto.setAuthorId(author.getId());
        createDto.setCategoryId(category.getId());
        createDto.setPublicationDate(LocalDateTime.now());
        createDto.setPublished(false);

        NewsDto created = newsService.create(createDto);

        NewsUpdateRequestDto updateDto = new NewsUpdateRequestDto();
        updateDto.setTitle("Updated Title");
        updateDto.setPublished(true);

        NewsDto updated = newsService.update(created.id(), updateDto);

        assertEquals("Updated Title", updated.title());
        assertEquals("Original Body", updated.body());
        assertTrue(updated.published());
        assertEquals(author.getId(), updated.authorId());
    }

    @Test
    void updateNews_WithNewCategory_ShouldUpdateCategory() {
        User author = createTestUser("author2", "author2@test.com");
        Term originalCategory = createTestTerm("Technology", "category");
        Term newCategory = createTestTerm("Sports", "category");

        NewsCreateRequestDto createDto = new NewsCreateRequestDto();
        createDto.setTitle("Test Article");
        createDto.setBody("Test Body");
        createDto.setAuthorId(author.getId());
        createDto.setCategoryId(originalCategory.getId());
        createDto.setPublicationDate(LocalDateTime.now());
        createDto.setPublished(false);

        NewsDto created = newsService.create(createDto);

        NewsUpdateRequestDto updateDto = new NewsUpdateRequestDto();
        updateDto.setCategoryId(newCategory.getId());

        NewsDto updated = newsService.update(created.id(), updateDto);

        assertEquals(newCategory.getId(), updated.categoryId());
        assertEquals("Test Article", updated.title());
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