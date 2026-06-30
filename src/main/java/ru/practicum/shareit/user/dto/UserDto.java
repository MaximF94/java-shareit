package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserDto {
    Long id;
    String name;
    String email;

    public UserDto(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
