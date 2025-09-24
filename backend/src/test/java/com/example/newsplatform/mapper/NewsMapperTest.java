package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.Term;
import com.example.newsplatform.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NewsMapperTest {

    private final NewsMapper newsMapper = Mappers.getMapper(NewsMapper.class);

    @Test
    void shouldMapNewsToNewsDto() {
        // Given
        User author = new User();
        author.setId(1L);
        author.setUsername("test_author");

        Term term = new Term();
        term.setId(10L);
        term.setName("Technology");

        News news = new News();
        news.setId(1L);
        news.setTitle("Test Title");
        news.setContent("Test Content");
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
        assertEquals(news.getContent(), dto.getContent());
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

        // When
        News entity = newsMapper.toEntity(req);

        // Then
        assertNotNull(entity);
        assertEquals(req.getTitle(), entity.getTitle());
        assertEquals(req.getContent(), entity.getContent());
    }
}