package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User save(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }

        if (user.getId() == null || user.getId() == 0) {
            boolean emailExists = users.values().stream()
                    .anyMatch(existingUser -> existingUser.getEmail().equals(user.getEmail()));

            isExistsEmail(emailExists, user.getEmail());

            user.setId(getNextId());
            users.put(user.getId(), user);
            return user;
        }

        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            throw new ValidationException("Пользователь с id " + user.getId() + " не найден");
        }

        if (existingUser.getEmail().equals(user.getEmail())) {
            users.put(user.getId(), user);
            return user;
        }

        Long currentId = user.getId();
        boolean emailExists = users.values().stream()
                .filter(u -> !currentId.equals(u.getId()))
                .anyMatch(u -> u.getEmail().equals(user.getEmail()));

        isExistsEmail(emailExists, user.getEmail());

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        if (userId != 0) {
            users.remove(userId);
        }
    }

    private long getNextId() {
        long maxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }

    private void isExistsEmail(boolean emailExists, String email) {
        if (emailExists) {
            throw new ValidationException("Пользователь с email '" + email + "' уже существует");
        }
    }
}
