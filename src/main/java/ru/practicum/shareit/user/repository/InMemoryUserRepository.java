package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, User> emailIndex = new HashMap<>();
    private long nextId = 1;

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(emailIndex.get(email));
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId() == 0) {
            user.setId(nextId++);
        }

        emailIndex.put(user.getEmail(), user);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        emailIndex.put(user.getEmail(), user);
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        if (userId > 0) {
            User removed = users.remove(userId);
            if (removed != null) {
                emailIndex.remove(removed.getEmail());
            }
        }
    }

}
