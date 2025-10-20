package com.example.phoebe.service;

import com.example.phoebe.dto.request.BulkActionRequestDto;
import com.example.phoebe.mapper.NewsMapper;
import com.example.phoebe.repository.NewsRepository;
import com.example.phoebe.repository.UserRepository;
import com.example.phoebe.service.impl.NewsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private NewsServiceImpl newsService;

    @Test
    void performBulkActionAdminRoleShouldAllowBulkDelete() {
        // Given
        when(authorizationService.isAdmin(authentication)).thenReturn(true);
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(true);

        // When & Then
        assertDoesNotThrow(() -> newsService.performBulkAction(request, authentication));
        // Verify that the optimized batch method is called
        verify(newsRepository).deleteAllByIdInBatch(List.of(1L, 2L, 3L));
    }

    @Test
    void performBulkActionAdminRoleShouldAllowBulkUnpublish() {
        // Given
        when(authorizationService.isAdmin(authentication)).thenReturn(true);
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
        when(authorizationService.isAdmin(authentication)).thenReturn(false);
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(true);

        // When & Then
        assertThrows(AccessDeniedException.class, () -> newsService.performBulkAction(request, authentication));
        // Verify that no batch delete operation was ever called for any list
        verify(newsRepository, never()).deleteAllByIdInBatch(anyList());
    }

    @Test
    void performBulkActionUserRoleShouldDenyBulkOperations() {
        // Given
        when(authorizationService.isAdmin(null)).thenReturn(false);
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.UNPUBLISH);
        request.setFilterType(BulkActionRequestDto.FilterType.ALL);
        request.setConfirmed(true);

        // When & Then
        assertThrows(AccessDeniedException.class, () -> newsService.performBulkAction(request, null));
        verify(newsRepository, never()).unpublishByIds(anyList());
    }

    @Test
    void performBulkActionUnconfirmedRequestShouldThrowException() {
        // Given
        // No need to stub isAdmin, as the confirmation check should happen first.
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setConfirmed(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> newsService.performBulkAction(request, authentication));
        verify(newsRepository, never()).deleteAllByIdInBatch(anyList());
    }
}