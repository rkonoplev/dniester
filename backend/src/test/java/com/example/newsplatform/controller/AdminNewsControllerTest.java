package com.example.newsplatform.controller;

import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.request.NewsUpdateRequestDto;
import com.example.newsplatform.dto.request.BulkActionRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import com.example.newsplatform.service.NewsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AdminNewsController.
 * Tests all CRUD operations and bulk actions for news management.
 * Uses Mockito for service layer mocking to isolate controller logic.
 */
@ExtendWith(MockitoExtension.class)
class AdminNewsControllerTest {

    @Mock
    private NewsService newsService;

    @InjectMocks
    private AdminNewsController controller;

    /**
     * Test that searchAll endpoint returns paginated news results.
     * Verifies controller properly delegates to service and returns expected data.
     */
    @Test
    void searchAll_ShouldReturnNews() {
        NewsDto newsDto = new NewsDto(1L, "Test Title", "Test Body", "Test Teaser", 
                LocalDateTime.now(), true, "author", Set.of("tag"), "category", 1L, 1L);
        Page<NewsDto> page = new PageImpl<>(List.of(newsDto));
        
        when(newsService.searchAll(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(page);

        Page<NewsDto> result = controller.searchAll("test", "tech", Pageable.unpaged());
        
        assertEquals(1, result.getContent().size());
        assertEquals("Test Title", result.getContent().get(0).title());
    }

    /**
     * Test news creation with valid input data.
     * Verifies HTTP 201 status and proper response body mapping.
     */
    @Test
    void create_WithValidDto_ShouldReturn201() {
        NewsDto inputDto = new NewsDto(null, "New Title", "New Body", "New Teaser",
                LocalDateTime.now(), true, null, Set.of(), null, 1L, 1L);
        NewsDto createdDto = new NewsDto(1L, "New Title", "New Body", "New Teaser",
                LocalDateTime.now(), true, "author", Set.of(), "category", 1L, 1L);

        when(newsService.create(any(NewsCreateRequestDto.class))).thenReturn(createdDto);

        ResponseEntity<NewsDto> response = controller.create(inputDto);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New Title", response.getBody().title());
    }

    /**
     * Test news update operation with valid data.
     * Ensures proper HTTP 200 response and updated content.
     */
    @Test
    void update_WithValidDto_ShouldReturn200() {
        NewsDto updateDto = new NewsDto(1L, "Updated Title", "Updated Body", null,
                LocalDateTime.now(), true, null, Set.of(), null, 1L, 1L);
        Authentication auth = mock(Authentication.class);

        when(newsService.update(eq(1L), any(NewsUpdateRequestDto.class))).thenReturn(updateDto);

        ResponseEntity<NewsDto> response = controller.update(1L, updateDto, auth);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Title", response.getBody().title());
    }

    /**
     * Test news deletion operation.
     * Verifies HTTP 204 No Content response for successful deletion.
     */
    @Test
    void delete_ShouldReturn204() {
        Authentication auth = mock(Authentication.class);
        doNothing().when(newsService).delete(1L);

        ResponseEntity<Void> response = controller.delete(1L, auth);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    /**
     * Test bulk operations (delete/unpublish) on multiple news items.
     * Verifies proper delegation to service layer and HTTP 204 response.
     */
    @Test
    void performBulkAction_ShouldReturn204() {
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(Set.of(1L, 2L));
        request.setConfirmed(true);
        Authentication auth = mock(Authentication.class);

        doNothing().when(newsService).performBulkAction(any(BulkActionRequestDto.class), any());

        ResponseEntity<Void> response = controller.performBulkAction(request, auth);
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}