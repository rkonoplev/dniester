package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.UserCreateRequest;
import com.example.newsplatform.dto.UserDto;
import com.example.newsplatform.dto.UserUpdateRequest;
import com.example.newsplatform.entity.User;
import java.util.Set;

/**
 * Utility class to map between User entity and various DTOs.
 * 
 * Default Role Behavior:
 * - If a user has no roles specified, defaults to 'Admin' role in DTO responses
 * - This serves as a temporary placeholder until roles are fully defined
 * - The role field cannot be null in API responses
 */
public class UserMapper {

    public static UserDto toDto(User entity) {
        if (entity == null) return null;
        
        // Default to Admin role if no roles specified
        Set<String> roleNames;
        if (entity.getRoles() == null || entity.getRoles().isEmpty()) {
            roleNames = java.util.Set.of("Admin");
        } else {
            roleNames = entity.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(java.util.stream.Collectors.toSet());
        }
        
        return new UserDto(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.isActive(),
                roleNames
        );
    }

    public static User fromCreateRequest(UserCreateRequest request) {
        if (request == null) return null;
        
        User entity = new User();
        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());
        entity.setActive(request.isActive());
        return entity;
    }

    public static void updateEntity(User entity, UserUpdateRequest request) {
        if (entity == null || request == null) return;

        if (request.getEmail() != null) entity.setEmail(request.getEmail());
        if (request.getActive() != null) entity.setActive(request.getActive());
    }
}