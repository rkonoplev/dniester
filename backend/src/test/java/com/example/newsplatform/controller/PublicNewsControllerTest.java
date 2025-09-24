package com.example.newsplatform.controller;

import com.example.newsplatform.dto.response.NewsDto;
import com.example.newsplatform.service.NewsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicNewsControllerTest {

    @Mock
    private NewsService newsService;

    @InjectMocks
    private PublicNewsController controller;

    @Test
    void searchPublished_ShouldReturnPageOfNews() {
        // Given
        NewsDto newsDto = new NewsDto(1L, "Public Title", "Public Content", true, LocalDateTime.now(), 1L, "author", Collections.emptySet());
        Page<NewsDto> page = new PageImpl<>(List.of(newsDto));
        when(newsService.searchPublished(eq("test"), eq("cat"), any(Pageable.class))).thenReturn(page);

        // When
        Page<NewsDto> result = controller.searchPublished("test", "cat", PageRequest.of(0, 10));

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("Public Title", result.getContent().get(0).getTitle());
    }

    @Test
    void getPublishedById_ShouldReturnSingleNews() {
        // Given
        NewsDto newsDto = new NewsDto(1L, "Single News", "Content", true, LocalDateTime.now(), 1L, "author", Collections.emptySet());
        when(newsService.getPublishedById(1L)).thenReturn(newsDto);

        // When
        NewsDto result = controller.getPublishedById(1L);

        // Then
        assertEquals("Single News", result.getTitle());
    }

    @Test
    void getByTermId_ShouldReturnPageOfNews() {
        // Given
        NewsDto newsDto = new NewsDto(1L, "Term News", "Content", true, LocalDateTime.now(), 1L, "author", Collections.emptySet());
        Page<NewsDto> page = new PageImpl<>(List.of(newsDto));
        when(newsService.getPublishedByTermId(eq(5L), any(Pageable.class))).thenReturn(page);

        // When
        Page<NewsDto> result = controller.getByTermId(5L, PageRequest.of(0, 10));

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("Term News", result.getContent().get(0).getTitle());
    }

    @Test
    void getByTermIds_ShouldReturnPageOfNews() {
        // Given
        NewsDto newsDto = new NewsDto(1L, "Multi Term News", "Content", true, LocalDateTime.now(), 1L, "author", Collections.emptySet());
        Page<NewsDto> page = new PageImpl<>(List.of(newsDto));
        List<Long> termIds = List.of(5L, 6L);
        when(newsService.getPublishedByTermIds(eq(termIds), any(Pageable.class))).thenReturn(page);

        // When
        Page<NewsDto> result = controller.getByTermIds(termIds, PageRequest.of(0, 10));

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("Multi Term News", result.getContent().get(0).getTitle());
    }
}