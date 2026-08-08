package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateDto {
    private Long id;

    private String name;

    @Email(message = "Некорректный формат email")
    private String email;

    public UserUpdateDto(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
