package com.example.phoebe.security;

import com.example.phoebe.entity.News;
import com.example.phoebe.entity.Role;
import com.example.phoebe.entity.User;
import com.example.phoebe.repository.NewsRepository;
import com.example.phoebe.repository.RoleRepository;
import com.example.phoebe.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for role-based security and content ownership verification.
 * These tests verify that ADMINs can access any content, while EDITORs can only access their own.
 */
@SpringBootTest
@ActiveProfiles("test") // Uses H2 in-memory database for speed and isolation
@Transactional // Rolls back database changes after each test
class RoleBasedSecurityTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthorVerification authorVerification;

    private User adminUser;
    private User editorUser;
    private News editorsNews;

    /**
     * Sets up a standard fixture for security tests.
     * Creates and persists ADMIN and EDITOR roles, an admin user, an editor user,
     * and a news article authored by the editor.
     */
    @BeforeEach
    void setUp() {
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        roleRepository.save(adminRole);

        Role editorRole = new Role();
        editorRole.setName("EDITOR");
        roleRepository.save(editorRole);

        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword("password");
        adminUser.setActive(true);
        adminUser.setRoles(Set.of(adminRole));
        userRepository.save(adminUser);

        editorUser = new User();
        editorUser.setUsername("editor");
        editorUser.setEmail("editor@test.com");
        editorUser.setPassword("password");
        editorUser.setActive(true);
        editorUser.setRoles(Set.of(editorRole));
        userRepository.save(editorUser);

        editorsNews = new News();
        editorsNews.setTitle("Editor's News");
        editorsNews.setBody("Content");
        editorsNews.setAuthor(editorUser);
        newsRepository.save(editorsNews);

        // Clear context to ensure no authentication leakage between tests
        SecurityContextHolder.clearContext();
    }

    @Test
    void testAdminCanAccessAnyContent() {
        // Given: an admin is authenticated
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(adminUser.getUsername(), "password")
        );

        // When & Then: admin should have access to news authored by the editor
        assertTrue(authorVerification.hasAccessToNews(adminUser, editorsNews.getId()));
    }

    @Test
    void testEditorCanOnlyAccessOwnContent() {
        // Given: an editor is authenticated
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(editorUser.getUsername(), "password")
        );

        // When & Then: editor should have access to their own news
        assertTrue(authorVerification.hasAccessToNews(editorUser, editorsNews.getId()));

        // And when another user's news is created
        News otherNews = new News();
        otherNews.setTitle("Admin News");
        otherNews.setBody("Admin Content");
        otherNews.setAuthor(adminUser);
        newsRepository.save(otherNews);

        // Then: editor should NOT have access to it
        assertFalse(authorVerification.hasAccessToNews(editorUser, otherNews.getId()));
    }

    @Test
    void testAuthorVerificationMethod() {
        // When & Then: verify that the isAuthor check is correct for the actual author
        assertTrue(authorVerification.isAuthor(editorUser, editorsNews));

        // And when & Then: verify that the check is correct for a different user
        assertFalse(authorVerification.isAuthor(adminUser, editorsNews));
    }
}
