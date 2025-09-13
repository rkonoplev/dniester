package com.example.newsplatform.security;

import com.example.newsplatform.entity.Role;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleSecurityAspectTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private RoleSecurityAspect roleSecurityAspect;

    @Test
    void checkRole_WithValidRole_ShouldProceed() throws Throwable {
        // Setup
        User user = new User();
        user.setUsername("admin");
        Role adminRole = new Role();
        adminRole.setId(1L);
        user.setRoles(Set.of(adminRole));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(joinPoint.proceed()).thenReturn("success");
        SecurityContextHolder.setContext(securityContext);

        RequireRole requireRole = mock(RequireRole.class);
        when(requireRole.value()).thenReturn(new long[]{1L});

        // Execute
        Object result = roleSecurityAspect.checkRole(joinPoint, requireRole);

        // Verify
        assertEquals("success", result);
        verify(joinPoint).proceed();
    }

    @Test
    void checkRole_WithInvalidRole_ShouldThrowException() {
        // Setup
        User user = new User();
        user.setUsername("editor");
        Role editorRole = new Role();
        editorRole.setId(2L);
        user.setRoles(Set.of(editorRole));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("editor");
        when(userRepository.findByUsername("editor")).thenReturn(Optional.of(user));
        SecurityContextHolder.setContext(securityContext);

        RequireRole requireRole = mock(RequireRole.class);
        when(requireRole.value()).thenReturn(new long[]{1L}); // Requires ADMIN but user is EDITOR

        // Execute & Verify
        assertThrows(AccessDeniedException.class, 
            () -> roleSecurityAspect.checkRole(joinPoint, requireRole));
    }
}