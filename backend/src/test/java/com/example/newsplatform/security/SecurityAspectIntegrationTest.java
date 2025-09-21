package com.example.newsplatform.security;

import com.example.newsplatform.entity.Role;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.repository.RoleRepository;
import com.example.newsplatform.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SecurityAspectIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestSecurityService testSecurityService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void requireAnyRole_WithValidRole_ShouldSucceed() {
        // Setup user with ADMIN role
        User admin = createUserWithRoles("admin", "ADMIN");
        setSecurityContext(admin);

        // Should succeed because user has one of the required roles (ADMIN or EDITOR)
        assertDoesNotThrow(() -> testSecurityService.requiresAdminOrEditor());
    }

    @Test
    void requireAnyRole_WithoutValidRole_ShouldFail() {
        // Setup user with no roles
        User user = createUserWithRoles("user");
        setSecurityContext(user);

        // Should fail because user has no roles
        assertThrows(AccessDeniedException.class,
                () -> testSecurityService.requiresAdminOrEditor());
    }

    private User createUserWithRoles(String username, String... roleNames) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@test.com");
        user.setActive(true);

        Set<Role> roles = Set.of(roleNames).stream().map(roleName -> {
            // Use existing role or create a new one for the test
            return roleRepository.findByName(roleName).orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName(roleName);
                return roleRepository.save(newRole);
            });
        }).collect(Collectors.toSet());

        user.setRoles(roles);
        return userRepository.save(user);
    }

    private void setSecurityContext(User user) {
        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user.getUsername(), "password", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
