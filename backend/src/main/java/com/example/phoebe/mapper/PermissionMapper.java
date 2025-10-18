package com.example.phoebe.mapper;

import com.example.phoebe.dto.response.PermissionDto;
import com.example.phoebe.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import java.util.List;

/**
 * Mapper for converting between {@link Permission} entity and its DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PermissionMapper {
    /**
     * Maps a single Permission entity to its DTO representation.
     */
    PermissionDto toDto(Permission permission);

    /**
     * Maps a list of Permission entities to a list of DTOs.
     */
    List<PermissionDto> toDto(List<Permission> permissions);
}