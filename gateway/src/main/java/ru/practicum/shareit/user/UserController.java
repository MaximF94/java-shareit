package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Controller
@RequestMapping(path = "/users")
@Validated
public class UserController {

    private final UserClient userClient;

    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto dto) {
        return userClient.createUser(dto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        return userClient.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @Valid @RequestBody UserUpdateDto dto) {
        return userClient.updateUser(userId, dto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        return userClient.deleteUser(userId);
    }
}
