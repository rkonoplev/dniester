package com.example.phoebe.mapper;

import com.example.phoebe.dto.request.ChannelSettingsUpdateDto;
import com.example.phoebe.dto.response.ChannelSettingsDto;
import com.example.phoebe.entity.ChannelSettings;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for ChannelSettings entity and DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChannelSettingsMapper {

    /**
     * Maps ChannelSettings entity to response DTO.
     */
    ChannelSettingsDto toDto(ChannelSettings entity);

    /**
     * Updates ChannelSettings entity from update DTO.
     * Ignores null values to support partial updates.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget ChannelSettings entity, ChannelSettingsUpdateDto dto);
}