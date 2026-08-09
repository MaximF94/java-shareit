package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public class UserMapper {

    public static UserDto map(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User map(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public static Collection<UserDto> map(Collection<User> users) {
        return users.stream().map(UserMapper::map).toList();
    }
}
