package ru.practicum.shareit.user.model;

import lombok.Data;

@Data
public class User {
    Long id;
    String name;
    String email;

    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
