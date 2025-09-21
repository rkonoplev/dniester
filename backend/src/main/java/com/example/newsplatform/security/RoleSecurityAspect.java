package com.example.newsplatform.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aspect for enforcing role-based security using annotations.
 * This implementation uses role names (Strings) and retrieves user roles
 * from the Spring SecurityContext, avoiding direct database calls.
 */
@Aspect
@Component
public class RoleSecurityAspect {

    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        if (!hasAnyRole(requireRole.value())) {
            throw new AccessDeniedException("Access denied: insufficient role permissions");
        }
        return joinPoint.proceed();
    }

    @Around("@annotation(requireAnyRole)")
    public Object checkAnyRole(ProceedingJoinPoint joinPoint, RequireAnyRole requireAnyRole) throws Throwable {
        if (!hasAnyRole(requireAnyRole.value())) {
            throw new AccessDeniedException("Access denied: requires any of specified roles");
        }
        return joinPoint.proceed();
    }

    @Around("@annotation(requireAllRoles)")
    public Object checkAllRoles(ProceedingJoinPoint joinPoint, RequireAllRoles requireAllRoles) throws Throwable {
        if (!hasAllRoles(requireAllRoles.value())) {
            throw new AccessDeniedException("Access denied: requires all specified roles");
        }
        return joinPoint.proceed();
    }

    private List<String> getCurrentUserRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AccessDeniedException("No authentication found");
        }
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                // Assuming roles are prefixed with "ROLE_" by Spring Security
                .map(s -> s.startsWith("ROLE_") ? s.substring(5) : s)
                .collect(Collectors.toList());
    }

    private boolean hasAnyRole(String... requiredRoles) {
        List<String> userRoles = getCurrentUserRoles();
        return Arrays.stream(requiredRoles)
                .anyMatch(userRoles::contains);
    }

    private boolean hasAllRoles(String... requiredRoles) {
        List<String> userRoles = getCurrentUserRoles();
        return Arrays.stream(requiredRoles)
                .allMatch(userRoles::contains);
    }
}
