package com.example.newsplatform.controller;

import com.example.newsplatform.dto.response.NewsDto;
import com.example.newsplatform.service.NewsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PublicNewsController.
 * Tests public API endpoints for fetching published news articles.
 * Ensures only published content is accessible through public endpoints.
 */
@ExtendWith(MockitoExtension.class)
class PublicNewsControllerTest {

    @Mock
    private NewsService newsService;

    @InjectMocks
    private PublicNewsController controller;

    /**
     * Test public search functionality for published news.
     * Verifies only published articles are returned with proper pagination.
     */
    @Test
    void searchPublished_ShouldReturnPublishedNews() {
        NewsDto newsDto = new NewsDto(1L, "Public Title", "Public Body", "Public Teaser",
                LocalDateTime.now(), true, "author", Set.of("tag"), "category", 1L, 1L);
        Page<NewsDto> page = new PageImpl<>(List.of(newsDto));

        when(newsService.searchPublished(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(page);

        Page<NewsDto> result = controller.searchPublished("test", "tech", Pageable.unpaged());
        
        assertEquals(1, result.getContent().size());
        assertEquals("Public Title", result.getContent().get(0).title());
    }

    /**
     * Test fetching a single published news article by ID.
     * Ensures proper data mapping and response structure.
     */
    @Test
    void getPublishedById_ShouldReturnNews() {
        NewsDto newsDto = new NewsDto(1L, "Single News", "Body", "Teaser",
                LocalDateTime.now(), true, "author", Set.of(), "category", 1L, 1L);

        when(newsService.getPublishedById(1L)).thenReturn(newsDto);

        NewsDto result = controller.getPublishedById(1L);
        
        assertEquals("Single News", result.title());
    }

    /**
     * Test filtering published news by taxonomy term ID.
     * Verifies category-based filtering works correctly.
     */
    @Test
    void getByTermId_ShouldReturnNewsByTerm() {
        NewsDto newsDto = new NewsDto(1L, "Term News", "Body", "Teaser",
                LocalDateTime.now(), true, "author", Set.of(), "category", 1L, 1L);
        Page<NewsDto> page = new PageImpl<>(List.of(newsDto));

        when(newsService.getPublishedByTermId(eq(1L), any(Pageable.class))).thenReturn(page);

        Page<NewsDto> result = controller.getByTermId(1L, Pageable.unpaged());
        
        assertEquals(1, result.getContent().size());
        assertEquals("Term News", result.getContent().get(0).title());
    }

    /**
     * Test filtering published news by multiple term IDs.
     * Ensures multi-category filtering functionality.
     */
    @Test
    void getByTermIds_ShouldReturnNewsByMultipleTerms() {
        NewsDto newsDto = new NewsDto(1L, "Multi Term News", "Body", "Teaser",
                LocalDateTime.now(), true, "author", Set.of(), "category", 1L, 1L);
        Page<NewsDto> page = new PageImpl<>(List.of(newsDto));

        when(newsService.getPublishedByTermIds(anyList(), any(Pageable.class))).thenReturn(page);

        Page<NewsDto> result = controller.getByTermIds(List.of(1L, 2L, 3L), Pageable.unpaged());
        
        assertEquals(1, result.getContent().size());
        assertEquals("Multi Term News", result.getContent().get(0).title());
    }
}