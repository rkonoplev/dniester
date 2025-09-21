package com.example.newsplatform.dto.request;

import com.example.newsplatform.entity.News;
import com.example.newsplatform.repository.NewsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class NewsCreateRequestDtoIntegrationTest {

    @Autowired
    private NewsRepository newsRepository;

    @Test
    void createNews_WithValidDto_ShouldCreateNewsSuccessfully() {
        NewsCreateRequestDto dto = new NewsCreateRequestDto();
        dto.setTitle("Integration Test Title");
        dto.setBody("Integration test body content.");

        News news = new News();
        news.setTitle(dto.getTitle());
        news.setBody(dto.getBody());

        News savedNews = newsRepository.save(news);

        assertNotNull(savedNews.getId());
        assertEquals("Integration Test Title", savedNews.getTitle());
    }

    @Test
    void createNews_WithOptionalFields_ShouldCreateNewsWithDefaults() {
        NewsCreateRequestDto dto = new NewsCreateRequestDto();
        dto.setTitle("Another Test Title");

        News news = new News();
        news.setTitle(dto.getTitle());

        News savedNews = newsRepository.save(news);

        assertNotNull(savedNews.getId());
        assertEquals("Another Test Title", savedNews.getTitle());
    }
}
