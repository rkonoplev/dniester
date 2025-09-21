package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.request.RoleCreateRequestDto;
import com.example.newsplatform.dto.response.PermissionDto;
import com.example.newsplatform.dto.response.RoleDto;
import com.example.newsplatform.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Mapper for the entity {@link Role} and its DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = PermissionMapper.class)
public interface RoleMapper {

    /**
     * Converts a {@link Role} entity to a {@link RoleDto}.
     *
     * @param role The role entity.
     * @return The corresponding DTO.
     */
    @Mapping(target = "userCount", expression = "java(role.getUsers().size())")
    @Mapping(target = "permissions", source = "permissions")
    RoleDto toDto(Role role);

    /**
     * Converts a list of {@link Role} entities to a list of {@link RoleDto}s.
     *
     * @param roles The list of role entities.
     * @return The corresponding list of DTOs.
     */
    List<RoleDto> toDto(List<Role> roles);

    /**
     * Converts a {@link RoleCreateRequestDto} to a {@link Role} entity.
     *
     * @param dto The DTO for creating a role.
     * @return The corresponding entity.
     */
    Role toEntity(RoleCreateRequestDto dto);
}
