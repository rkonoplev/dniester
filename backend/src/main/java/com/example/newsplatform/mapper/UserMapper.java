package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.request.UserCreateRequestDto;
import com.example.newsplatform.dto.response.UserDto;
import com.example.newsplatform.dto.request.UserUpdateRequestDto;
import com.example.newsplatform.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for converting between User entity and various User DTOs using MapStruct.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(source = "roles", target = "roleNames")
    UserDto toDto(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "active", ignore = true)
    User fromCreateRequest(UserCreateRequestDto request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateEntity(@MappingTarget User entity, UserUpdateRequestDto request);

    default Set<String> rolesToRoleNames(Set<com.example.newsplatform.entity.Role> roles) {
        if (roles == null || roles.isEmpty()) {
            // Default to a non-privileged role for safety
            return Set.of("EDITOR");
        }
        return roles.stream()
                .map(com.example.newsplatform.entity.Role::getName)
                .collect(Collectors.toSet());
    }
}