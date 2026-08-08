package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto getUser(Long id);

    Collection<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long id);

    void deleteUser(Long userId);
}
