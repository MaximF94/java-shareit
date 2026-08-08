package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

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

    @Test
    public void testCreateItemRequest() {
        Long userId = createUser("Ivan", "ivan@mail.ru");

        ItemRequestDtoCreate dto = new ItemRequestDtoCreate();
        dto.setDescription("Need a drill");

        ItemRequestDtoCreate created = itemRequestService.createItemRequest(dto, userId);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Need a drill", created.getDescription());
    }

    @Test
    public void testFindItemRequestById() {
        Long userId = createUser("Ivan", "ivan@mail.ru");

        ItemRequestDtoCreate dto = new ItemRequestDtoCreate();
        dto.setDescription("Need a drill");
        ItemRequestDtoCreate created = itemRequestService.createItemRequest(dto, userId);

        ItemRequestDto found = itemRequestService.findItemRequestById(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Need a drill", found.getDescription());
    }

    @Test
    public void testThrowNotFoundExceptionWhenRequestNotFound() {
        assertThrows(NotFoundException.class, () -> itemRequestService.findItemRequestById(999L));
    }

    @Test
    public void testFindAllByRequestorId() {
        Long userId = createUser("Ivan", "ivan@mail.ru");

        ItemRequestDtoCreate dto1 = new ItemRequestDtoCreate();
        dto1.setDescription("Need a drill");
        itemRequestService.createItemRequest(dto1, userId);

        ItemRequestDtoCreate dto2 = new ItemRequestDtoCreate();
        dto2.setDescription("Need a hammer");
        itemRequestService.createItemRequest(dto2, userId);

        var requests = itemRequestService.findAllByRequestorId(userId);

        assertNotNull(requests);
        assertEquals(2, requests.size());
    }

    @Test
    public void testFindAllOtherRequests() {
        Long user1Id = createUser("User1", "user1@mail.ru");
        Long user2Id = createUser("User2", "user2@mail.ru");

        ItemRequestDtoCreate dto1 = new ItemRequestDtoCreate();
        dto1.setDescription("Need a drill");
        itemRequestService.createItemRequest(dto1, user1Id);

        ItemRequestDtoCreate dto2 = new ItemRequestDtoCreate();
        dto2.setDescription("Need a hammer");
        itemRequestService.createItemRequest(dto2, user2Id);

        var otherRequests = itemRequestService.findAllOtherRequests(user1Id);

        assertNotNull(otherRequests);
        assertEquals(1, otherRequests.size());
        assertEquals("Need a hammer", otherRequests.iterator().next().getDescription());
    }
}
