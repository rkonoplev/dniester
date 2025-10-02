package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.request.RoleCreateRequestDto;
import com.example.newsplatform.dto.request.RoleUpdateRequestDto;
import com.example.newsplatform.dto.response.RoleDto;
import com.example.newsplatform.entity.Permission;
import com.example.newsplatform.entity.Role;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    @Mapping(target = "permissionNames", source = "permissions")
    RoleDto toDto(Role role);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    Role fromCreateRequest(RoleCreateRequestDto createRequest);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    void updateEntityFromDto(RoleUpdateRequestDto dto, @MappingTarget Role role);
    
    default Set<String> mapPermissionsToNames(Set<Permission> permissions) {
        if (permissions == null) {
            return Set.of();
        }
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
}
