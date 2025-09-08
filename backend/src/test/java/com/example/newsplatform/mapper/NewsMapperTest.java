package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.NewsCreateRequestDto;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequestDto;
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

        assertEquals(1L, dto.id());
        assertEquals("Test title", dto.title());
        assertEquals("Body", dto.body());
        assertEquals("Teaser", dto.teaser());
        assertEquals(42L, dto.authorId());
        assertEquals(99L, dto.categoryId());
        assertEquals("Category", dto.category());
        assertTrue(dto.published());
    }

    @Test
    void testDtoToCreateRequest() {
        LocalDateTime now = LocalDateTime.now();
        NewsDto dto = new NewsDto(
                null, "Sample", "Body", "Teaser", now, true,
                null, Set.of(), null, 20L, 10L
        );

        NewsCreateRequestDto req = NewsMapper.newsDtoToCreateRequest(dto);

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
        NewsDto dto = new NewsDto(
                null, "Updated", "New body", null, null, false,
                null, Set.of(), null, null, null
        );

        NewsUpdateRequestDto req = NewsMapper.newsDtoToUpdateRequest(dto);

        assertEquals("Updated", req.getTitle());
        assertEquals("New body", req.getBody());
    }

    @Test
    void testFromCreateRequest() {
        NewsCreateRequestDto req = new NewsCreateRequestDto();
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
        NewsUpdateRequestDto req = new NewsUpdateRequestDto();
        req.setTitle("NewTitle");
        req.setBody("NewBody");
        req.setPublished(true);

        NewsMapper.updateEntity(news, req);

        assertEquals("NewTitle", news.getTitle());
        assertEquals("NewBody", news.getBody());
        assertTrue(news.isPublished());
    }
}
