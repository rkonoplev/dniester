package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.request.RoleCreateRequestDto;
import com.example.newsplatform.dto.request.RoleUpdateRequestDto;
import com.example.newsplatform.dto.response.RoleDto;
import com.example.newsplatform.entity.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    @Mapping(target = "permissionNames", ignore = true)
    RoleDto toDto(Role role);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toEntity(RoleCreateRequestDto roleCreateRequestDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    Role toEntity(RoleUpdateRequestDto roleUpdateRequestDto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    void updateEntityFromDto(RoleUpdateRequestDto dto, @MappingTarget Role role);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    void updateEntityFromDto(RoleCreateRequestDto dto, @MappingTarget Role role);
    
    default Role fromCreateRequest(RoleCreateRequestDto createRequest) {
        if (createRequest == null) {
            return null;
        }
        
        Role role = new Role();
        role.setName(createRequest.getName());
        role.setDescription(createRequest.getDescription());
        return role;
    }
}
