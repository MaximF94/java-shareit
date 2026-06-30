package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    Collection<User> findAll();

    User save(User user);

    void deleteUser(long userId);
}
