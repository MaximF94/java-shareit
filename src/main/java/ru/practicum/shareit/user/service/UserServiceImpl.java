package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.DuplicateEmailException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            return new NotFoundException("Пользователь не найден");
        });
    }

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        Set<String> errors = validate(user);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        checkEmailUniqueness(user.getEmail(), null);

        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {

        if (user.getId() == null || user.getId() == 0) {
            throw new ValidationException("Id должен быть указан");
        }

        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));


        String oldEmail = existingUser.getEmail();
        String oldName = existingUser.getName();

        try {
            updateUserFields(existingUser, user);

            Set<String> errors = validate(existingUser);
            if (!errors.isEmpty()) {
                throw new ValidationException(String.join(", ", errors));
            }

            if (!oldEmail.equals(existingUser.getEmail())) {
                checkEmailUniqueness(existingUser.getEmail(), user.getId());
            }

            return userRepository.save(existingUser);

        } catch (Exception e) {
            existingUser.setEmail(oldEmail);
            existingUser.setName(oldName);
            throw e;
        }
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    public User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            return new NotFoundException("Пользователь не найден");
        });
    }

    private void updateUserFields(User oldUser, User newUser) {

        if (!(newUser.getEmail() == null)) {
            oldUser.setEmail(newUser.getEmail());
        }

        if (!(newUser.getName() == null)) {
            oldUser.setName(newUser.getName());
        }

    }

    private Set<String> validate(User user) {

        Set<String> errors = new HashSet<>();

        if (!isValidEmail(user.getEmail())) {
            errors.add("электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getName().isBlank()) {
            errors.add("имя не может быть пустым");
        }

        return errors;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.contains("@");
    }

    private void checkEmailUniqueness(String email, Long excludeId) {
        boolean emailExists = userRepository.findAll().stream()
                .anyMatch(u -> {
                    if (excludeId != null && u.getId().equals(excludeId)) {
                        return false;
                    }
                    return u.getEmail().equals(email);
                });

        if (emailExists) {
            throw new DuplicateEmailException("Пользователь с email '" + email + "' уже существует");
        }
    }
}
