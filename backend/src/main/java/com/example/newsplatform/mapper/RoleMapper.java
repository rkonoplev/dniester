package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.request.RoleCreateRequestDto;
import com.example.newsplatform.dto.request.RoleUpdateRequestDto;
import com.example.newsplatform.dto.response.RoleDto;
import com.example.newsplatform.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper extends BaseMapper {

    @Mapping(target = "permissionNames", source = "permissions", qualifiedByName = "permissionsToNames")
    RoleDto toDto(Role role);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    Role fromCreateRequest(RoleCreateRequestDto createRequest);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    void updateEntityFromDto(RoleUpdateRequestDto dto, @MappingTarget Role role);
    

}
