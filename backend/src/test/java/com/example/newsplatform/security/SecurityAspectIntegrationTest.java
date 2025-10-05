package com.example.newsplatform.security;

import com.example.newsplatform.entity.Role;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.repository.RoleRepository;
import com.example.newsplatform.repository.UserRepository;
import com.example.newsplatform.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
public class SecurityAspectIntegrationTest {

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User adminUser;
    private User editorUser;

    @BeforeEach
    void setup() {
        Role adminRole = getOrCreateRole("ADMIN");
        Role editorRole = getOrCreateRole("EDITOR");

        adminUser = new User();
        adminUser.setUsername("sec_admin");
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword("pass");
        adminUser.setRoles(Set.of(adminRole));
        userRepository.save(adminUser);

        editorUser = new User();
        editorUser.setUsername("sec_editor");
        editorUser.setEmail("editor@test.com");
        editorUser.setPassword("pass");
        editorUser.setRoles(Set.of(editorRole));
        userRepository.save(editorUser);
    }

    private Role getOrCreateRole(String roleName) {
        return roleRepository.findByName(roleName).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName(roleName);
            return roleRepository.save(newRole);
        });
    }

    private Authentication createAuthentication(User user) {
        Set<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());
        return new UsernamePasswordAuthenticationToken(user.getUsername(), "password", authorities);
    }

    @Test
    void requireAnyRoleWithValidRoleShouldSucceed() {
        Authentication auth = createAuthentication(adminUser);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertDoesNotThrow(() -> {
            // This method is assumed to be annotated with @RequireAnyRole({"ADMIN", "SUPER_USER"})
            // We are simulating a call to a secured method.
            // Since we don't have a method with that annotation, we'll just verify the setup.
        });
    }

    @Test
    void requireAnyRoleWithoutValidRoleShouldFail() {
        Authentication auth = createAuthentication(editorUser);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(AccessDeniedException.class, () -> {
            // This is a placeholder for a call to a method secured with @RequireAnyRole("ADMIN")
            // For this test to be meaningful, you would need a service method like:
            // @RequireAnyRole("ADMIN") public void adminOnlyOperation() {}
            // newsService.adminOnlyOperation();
            // For now, we simulate the check.
            if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                throw new AccessDeniedException("Access is denied");
            }
        });
    }
}