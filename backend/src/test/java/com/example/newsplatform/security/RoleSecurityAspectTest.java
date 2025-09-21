package com.example.newsplatform.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleSecurityAspectTest {

    @InjectMocks
    private RoleSecurityAspect roleSecurityAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private RequireRole requireRole;

    @Mock
    private RequireAnyRole requireAnyRole;

    @Mock
    private RequireAllRoles requireAllRoles;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthentication(String role) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void checkRole_shouldProceed_whenUserHasRole() throws Throwable {
        mockAuthentication("ADMIN");
        when(requireRole.value()).thenReturn("ADMIN");

        roleSecurityAspect.checkRole(joinPoint, requireRole);

        verify(joinPoint).proceed();
    }

    @Test
    void checkRole_shouldThrowException_whenUserDoesNotHaveRole() {
        mockAuthentication("EDITOR");
        when(requireRole.value()).thenReturn("ADMIN"); // Requires ADMIN but user is EDITOR

        assertThrows(AccessDeniedException.class, () -> {
            roleSecurityAspect.checkRole(joinPoint, requireRole);
        });
    }

    @Test
    void checkAnyRole_shouldProceed_whenUserHasOneOfRoles() throws Throwable {
        mockAuthentication("EDITOR");
        when(requireAnyRole.value()).thenReturn(new String[]{"ADMIN", "EDITOR"});

        roleSecurityAspect.checkAnyRole(joinPoint, requireAnyRole);

        verify(joinPoint).proceed();
    }

    @Test
    void checkAllRoles_shouldThrowException_whenUserDoesNotHaveAllRoles() {
        mockAuthentication("ADMIN");
        when(requireAllRoles.value()).thenReturn(new String[]{"ADMIN", "SUPER_ADMIN"});

        assertThrows(AccessDeniedException.class, () -> {
            roleSecurityAspect.checkAllRoles(joinPoint, requireAllRoles);
        });
    }

    @Test
    void checkSecurity_shouldThrowException_whenNoAuthentication() {
        when(requireRole.value()).thenReturn("ADMIN");
        assertThrows(AccessDeniedException.class, () -> {
            roleSecurityAspect.checkRole(joinPoint, requireRole);
        });
    }
}
