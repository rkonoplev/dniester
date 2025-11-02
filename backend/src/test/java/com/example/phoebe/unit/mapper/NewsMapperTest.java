package com.example.phoebe.mapper;

import com.example.phoebe.dto.request.NewsCreateRequestDto;
import com.example.phoebe.dto.response.NewsDto;
import com.example.phoebe.entity.News;
import com.example.phoebe.entity.Term;
import com.example.phoebe.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link NewsMapper}.
 * Verifies that the mapper correctly converts between News entities and their corresponding DTOs.
 * These tests do not require a Spring context.
 */
class NewsMapperTest {

    private final NewsMapper newsMapper = Mappers.getMapper(NewsMapper.class);

    @Test
    void shouldMapNewsToNewsDto() {
        // Given
        User author = new User("test_author", "pass", "email", true);
        author.setId(1L);

        Term term = new Term("Technology", "category");
        term.setId(10L);

        News news = new News();
        news.setId(1L);
        news.setTitle("Test Title");
        news.setBody("Test Content");
        news.setTeaser("Test Teaser");
        news.setPublished(true);
        news.setPublicationDate(LocalDateTime.now());
        news.setAuthor(author);
        news.setTerms(Set.of(term));

        // When
        NewsDto dto = newsMapper.toDto(news);

        // Then
        assertNotNull(dto);
        assertEquals(news.getId(), dto.getId());
        assertEquals(news.getTitle(), dto.getTitle());
        assertEquals(news.getBody(), dto.getBody());
        assertEquals(news.getTeaser(), dto.getTeaser());
        assertEquals(news.isPublished(), dto.isPublished());
        assertEquals(author.getId(), dto.getAuthorId());
        assertEquals(author.getUsername(), dto.getAuthorName());
        assertEquals(1, dto.getTermNames().size());
        assertEquals("Technology", dto.getTermNames().iterator().next());
    }

    @Test
    void shouldMapNewsCreateRequestDtoToNews() {
        // Given
        NewsCreateRequestDto req = new NewsCreateRequestDto();
        req.setTitle("New Article");
        req.setContent("Article content.");
        req.setTeaser("Article teaser.");

        // When
        News entity = newsMapper.toEntity(req);

        // Then
        assertNotNull(entity);
        assertEquals(req.getTitle(), entity.getTitle());
        assertEquals(req.getContent(), entity.getBody());
        assertEquals(req.getTeaser(), entity.getTeaser());
    }
}
