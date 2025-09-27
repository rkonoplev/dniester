package com.example.newsplatform.service;

import com.example.newsplatform.dto.request.BulkActionRequestDto;
import com.example.newsplatform.mapper.NewsMapper;
import com.example.newsplatform.repository.NewsRepository;
import com.example.newsplatform.repository.UserRepository;
import com.example.newsplatform.security.RoleConstants;
import com.example.newsplatform.service.impl.NewsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
    private UserRepository userRepository;

    @Mock
    private NewsMapper newsMapper;

    @Mock
    private Authentication authentication;

    private NewsServiceImpl newsService;

    @BeforeEach
    void setUp() {
        newsService = new NewsServiceImpl(newsRepository, userRepository, newsMapper);
    }

    private Authentication createAdminAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                "admin",
                "password",
                List.of(new SimpleGrantedAuthority(RoleConstants.ROLE_ADMIN))
        );
    }

    private Authentication createEditorAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                "editor",
                "password",
                List.of(new SimpleGrantedAuthority(RoleConstants.ROLE_EDITOR))
        );
    }

    private Authentication createUserAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                "user",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void performBulkAction_AdminRole_ShouldAllowBulkDelete() {
        // Given
        Authentication adminAuth = createAdminAuthentication();

        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(true);

        // When & Then - No exception should be thrown for ADMIN
        assertDoesNotThrow(() -> newsService.performBulkAction(request, adminAuth));
        verify(newsRepository).deleteAllById(Arrays.asList(1L, 2L, 3L));
    }

    @Test
    void performBulkAction_AdminRole_ShouldAllowBulkUnpublish() {
        // Given
        Authentication adminAuth = createAdminAuthentication();

        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.UNPUBLISH);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(true);

        // When & Then
        assertDoesNotThrow(() -> newsService.performBulkAction(request, adminAuth));
        verify(newsRepository).unpublishByIds(Arrays.asList(1L, 2L, 3L));
    }

    @Test
    void performBulkAction_EditorRole_ShouldDenyBulkOperations() {
        // Given
        Authentication editorAuth = createEditorAuthentication();

        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(true);

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> newsService.performBulkAction(request, editorAuth));

        assertEquals("Access is denied", exception.getMessage());
        verify(newsRepository, never()).deleteAllById(any());
        verify(newsRepository, never()).unpublishByIds(any());
    }

    @Test
    void performBulkAction_UserRole_ShouldDenyBulkOperations() {
        // Given
        Authentication userAuth = createUserAuthentication();

        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(true);

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> newsService.performBulkAction(request, userAuth));

        assertEquals("Access is denied", exception.getMessage());
        verify(newsRepository, never()).deleteAllById(any());
    }

    @Test
    void performBulkAction_UnconfirmedRequest_ShouldThrowException() {
        // Given
        Authentication adminAuth = createAdminAuthentication();

        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(false); // Not confirmed

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> newsService.performBulkAction(request, adminAuth));

        assertEquals("Bulk operation must be confirmed", exception.getMessage());
        verify(newsRepository, never()).deleteAllById(any());
    }

    @Test
    void performBulkAction_EmptyTargetIds_ShouldNotCallRepository() {
        // Given
        Authentication adminAuth = createAdminAuthentication();

        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>()); // Empty set
        request.setConfirmed(true);

        // When
        newsService.performBulkAction(request, adminAuth);

        // Then
        verify(newsRepository, never()).deleteAllById(any());
        verify(newsRepository, never()).unpublishByIds(any());
    }
}