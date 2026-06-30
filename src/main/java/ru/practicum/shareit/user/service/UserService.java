package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    User getUser(Long id);

    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long userId);
}
