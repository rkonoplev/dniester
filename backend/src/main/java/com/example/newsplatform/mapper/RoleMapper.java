package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.request.RoleCreateRequestDto;
import com.example.newsplatform.dto.request.RoleUpdateRequestDto;
import com.example.newsplatform.dto.response.RoleDto;
import com.example.newsplatform.entity.Permission;
import com.example.newsplatform.entity.Role;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for converting between {@link Role} entity and its DTOs using MapStruct.
 * It handles mappings for API responses, creation requests, and update requests.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {

    /**
     * Maps a Role entity to a RoleDto for API responses.
     *
     * @param role The source Role entity.
     * @return The mapped RoleDto, including role and permission names.
     */
    @Mapping(source = "permissions", target = "permissionNames", qualifiedByName = "permissionsToNames")
    RoleDto toDto(Role role);

    /**
     * Maps a RoleCreateRequestDto to a new Role entity.
     * Ignores relationship fields and auto-generated fields like 'id'.
     *
     * @param createRequest The DTO containing data for the new role.
     * @return A new Role entity ready for persistence.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    Role fromCreateRequest(RoleCreateRequestDto createRequest);

    /**
     * Updates an existing Role entity from a request DTO, ignoring null values.
     * This method is designed for partial updates (PATCH-style behavior).
     *
     * @param updateRequest The source DTO with potentially partial data.
     * @param role          The target entity to be updated.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    void updateEntityFromDto(RoleUpdateRequestDto updateRequest, @MappingTarget Role role);

    /**
     * Helper method to convert a Set of Permission entities to a Set of their names.
     * This is used by MapStruct via the `qualifiedByName` attribute.
     *
     * @param permissions The set of Permission entities.
     * @return A set of permission name strings.
     */
    @Named("permissionsToNames")
    default Set<String> permissionsToNames(Set<Permission> permissions) {
        if (permissions == null) {
            return Set.of();
        }
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
}