package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

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
