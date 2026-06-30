package ru.practicum.shareit.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto dto) {
        User user = UserMapper.map(dto);
        User createdUser = userService.createUser(user);
        UserDto createdUserDto = UserMapper.map(createdUser);
        return ResponseEntity.ok(createdUserDto);
    }

    @GetMapping
    public ResponseEntity<Collection<UserDto>> getAll() {
        Collection<User> users = userService.getAllUsers();
        Collection<UserDto> dtos = UserMapper.map(users);

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable long userId) {
        User user = userService.getUser(userId);
        UserDto dto = UserMapper.map(user);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@PathVariable Long userId, @RequestBody UserDto dto) {
        dto.setId(userId);
        User updatedUser = userService.updateUser(UserMapper.map(dto));
        UserDto updatedUserDto = UserMapper.map(updatedUser);
        return ResponseEntity.ok(updatedUserDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
