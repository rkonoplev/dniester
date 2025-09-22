package com.example.newsplatform.service.impl;

import com.example.newsplatform.entity.News;
import com.example.newsplatform.repository.NewsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NewsServiceImplIntegrationTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsServiceImpl newsService;

    @Test
    void testDeleteNews() {
        News news = new News();
        news.setTitle("Test Title for Deletion");
        news = newsRepository.save(news);

        assertTrue(newsRepository.findById(news.getId()).isPresent());

        newsService.delete(news.getId());

        assertFalse(newsRepository.findById(news.getId()).isPresent());
    }
}
