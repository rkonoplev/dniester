package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.response.PermissionDto;
import com.example.newsplatform.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Mapper for the entity {@link Permission} and its DTO {@link PermissionDto}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper {

    /**
     * Converts a {@link Permission} entity to a {@link PermissionDto}.
     *
     * @param permission The permission entity.
     * @return The corresponding DTO.
     */
    PermissionDto toDto(Permission permission);

    /**
     * Converts a list of {@link Permission} entities to a list of {@link PermissionDto}s.
     *
     * @param permissions The list of permission entities.
     * @return The corresponding list of DTOs.
     */
    List<PermissionDto> toDto(List<Permission> permissions);
}
