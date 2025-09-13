package com.example.newsplatform.security;

import com.example.newsplatform.entity.Role;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SecurityAspectIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestSecurityService testSecurityService;

    @Test
    void requireAnyRole_WithValidRole_ShouldSucceed() {
        // Setup user with ADMIN role
        User admin = createUserWithRole("admin", 1L);
        setSecurityContext("admin");

        // Should succeed
        assertDoesNotThrow(() -> testSecurityService.adminOrEditorMethod());
    }

    @Test
    void requireAnyRole_WithoutValidRole_ShouldFail() {
        // Setup user with no roles
        User user = new User();
        user.setUsername("user");
        user.setEmail("user@test.com");
        user.setActive(true);
        user.setRoles(Set.of());
        userRepository.save(user);
        
        setSecurityContext("user");

        // Should fail
        assertThrows(AccessDeniedException.class, 
            () -> testSecurityService.adminOrEditorMethod());
    }

    private User createUserWithRole(String username, Long roleId) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@test.com");
        user.setActive(true);
        
        Role role = new Role();
        role.setId(roleId);
        role.setName(roleId == 1L ? "ADMIN" : "EDITOR");
        
        user.setRoles(Set.of(role));
        return userRepository.save(user);
    }

    private void setSecurityContext(String username) {
        UsernamePasswordAuthenticationToken auth = 
            new UsernamePasswordAuthenticationToken(username, "password");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}