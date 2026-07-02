package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.DuplicateEmailException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
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
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            return new NotFoundException("Пользователь не найден");
        });

        return UserMapper.map(user);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<User> users = userRepository.findAll();
        return UserMapper.map(users);
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        User user = UserMapper.map(userDto);

        validate(user);

        checkEmailUniqueness(user.getEmail(), null);

        User createdUser = userRepository.save(user);

        return UserMapper.map(createdUser);

    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {

        userDto.setId(id);

        User user = UserMapper.map(userDto);

        if (user.getId() == null || user.getId() == 0) {
            throw new ValidationException("Id должен быть указан");
        }

        User existingUser = findExistingUser(user);


        User userToValidate = new User();
        userToValidate.setId(existingUser.getId());
        userToValidate.setName(user.getName() != null ? user.getName() : existingUser.getName());
        userToValidate.setEmail(user.getEmail() != null ? user.getEmail() : existingUser.getEmail());

        validate(userToValidate);

        if (user.getEmail() != null && !existingUser.getEmail().equals(user.getEmail())) {
            checkEmailUniqueness(user.getEmail(), user.getId());
        }

        updateUserFields(existingUser, user);

        User updatedUser = userRepository.update(existingUser);

        return UserMapper.map(updatedUser);
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

        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }

        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }

    }

    private Set<String> checkDataField(User user) {

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

    private User findExistingUser(User user) {
        return userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + user.getId() + " не найден"));
    }

    private void validate(User user) {
        Set<String> errors = checkDataField(user);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }
    }

    private void checkEmailUniqueness(String email, Long excludeId) {
        userRepository.findByEmail(email)
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(excludeId)) {
                        throw new DuplicateEmailException("Пользователь с email '" + email + "' уже существует");
                    }
                });
    }
}
