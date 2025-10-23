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