package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.UserCreateRequest;
import com.example.newsplatform.dto.UserDto;
import com.example.newsplatform.dto.UserUpdateRequest;
import com.example.newsplatform.entity.User;

/**
 * Utility class to map between User entity and various DTOs.
 */
public class UserMapper {

    public static UserDto toDto(User entity) {
        if (entity == null) return null;
        
        return new UserDto(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getFullName(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getRoles() != null ? 
                    entity.getRoles().stream().map(role -> role.getName()).collect(java.util.stream.Collectors.toSet()) : 
                    java.util.Set.of()
        );
    }

    public static User fromCreateRequest(UserCreateRequest request) {
        if (request == null) return null;
        
        User entity = new User();
        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());
        entity.setFullName(request.getFullName());
        entity.setActive(true); // Default to active
        return entity;
    }

    public static void updateEntity(User entity, UserUpdateRequest request) {
        if (entity == null || request == null) return;

        if (request.getEmail() != null) entity.setEmail(request.getEmail());
        if (request.getFullName() != null) entity.setFullName(request.getFullName());
        if (request.getActive() != null) entity.setActive(request.getActive());
    }
}