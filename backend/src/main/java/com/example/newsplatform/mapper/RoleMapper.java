package com.example.newsplatform.mapper;

import com.example.newsplatform.dto.RoleDto;
import com.example.newsplatform.entity.Role;

/**
 * Utility class to map between Role entity and DTOs.
 */
public class RoleMapper {

    public static RoleDto toDto(Role entity) {
        if (entity == null) return null;
        
        return new RoleDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }

    public static Role fromDto(RoleDto dto) {
        if (dto == null) return null;
        
        Role entity = new Role();
        entity.setId(dto.id());
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        return entity;
    }
}