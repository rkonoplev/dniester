package com.example.newsplatform.dto.request;

import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.Term;
import com.example.newsplatform.repository.NewsRepository;
import com.example.newsplatform.repository.TermRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class NewsUpdateRequestDtoIntegrationTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private TermRepository termRepository;

    @Test
    void updateNews_WithPartialDto_ShouldUpdateOnlyProvidedFields() {
        News news = new News();
        news.setTitle("Original Title");
        news.setBody("Original body.");
        News savedNews = newsRepository.save(news);

        NewsUpdateRequestDto updateDto = new NewsUpdateRequestDto();
        updateDto.setTitle("Updated Title");

        savedNews.setTitle(updateDto.getTitle());
        News updatedNews = newsRepository.save(savedNews);

        assertEquals("Updated Title", updatedNews.getTitle());
        assertEquals("Original body.", updatedNews.getBody());
    }

    @Test
    void updateNews_WithNewCategory_ShouldUpdateCategory() {
        News news = new News();
        news.setTitle("Tech News");
        News savedNews = newsRepository.save(news);

        Term newCategory = new Term();
        newCategory.setName("Technology");
        termRepository.save(newCategory);

        NewsUpdateRequestDto updateDto = new NewsUpdateRequestDto();
        updateDto.setTermIds(Set.of(newCategory.getId()));

        savedNews.setTerms(Set.of(newCategory));
        News updatedNews = newsRepository.save(savedNews);

        assertTrue(updatedNews.getTerms().stream().anyMatch(t -> t.getName().equals("Technology")));
    }
}
