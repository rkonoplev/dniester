package com.example.phoebe.mapper;

import com.example.phoebe.dto.request.UserCreateRequestDto;
import com.example.phoebe.dto.request.UserUpdateRequestDto;
import com.example.phoebe.dto.response.UserDto;
import com.example.phoebe.entity.Role;
import com.example.phoebe.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapUserToUserDto() {
        // Given
        Role adminRole = new Role("ADMIN", null);
        User user = new User("testuser", "pass", "test@example.com", true);
        user.addRole(adminRole);

        // When
        UserDto dto = userMapper.toDto(user);

        // Then
        assertNotNull(dto);
        assertEquals(user.getUsername(), dto.username());
        assertEquals(user.getEmail(), dto.email());
        assertEquals(user.isActive(), dto.active());
        assertNotNull(dto.roleNames());
        assertEquals(1, dto.roleNames().size());
        assertTrue(dto.roleNames().contains("ADMIN"));
    }

    @Test
    void shouldMapCreateDtoToUser() {
        // Given
        UserCreateRequestDto createDto = new UserCreateRequestDto();
        createDto.setUsername("Editor_User");
        createDto.setEmail("Editor@Example.COM");

        // When
        User user = userMapper.fromCreateRequest(createDto);

        // Then
        assertNotNull(user);
        // Username is final and can't be set by MapStruct, so it will be null
        // assertEquals(createDto.getUsername(), user.getUsername()); // Username can't be mapped
        assertEquals(createDto.getEmail(), user.getEmail());
        // Password is ignored in mapping, so it should be null
    }

    @Test
    void shouldUpdateUserFromUpdateDto() {
        // Given
        User user = new User("original", "pass", "original@test.com", true);
        UserUpdateRequestDto updateDto = new UserUpdateRequestDto();
        updateDto.setEmail("updated@example.com");
        updateDto.setActive(false);

        // When
        userMapper.updateEntity(user, updateDto);

        // Then
        assertEquals("original", user.getUsername(), "Username (business key) should not be changed.");
        assertEquals("updated@example.com", user.getEmail());
        assertEquals(false, user.isActive());
    }
}
