package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private Long createUser(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return userService.createUser(dto).getId();
    }

    private Long createItem(Long userId, String name) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription("Описание");
        dto.setAvailable(true);
        return itemService.createItem(dto, userId).getId();
    }

    @Test
    void testCreateBooking() {
        Long ownerId = createUser("Владелец", "owner@mail.ru");
        Long bookerId = createUser("Арендатор", "booker@mail.ru");
        Long itemId = createItem(ownerId, "Дрель");

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(itemId);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.createBooking(dto, bookerId);

        assertNotNull(created);
        assertNotNull(created.getId());
    }

    @Test
    void testApproveBooking() {
        Long ownerId = createUser("Владелец", "owner@mail.ru");
        Long bookerId = createUser("Арендатор", "booker@mail.ru");
        Long itemId = createItem(ownerId, "Дрель");

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(itemId);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.createBooking(dto, bookerId);

        BookingDto approved = bookingService.approveBooking(created.getId(), true, ownerId);

        assertNotNull(approved);
    }

    @Test
    void testGetBookingById() {
        Long ownerId = createUser("Владелец", "owner@mail.ru");
        Long bookerId = createUser("Арендатор", "booker@mail.ru");
        Long itemId = createItem(ownerId, "Дрель");

        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(itemId);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = bookingService.createBooking(dto, bookerId);

        BookingDto found = bookingService.findBookingById(created.getId(), bookerId);

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void testThrowNotFoundExceptionWhenBookingNotFound() {
        assertThrows(NotFoundException.class, () -> bookingService.findBookingById(999L, 1L));
    }
}
