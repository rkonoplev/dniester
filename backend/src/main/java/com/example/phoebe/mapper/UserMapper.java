package com.example.phoebe.mapper;

import com.example.phoebe.dto.request.UserCreateRequestDto;
import com.example.phoebe.dto.response.UserDto;
import com.example.phoebe.dto.request.UserUpdateRequestDto;
import com.example.phoebe.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for converting between User entity and various User DTOs using MapStruct.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper extends BaseMapper {

    @Mapping(source = "roles", target = "roleNames", qualifiedByName = "rolesToNames")
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


}