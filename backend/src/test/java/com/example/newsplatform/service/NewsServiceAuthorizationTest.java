package com.example.newsplatform.service;

import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.request.NewsUpdateRequestDto;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.exception.ResourceNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NewsService authorization logic.
 * Tests ADMIN vs EDITOR access control and author-based security.
 */
@ExtendWith(MockitoExtension.class)
class NewsServiceAuthorizationTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NewsMapper newsMapper;

    private NewsServiceImpl newsService;

    private User adminUser;
    private User editorUser;
    private User otherEditorUser;
    private News adminNews;
    private News editorNews;
    private News otherEditorNews;

    @BeforeEach
    void setUp() {
        newsService = new NewsServiceImpl(newsRepository, userRepository, newsMapper);

        // Setup users
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");

        editorUser = new User();
        editorUser.setId(2L);
        editorUser.setUsername("editor");

        otherEditorUser = new User();
        otherEditorUser.setId(3L);
        otherEditorUser.setUsername("other_editor");

        // Setup news articles
        adminNews = new News();
        adminNews.setId(1L);
        adminNews.setAuthor(adminUser);

        editorNews = new News();
        editorNews.setId(2L);
        editorNews.setAuthor(editorUser);

        otherEditorNews = new News();
        otherEditorNews.setId(3L);
        otherEditorNews.setAuthor(otherEditorUser);
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

    private Authentication createOtherEditorAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                "other_editor",
                "password",
                List.of(new SimpleGrantedAuthority(RoleConstants.ROLE_EDITOR))
        );
    }

    @Test
    void findAll_AdminRole_ShouldReturnAllNews() {
        // Given
        Authentication adminAuth = createAdminAuthentication();
        Pageable pageable = Pageable.unpaged();

        List<News> allNews = List.of(adminNews, editorNews, otherEditorNews);
        Page<News> newsPage = new PageImpl<>(allNews);

        when(newsRepository.findAll(pageable)).thenReturn(newsPage);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        // When
        var result = newsService.findAll(pageable, adminAuth);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        verify(newsRepository).findAll(pageable);
        verify(newsRepository, never()).findByAuthorId(any(), any());
    }

    @Test
    void findAll_EditorRole_ShouldReturnOnlyOwnNews() {
        // Given
        Authentication editorAuth = createEditorAuthentication();
        Pageable pageable = Pageable.unpaged();

        List<News> editorNewsList = List.of(editorNews);
        Page<News> newsPage = new PageImpl<>(editorNewsList);

        when(newsRepository.findByAuthorId(2L, pageable)).thenReturn(newsPage);
        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(editorUser));

        // When
        var result = newsService.findAll(pageable, editorAuth);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(newsRepository).findByAuthorId(2L, pageable);
        verify(newsRepository, never()).findAll(any());
    }

    @Test
    void findById_AdminRole_ShouldReturnAnyNews() {
        // Given
        Authentication adminAuth = createAdminAuthentication();

        when(newsRepository.findById(2L)).thenReturn(Optional.of(editorNews));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(newsRepository.existsByIdAndAuthorId(2L, 1L)).thenReturn(false); // Admin is not author
        when(newsMapper.toDto(editorNews)).thenReturn(any()); // Mock DTO conversion

        // When
        var result = newsService.findById(2L, adminAuth);

        // Then - Should not throw exception even though admin is not author
        assertNotNull(result);
        verify(newsRepository).findById(2L);
    }

    @Test
    void findById_EditorRole_ShouldReturnOwnNews() {
        // Given
        Authentication editorAuth = createEditorAuthentication();

        when(newsRepository.findById(2L)).thenReturn(Optional.of(editorNews));
        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(editorUser));
        when(newsRepository.existsByIdAndAuthorId(2L, 2L)).thenReturn(true); // Editor is author
        when(newsMapper.toDto(editorNews)).thenReturn(any());

        // When
        var result = newsService.findById(2L, editorAuth);

        // Then
        assertNotNull(result);
        verify(newsRepository).findById(2L);
    }

    @Test
    void findById_EditorRole_ShouldDenyAccessToOthersNews() {
        // Given
        Authentication editorAuth = createEditorAuthentication();

        when(newsRepository.findById(1L)).thenReturn(Optional.of(adminNews));
        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(editorUser));
        when(newsRepository.existsByIdAndAuthorId(1L, 2L)).thenReturn(false); // Editor is not author

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> newsService.findById(1L, editorAuth));

        assertEquals("Access is denied", exception.getMessage());
    }

    @Test
    void update_EditorRole_ShouldAllowUpdatingOwnNews() {
        // Given
        Authentication editorAuth = createEditorAuthentication();
        NewsUpdateRequestDto updateRequest = new NewsUpdateRequestDto();
        updateRequest.setTitle("Updated Title");

        when(newsRepository.findById(2L)).thenReturn(Optional.of(editorNews));
        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(editorUser));
        when(newsRepository.existsByIdAndAuthorId(2L, 2L)).thenReturn(true);
        when(newsRepository.save(editorNews)).thenReturn(editorNews);
        when(newsMapper.toDto(editorNews)).thenReturn(any());

        // When
        var result = newsService.update(2L, updateRequest, editorAuth);

        // Then - Should not throw exception
        assertNotNull(result);
        verify(newsRepository).save(editorNews);
    }

    @Test
    void update_EditorRole_ShouldDenyUpdatingOthersNews() {
        // Given
        Authentication editorAuth = createEditorAuthentication();
        NewsUpdateRequestDto updateRequest = new NewsUpdateRequestDto();

        when(newsRepository.findById(1L)).thenReturn(Optional.of(adminNews));
        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(editorUser));
        when(newsRepository.existsByIdAndAuthorId(1L, 2L)).thenReturn(false);

        // When & Then
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> newsService.update(1L, updateRequest, editorAuth));

        assertEquals("Access is denied", exception.getMessage());
        verify(newsRepository, never()).save(any());
    }

    @Test
    void delete_EditorRole_ShouldAllowDeletingOwnNews() {
        // Given
        Authentication editorAuth = createEditorAuthentication();

        when(newsRepository.findById(2L)).thenReturn(Optional.of(editorNews));
        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(editorUser));
        when(newsRepository.existsByIdAndAuthorId(2L, 2L)).thenReturn(true);

        // When
        newsService.delete(2L, editorAuth);

        // Then - Should not throw exception
        verify(newsRepository).delete(editorNews);
    }

    @Test
    void isAuthor_ShouldReturnTrueForAuthor() {
        // Given
        Authentication editorAuth = createEditorAuthentication();

        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(editorUser));
        when(newsRepository.existsByIdAndAuthorId(2L, 2L)).thenReturn(true);

        // When
        boolean result = newsService.isAuthor(2L, editorAuth);

        // Then
        assertTrue(result);
    }

    @Test
    void isAuthor_ShouldReturnFalseForNonAuthor() {
        // Given
        Authentication editorAuth = createEditorAuthentication();

        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(editorUser));
        when(newsRepository.existsByIdAndAuthorId(1L, 2L)).thenReturn(false);

        // When
        boolean result = newsService.isAuthor(1L, editorAuth);

        // Then
        assertFalse(result);
    }

    @Test
    void hasAdminRole_ShouldReturnTrueForAdmin() {
        // Given
        Authentication adminAuth = createAdminAuthentication();

        // When
        boolean result = newsService.hasAdminRole(adminAuth);

        // Then
        assertTrue(result);
    }

    @Test
    void hasAdminRole_ShouldReturnFalseForEditor() {
        // Given
        Authentication editorAuth = createEditorAuthentication();

        // When
        boolean result = newsService.hasAdminRole(editorAuth);

        // Then
        assertFalse(result);
    }
}