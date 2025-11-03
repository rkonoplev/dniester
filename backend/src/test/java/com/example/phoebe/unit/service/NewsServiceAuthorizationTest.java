package com.example.phoebe.service;

import com.example.phoebe.entity.News;
import com.example.phoebe.entity.User;
import com.example.phoebe.repository.NewsRepository;
import com.example.phoebe.repository.UserRepository;
import com.example.phoebe.service.impl.NewsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsServiceAuthorizationTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NewsServiceImpl newsService;

    private User adminUser;
    private User editorUser;
    private User anotherUser;
    private News editorsNews;

    @BeforeEach
    void setUp() {
        adminUser = new User("admin", "pass", "admin@test.com", true);
        editorUser = new User("editor", "pass", "editor@test.com", true);
        anotherUser = new User("another", "pass", "another@test.com", true);

        editorsNews = new News();
        editorsNews.setId(1L);
        editorsNews.setAuthor(editorUser);
    }

    private Authentication createAuth(User user, String role) {
        return new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    @Test
    void adminShouldHaveAccessToAnyNews() {
        // Given
        Authentication adminAuth = createAuth(adminUser, "ADMIN");

        // When & Then
        assertTrue(newsService.canAccessNews(editorsNews.getId(), adminAuth));
    }

    @Test
    void authorShouldHaveAccessToOwnNews() {
        // Given
        Authentication authorAuth = createAuth(editorUser, "EDITOR");
        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(editorUser));
        when(newsRepository.existsByIdAndAuthorId(editorsNews.getId(), editorUser.getId())).thenReturn(true);

        // When & Then
        assertTrue(newsService.isAuthor(editorsNews.getId(), authorAuth));
    }

    @Test
    void nonAuthorShouldNotHaveAccess() {
        // Given
        Authentication anotherUserAuth = createAuth(anotherUser, "EDITOR");
        when(userRepository.findByUsername("another")).thenReturn(Optional.of(anotherUser));
        when(newsRepository.existsByIdAndAuthorId(editorsNews.getId(), anotherUser.getId())).thenReturn(false);

        // When & Then
        assertFalse(newsService.isAuthor(editorsNews.getId(), anotherUserAuth));
    }
}
