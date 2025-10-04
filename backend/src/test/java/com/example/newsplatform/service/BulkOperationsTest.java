package com.example.newsplatform.service;

import com.example.newsplatform.dto.request.BulkActionRequestDto;
import com.example.newsplatform.mapper.NewsMapper;
import com.example.newsplatform.repository.NewsRepository;
import com.example.newsplatform.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BulkOperationsTest {

    @Mock
    private NewsRepository newsRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NewsMapper newsMapper;
    @Mock
    private Authentication authentication;

    private NewsServiceImpl newsService;

    @BeforeEach
    void setUp() {
        newsService = spy(new NewsServiceImpl(newsRepository, userRepository, newsMapper));
    }

    @Test
    void performBulkActionAdminRoleShouldAllowBulkDelete() {
        // Given
        doReturn(true).when(newsService).hasRole(authentication, "ADMIN");
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(true);

        // When & Then
        assertDoesNotThrow(() -> newsService.performBulkAction(request, authentication));
        verify(newsRepository).deleteAllById(List.of(1L, 2L, 3L));
    }

    @Test
    void performBulkActionAdminRoleShouldAllowBulkUnpublish() {
        // Given
        doReturn(true).when(newsService).hasRole(authentication, "ADMIN");
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.UNPUBLISH);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(true);

        // When & Then
        assertDoesNotThrow(() -> newsService.performBulkAction(request, authentication));
        verify(newsRepository).unpublishByIds(List.of(1L, 2L, 3L));
    }

    @Test
    void performBulkActionEditorRoleShouldDenyBulkOperations() {
        // Given
        doReturn(false).when(newsService).hasRole(authentication, "ADMIN");
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(true);

        // When & Then
        assertThrows(AccessDeniedException.class, () -> newsService.performBulkAction(request, authentication));
        verify(newsRepository, never()).deleteAllById(any());
    }

    @Test
    void performBulkActionUserRoleShouldDenyBulkOperations() {
        // Given
        doReturn(false).when(newsService).hasRole(authentication, "ADMIN");
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.UNPUBLISH);
        request.setFilterType(BulkActionRequestDto.FilterType.ALL);
        request.setConfirmed(true);

        // When & Then
        assertThrows(AccessDeniedException.class, () -> newsService.performBulkAction(request, authentication));
        verify(newsRepository, never()).unpublishByIds(any());
    }

    @Test
    void performBulkActionUnconfirmedRequestShouldThrowException() {
        // Given
        doReturn(true).when(newsService).hasRole(authentication, "ADMIN");
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setConfirmed(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> newsService.performBulkAction(request, authentication));
    }
}