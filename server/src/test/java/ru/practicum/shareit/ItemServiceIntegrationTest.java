package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private Long createUser(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return userService.createUser(dto).getId();
    }

    @Test
    void testCreateItem() {
        Long userId = createUser("Иван", "ivan@mail.ru");

        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная дрель");
        dto.setAvailable(true);

        ItemDto created = itemService.createItem(dto, userId);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Дрель", created.getName());
        assertEquals("Мощная дрель", created.getDescription());
        assertTrue(created.getAvailable());
    }

    @Test
    void testGetItem() {
        Long userId = createUser("Иван", "ivan@mail.ru");

        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная дрель");
        dto.setAvailable(true);
        ItemDto created = itemService.createItem(dto, userId);

        ItemDto found = itemService.getItem(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Дрель", found.getName());
    }

    @Test
    void testThrowNotFoundExceptionWhenItemNotFound() {
        assertThrows(NotFoundException.class, () -> itemService.getItem(999L));
    }

    @Test
    void testGetAllItemsFromUser() {
        Long userId = createUser("Иван", "ivan@mail.ru");

        ItemDto dto1 = new ItemDto();
        dto1.setName("Дрель");
        dto1.setDescription("Мощная дрель");
        dto1.setAvailable(true);
        itemService.createItem(dto1, userId);

        ItemDto dto2 = new ItemDto();
        dto2.setName("Перфоратор");
        dto2.setDescription("Мощный перфоратор");
        dto2.setAvailable(true);
        itemService.createItem(dto2, userId);

        var items = itemService.getAllItemsFromUser(userId);

        assertNotNull(items);
        assertEquals(2, items.size());
    }

    @Test
    void testUpdateItem() {
        Long userId = createUser("Иван", "ivan@mail.ru");

        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная дрель");
        dto.setAvailable(true);
        ItemDto created = itemService.createItem(dto, userId);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Перфоратор");
        updateDto.setDescription("Мощный перфоратор");
        updateDto.setAvailable(false);

        ItemDto updated = itemService.updateItem(updateDto, created.getId(), userId);

        assertEquals("Перфоратор", updated.getName());
        assertEquals("Мощный перфоратор", updated.getDescription());
        assertFalse(updated.getAvailable());
    }
}
