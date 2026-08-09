package ru.practicum.shareit;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.DuplicateEmailException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateUser() {
        UserDto dto = new UserDto();
        dto.setName("Иван Петров");
        dto.setEmail("ivan@mail.ru");

        UserDto created = userService.createUser(dto);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Иван Петров", created.getName());
        assertEquals("ivan@mail.ru", created.getEmail());
    }

    @Test
    void testThrowDuplicateEmailException() {
        UserDto dto1 = new UserDto();
        dto1.setName("Иван");
        dto1.setEmail("same@mail.ru");
        userService.createUser(dto1);

        UserDto dto2 = new UserDto();
        dto2.setName("Петр");
        dto2.setEmail("same@mail.ru");

        assertThrows(DuplicateEmailException.class, () -> userService.createUser(dto2));
    }

    @Test
    void testGetUser() {
        UserDto dto = new UserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");
        UserDto created = userService.createUser(dto);

        UserDto found = userService.getUser(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Иван", found.getName());
    }

    @Test
    void testThrowNotFoundExceptionWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.getUser(999L));
    }

    @Test
    void testUpdateUser() {
        UserDto dto = new UserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");
        UserDto created = userService.createUser(dto);

        UserDto updateDto = new UserDto();
        updateDto.setName("Петр");
        updateDto.setEmail("petr@mail.ru");

        UserDto updated = userService.updateUser(updateDto, created.getId());

        assertEquals("Петр", updated.getName());
        assertEquals("petr@mail.ru", updated.getEmail());
    }

    @Test
    void testDeleteUser() {
        UserDto dto = new UserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");
        UserDto created = userService.createUser(dto);

        userService.deleteUser(created.getId());

        assertThrows(NotFoundException.class, () -> userService.getUser(created.getId()));
    }

    @Test
    void testGetAllUsers() {
        UserDto dto1 = new UserDto();
        dto1.setName("Иван");
        dto1.setEmail("ivan@mail.ru");
        userService.createUser(dto1);

        UserDto dto2 = new UserDto();
        dto2.setName("Петр");
        dto2.setEmail("petr@mail.ru");
        userService.createUser(dto2);

        var users = userService.getAllUsers();

        assertNotNull(users);
        assertTrue(users.size() >= 2);
    }
}
