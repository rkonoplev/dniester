package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequest;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.Term;
import com.example.newsplatform.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NewsMapperTest {

    @Test
    void testEntityToDto() {
        News news = new News();
        news.setId(1L);
        news.setTitle("Test title");
        news.setBody("Body");
        news.setTeaser("Teaser");
        news.setPublicationDate(LocalDateTime.of(2025, 1, 1, 12, 0));
        news.setPublished(true);

        User author = new User();
        author.setId(42L);
        news.setAuthor(author);

        Term term = new Term();
        term.setId(99L);
        term.setName("Category");
        news.setTerms(Set.of(term));

        NewsDto dto = NewsMapper.toDto(news);

        assertEquals(1L, dto.getId());
        assertEquals("Test title", dto.getTitle());
        assertEquals("Body", dto.getBody());
        assertEquals("Teaser", dto.getTeaser());
        assertEquals(42L, dto.getAuthorId());
        assertEquals(99L, dto.getCategoryId());
        assertEquals("Category", dto.getCategory());
        assertTrue(dto.isPublished());
    }

    @Test
    void testDtoToCreateRequest() {
        NewsDto dto = new NewsDto();
        dto.setTitle("Sample");
        dto.setBody("Body");
        dto.setTeaser("Teaser");
        dto.setAuthorId(10L);
        dto.setCategoryId(20L);
        dto.setPublicationDate(LocalDateTime.now());
        dto.setPublished(true);

        NewsCreateRequest req = NewsMapper.newsDtoToCreateRequest(dto);

        assertNotNull(req);
        assertEquals("Sample", req.getTitle());
        assertEquals("Body", req.getBody());
        assertEquals("Teaser", req.getTeaser());
        assertEquals(10L, req.getAuthorId());
        assertEquals(20L, req.getCategoryId());
        assertTrue(req.isPublished());
    }

    @Test
    void testDtoToUpdateRequest() {
        NewsDto dto = new NewsDto();
        dto.setTitle("Updated");
        dto.setBody("New body");

        NewsUpdateRequest req = NewsMapper.newsDtoToUpdateRequest(dto);

        assertEquals("Updated", req.getTitle());
        assertEquals("New body", req.getBody());
    }

    @Test
    void testFromCreateRequest() {
        NewsCreateRequest req = new NewsCreateRequest();
        req.setTitle("Created");
        req.setBody("Body");
        req.setTeaser("Teaser");
        req.setPublicationDate(LocalDateTime.now());
        req.setPublished(true);

        News entity = NewsMapper.fromCreateRequest(req);

        assertEquals("Created", entity.getTitle());
        assertEquals("Body", entity.getBody());
        assertEquals("Teaser", entity.getTeaser());
        assertTrue(entity.isPublished());
    }

    @Test
    void testUpdateEntity() {
        News news = new News();
        NewsUpdateRequest req = new NewsUpdateRequest();
        req.setTitle("NewTitle");
        req.setBody("NewBody");
        req.setPublished(true);

        NewsMapper.updateEntity(news, req);

        assertEquals("NewTitle", news.getTitle());
        assertEquals("NewBody", news.getBody());
        assertTrue(news.isPublished());
    }
}
