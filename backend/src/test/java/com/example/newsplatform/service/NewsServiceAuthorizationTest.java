package com.example.newsplatform.service;

import com.example.newsplatform.dto.request.NewsUpdateRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.mapper.NewsMapper;
import com.example.newsplatform.repository.NewsRepository;
import com.example.newsplatform.repository.UserRepository;
import com.example.newsplatform.service.impl.NewsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceAuthorizationTest {

    @Mock
    private NewsRepository newsRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NewsMapper newsMapper;

    @Spy
    @InjectMocks
    private NewsServiceImpl newsService;

    private User adminUser;
    private User editorUser;
    private User anotherUser;
    private Authentication adminAuth;
    private Authentication editorAuth;

    private News ownNews;
    private News othersNews;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");

        editorUser = new User();
        editorUser.setId(2L);
        editorUser.setUsername("editor");

        anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setUsername("another");

        adminAuth = new UsernamePasswordAuthenticationToken("admin", "pass",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        editorAuth = new UsernamePasswordAuthenticationToken("editor", "pass",
                List.of(new SimpleGrantedAuthority("ROLE_EDITOR"))
        );

        ownNews = new News();
        ownNews.setId(10L);
        ownNews.setAuthor(editorUser);

        othersNews = new News();
        othersNews.setId(11L);
        othersNews.setAuthor(anotherUser);
    }

    @Test
    void hasAdminRole_ShouldReturnTrueForAdmin() {
        assertTrue(newsService.hasAdminRole(adminAuth));
    }

    @Test
    void hasAdminRole_ShouldReturnFalseForEditor() {
        assertFalse(newsService.hasAdminRole(editorAuth));
    }

    @Test
    void isAuthor_ShouldReturnTrueForAuthor() {
        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(editorUser));
        when(newsRepository.existsByIdAndAuthorId(10L, 2L)).thenReturn(true);
        assertTrue(newsService.isAuthor(10L, editorAuth));
    }

    @Test
    void isAuthor_ShouldReturnFalseForNonAuthor() {
        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(editorUser));
        when(newsRepository.existsByIdAndAuthorId(11L, 2L)).thenReturn(false);
        assertFalse(newsService.isAuthor(11L, editorAuth));
    }

    @Test
    void findAllForUser_AdminRole_ShouldReturnAllNews() {
        when(newsRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(ownNews, othersNews)));
        Page<NewsDto> result = newsService.findAllForUser(Pageable.unpaged(), adminAuth);
        assertEquals(2, result.getTotalElements());
        verify(newsRepository, never()).findByAuthorId(any(), any());
    }

    @Test
    void findAllForUser_EditorRole_ShouldReturnOnlyOwnNews() {
        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(editorUser));
        when(newsRepository.findByAuthorId(2L, Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(ownNews)));
        Page<NewsDto> result = newsService.findAllForUser(Pageable.unpaged(), editorAuth);
        assertEquals(1, result.getTotalElements());
        verify(newsRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void findById_AdminRole_ShouldReturnAnyNews() {
        when(newsRepository.findById(11L)).thenReturn(Optional.of(othersNews));
        assertDoesNotThrow(() -> newsService.findById(11L, adminAuth));
    }

    @Test
    void findById_EditorRole_ShouldReturnOwnNews() {
        doReturn(true).when(newsService).isAuthor(10L, editorAuth);
        when(newsRepository.findById(10L)).thenReturn(Optional.of(ownNews));
        assertDoesNotThrow(() -> newsService.findById(10L, editorAuth));
    }

    @Test
    void findById_EditorRole_ShouldDenyAccessToOthersNews() {
        doReturn(false).when(newsService).isAuthor(11L, editorAuth);
        when(newsRepository.findById(11L)).thenReturn(Optional.of(othersNews));
        assertThrows(AccessDeniedException.class, () -> newsService.findById(11L, editorAuth));
    }

    @Test
    void update_EditorRole_ShouldAllowUpdatingOwnNews() {
        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(editorUser));
        when(newsRepository.findById(10L)).thenReturn(Optional.of(ownNews));
        NewsUpdateRequestDto request = new NewsUpdateRequestDto("T", "C", "T", true);
        assertDoesNotThrow(() -> newsService.update(10L, request, editorAuth));
        verify(newsRepository).save(ownNews);
    }

    @Test
    void update_EditorRole_ShouldDenyUpdatingOthersNews() {
        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(editorUser));
        when(newsRepository.findById(11L)).thenReturn(Optional.of(othersNews));
        NewsUpdateRequestDto request = new NewsUpdateRequestDto("T", "C", "T", true);
        assertThrows(AccessDeniedException.class, () -> newsService.update(11L, request, editorAuth));
        verify(newsRepository, never()).save(any());
    }

    @Test
    void delete_EditorRole_ShouldAllowDeletingOwnNews() {
        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(editorUser));
        when(newsRepository.findById(10L)).thenReturn(Optional.of(ownNews));
        assertDoesNotThrow(() -> newsService.delete(10L, editorAuth));
        verify(newsRepository).delete(ownNews);
    }
}