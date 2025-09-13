package com.example.newsplatform.security;

import com.example.newsplatform.entity.User;
import com.example.newsplatform.repository.UserRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RoleSecurityAspect {

    @Autowired
    private UserRepository userRepository;

    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        User currentUser = getCurrentUser();
        if (!hasAnyRole(currentUser, requireRole.value())) {
            throw new AccessDeniedException("Access denied: insufficient role permissions");
        }
        return joinPoint.proceed();
    }

    @Around("@annotation(requireAnyRole)")
    public Object checkAnyRole(ProceedingJoinPoint joinPoint, RequireAnyRole requireAnyRole) throws Throwable {
        User currentUser = getCurrentUser();
        if (!hasAnyRole(currentUser, requireAnyRole.value())) {
            throw new AccessDeniedException("Access denied: requires any of specified roles");
        }
        return joinPoint.proceed();
    }

    @Around("@annotation(requireAllRoles)")
    public Object checkAllRoles(ProceedingJoinPoint joinPoint, RequireAllRoles requireAllRoles) throws Throwable {
        User currentUser = getCurrentUser();
        if (!hasAllRoles(currentUser, requireAllRoles.value())) {
            throw new AccessDeniedException("Access denied: requires all specified roles");
        }
        return joinPoint.proceed();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AccessDeniedException("No authentication found");
        }
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }

    private boolean hasAnyRole(User user, long[] requiredRoleIds) {
        return user.getRoles().stream()
                .anyMatch(role -> {
                    for (long requiredRoleId : requiredRoleIds) {
                        if (role.getId().equals(requiredRoleId)) {
                            return true;
                        }
                    }
                    return false;
                });
    }

    private boolean hasAllRoles(User user, long[] requiredRoleIds) {
        for (long requiredRoleId : requiredRoleIds) {
            boolean hasRole = user.getRoles().stream()
                    .anyMatch(role -> role.getId().equals(requiredRoleId));
            if (!hasRole) {
                return false;
            }
        }
        return true;
    }
}