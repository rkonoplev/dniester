package com.example.phoebe.service;

import com.example.phoebe.dto.request.BulkActionRequestDto;
import com.example.phoebe.mapper.NewsMapper;
import com.example.phoebe.repository.NewsRepository;
import com.example.phoebe.repository.UserRepository;
import com.example.phoebe.service.impl.NewsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for NewsServiceImpl bulk operations logic.
 * Uses fake Authentication objects instead of Mockito 'when' stubbing.
 */
@ExtendWith(MockitoExtension.class)
class BulkOperationsTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NewsMapper newsMapper;

    @InjectMocks
    private NewsServiceImpl newsService;

    @Captor
    private ArgumentCaptor<List<Long>> idListCaptor;

    // Helper method to create a fake Authentication with a given role
    private Authentication mockAuthWithRole(String role) {
        return new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority(role));
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) {
                // This method is intentionally left empty for this mock.
            }

            @Override
            public String getName() {
                return "testUser";
            }
        };
    }

    /**
     * Tests that an admin can perform a bulk DELETE operation successfully.
     */
    @Test
    void performBulkActionAdminRoleShouldAllowBulkDelete() {
        Authentication adminAuth = mockAuthWithRole("ROLE_ADMIN");

        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(true);

        assertDoesNotThrow(() -> {
            newsService.performBulkAction(request, adminAuth);
        });

        verify(newsRepository).deleteAllByIdInBatch(idListCaptor.capture());
        List<Long> capturedIds = idListCaptor.getValue();
        assertEquals(3, capturedIds.size());
        assertTrue(capturedIds.containsAll(List.of(1L, 2L, 3L)));
    }

    /**
     * Tests that an admin can perform a bulk UNPUBLISH operation successfully.
     */
    @Test
    void performBulkActionAdminRoleShouldAllowBulkUnpublish() {
        Authentication adminAuth = mockAuthWithRole("ROLE_ADMIN");

        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.UNPUBLISH);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(true);

        assertDoesNotThrow(() -> {
            newsService.performBulkAction(request, adminAuth);
        });

        verify(newsRepository).unpublishByIds(idListCaptor.capture());
        List<Long> capturedIds = idListCaptor.getValue();
        assertEquals(3, capturedIds.size());
        assertTrue(capturedIds.containsAll(List.of(1L, 2L, 3L)));
    }

    /**
     * Tests that an editor (non-admin) is denied access to bulk operations.
     */
    @Test
    void performBulkActionEditorRoleShouldDenyBulkOperations() {
        Authentication editorAuth = mockAuthWithRole("ROLE_EDITOR");

        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setFilterType(BulkActionRequestDto.FilterType.BY_IDS);
        request.setItemIds(new HashSet<>(Arrays.asList(1L, 2L, 3L)));
        request.setConfirmed(true);

        assertThrows(AccessDeniedException.class, () -> {
            newsService.performBulkAction(request, editorAuth);
        });

        verify(newsRepository, never()).deleteAllByIdInBatch(anyList());
        verify(newsRepository, never()).unpublishByIds(anyList());
    }

    /**
     * Tests that unauthenticated users cannot perform bulk operations.
     */
    @Test
    void performBulkActionUserRoleShouldDenyBulkOperations() {
        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.UNPUBLISH);
        request.setFilterType(BulkActionRequestDto.FilterType.ALL);
        request.setConfirmed(true);

        assertThrows(AccessDeniedException.class, () -> {
            newsService.performBulkAction(request, null);
        });

        verify(newsRepository, never()).unpublishByIds(anyList());
    }

    /**
     * Tests that an unconfirmed request (confirmed = false) throws exception.
     */
    @Test
    void performBulkActionUnconfirmedRequestShouldThrowException() {
        Authentication adminAuth = mockAuthWithRole("ROLE_ADMIN");

        BulkActionRequestDto request = new BulkActionRequestDto();
        request.setAction(BulkActionRequestDto.ActionType.DELETE);
        request.setConfirmed(false);

        assertThrows(IllegalArgumentException.class, () -> {
            newsService.performBulkAction(request, adminAuth);
        });

        verify(newsRepository, never()).deleteAllByIdInBatch(anyList());
    }
}