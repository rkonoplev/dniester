package com.example.newsplatform.service;

import com.example.newsplatform.dto.BulkActionRequestDto;
import com.example.newsplatform.repository.NewsRepository;
import com.example.newsplatform.service.impl.NewsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for bulk operations functionality.
 * Tests role-based access control and bulk operation restrictions.
 */
@ExtendWith(MockitoExtension.class)
class BulkOperationsTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private Authentication authentication;

    private NewsServiceImpl newsService;

    @BeforeEach
    void setUp() {
        newsService = spy(new NewsServiceImpl(newsRepository, null, null));
    }

    @Test
    void performBulkAction_AdminRole_ShouldAllowBulkDelete() {
        // Given
        doReturn(true).when(newsService).hasRoleId(authentication, 1L); // ADMIN role
        
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(true);

        // When & Then
        assertDoesNotThrow(() -> newsService.performBulkAction(request, authentication));
        verify(newsRepository).deleteAllById(Arrays.asList(1L, 2L, 3L));
    }

    @Test
    void performBulkAction_AdminRole_ShouldAllowBulkUnpublish() {
        // Given
        doReturn(true).when(newsService).hasRoleId(authentication, 1L); // ADMIN role
        
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.UNPUBLISH);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(true);

        // When & Then
        assertDoesNotThrow(() -> newsService.performBulkAction(request, authentication));
        verify(newsRepository).unpublishByIds(Arrays.asList(1L, 2L, 3L));
    }

    @Test
    void performBulkAction_EditorRole_ShouldDenyBulkOperations() {
        // Given
        doReturn(false).when(newsService).hasRoleId(authentication, 1L); // Not ADMIN
        
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(true);

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, 
            () -> newsService.performBulkAction(request, authentication));
        
        assertEquals("Bulk operations are restricted to ADMIN role only. EDITOR can only delete single articles.", 
            exception.getMessage());
        
        verify(newsRepository, never()).deleteAllById(any());
        verify(newsRepository, never()).unpublishByIds(any());
    }

    @Test
    void performBulkAction_UnconfirmedRequest_ShouldThrowException() {
        // Given
        doReturn(true).when(newsService).hasRoleId(authentication, 1L); // ADMIN role
        
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(false); // Not confirmed

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> newsService.performBulkAction(request, authentication));
        
        assertEquals("Bulk operation must be confirmed", exception.getMessage());
        verify(newsRepository, never()).deleteAllById(any());
    }

    @Test
    void performBulkAction_ByTermFilter_ShouldCallCorrectRepository() {
        // Given
        doReturn(true).when(newsService).hasRoleId(authentication, 1L); // ADMIN role
        when(newsRepository.findIdsByTermId(5L)).thenReturn(Arrays.asList(1L, 2L, 3L));
        
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_TERM);
        request.setTermId(5L);
        request.setConfirmed(true);

        // When
        newsService.performBulkAction(request, authentication);

        // Then
        verify(newsRepository).findIdsByTermId(5L);
        verify(newsRepository).deleteAllById(Arrays.asList(1L, 2L, 3L));
    }

    @Test
    void performBulkAction_ByAuthorFilter_ShouldCallCorrectRepository() {
        // Given
        doReturn(true).when(newsService).hasRoleId(authentication, 1L); // ADMIN role
        when(newsRepository.findIdsByAuthorId(10L)).thenReturn(Arrays.asList(4L, 5L));
        
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.UNPUBLISH);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_AUTHOR);
        request.setAuthorId(10L);
        request.setConfirmed(true);

        // When
        newsService.performBulkAction(request, authentication);

        // Then
        verify(newsRepository).findIdsByAuthorId(10L);
        verify(newsRepository).unpublishByIds(Arrays.asList(4L, 5L));
    }

    @Test
    void performBulkAction_AllFilter_ShouldCallCorrectRepository() {
        // Given
        doReturn(true).when(newsService).hasRoleId(authentication, 1L); // ADMIN role
        when(newsRepository.findAllIds()).thenReturn(Arrays.asList(1L, 2L, 3L, 4L, 5L));
        
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.ALL);
        request.setConfirmed(true);

        // When
        newsService.performBulkAction(request, authentication);

        // Then
        verify(newsRepository).findAllIds();
        verify(newsRepository).deleteAllById(Arrays.asList(1L, 2L, 3L, 4L, 5L));
    }

    @Test
    void performBulkAction_EmptyTargetIds_ShouldNotCallRepository() {
        // Given
        doReturn(true).when(newsService).hasRoleId(authentication, 1L); // ADMIN role
        
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>()); // Empty set
        request.setConfirmed(true);

        // When
        newsService.performBulkAction(request, authentication);

        // Then
        verify(newsRepository, never()).deleteAllById(any());
        verify(newsRepository, never()).unpublishByIds(any());
    }

    @Test
    void performBulkAction_UnsupportedAction_ShouldThrowException() {
        // Given
        doReturn(true).when(newsService).hasRoleId(authentication, 1L); // ADMIN role
        
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(null); // Unsupported action
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L)));
        request.setConfirmed(true);

        // When & Then
        Exception exception = assertThrows(Exception.class, 
            () -> newsService.performBulkAction(request, authentication));
        
        assertTrue(exception.getMessage().contains("Unsupported bulk action") || 
                  exception instanceof NullPointerException);
    }
}