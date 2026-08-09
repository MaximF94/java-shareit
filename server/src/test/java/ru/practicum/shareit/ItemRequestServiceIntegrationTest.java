package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

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

    private Long requestorId;
    private Long responderId;

    @BeforeEach
    void setUp() {
        requestorId = createUser("Анна", "anna@mail.ru");
        responderId = createUser("Петр", "petr@mail.ru");
    }

    private Long createUser(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return userService.createUser(dto).getId();
    }

    @Test
    public void testCreateItemRequest() {
        ItemRequestDtoCreate dto = new ItemRequestDtoCreate();
        dto.setDescription("Нужна дрель");

        ItemRequestDtoCreate created = itemRequestService.createItemRequest(dto, requestorId);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Нужна дрель", created.getDescription());
    }

    @Test
    public void testFindItemRequestById() {
        ItemRequestDtoCreate requestDto = new ItemRequestDtoCreate();
        requestDto.setDescription("Нужна дрель");
        ItemRequestDtoCreate createdRequest = itemRequestService.createItemRequest(requestDto, requestorId);
        Long requestId = createdRequest.getId();

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Дрель");
        itemDto.setDescription("Мощная дрель");
        itemDto.setAvailable(true);
        itemDto.setRequestId(requestId);

        ItemRequestDto foundRequest = itemRequestService.findItemRequestById(requestId);

        assertNotNull(foundRequest);
        assertEquals(requestId, foundRequest.getId());
        assertEquals("Нужна дрель", foundRequest.getDescription());
        assertEquals(requestorId, foundRequest.getRequestorId());


    }

    @Test
    public void testFindItemRequestWithMultipleItems() {
        ItemRequestDtoCreate requestDto = new ItemRequestDtoCreate();
        requestDto.setDescription("Нужна дрель или молоток");
        ItemRequestDtoCreate createdRequest = itemRequestService.createItemRequest(requestDto, requestorId);
        Long requestId = createdRequest.getId();

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Дрель");
        itemDto1.setDescription("Мощная дрель");
        itemDto1.setAvailable(true);
        itemDto1.setRequestId(requestId);
        itemService.createItem(itemDto1, responderId);

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Молоток");
        itemDto2.setDescription("Большой молоток");
        itemDto2.setAvailable(true);
        itemDto2.setRequestId(requestId);
        itemService.createItem(itemDto2, responderId);

        ItemRequestDto foundRequest = itemRequestService.findItemRequestById(requestId);

        assertNotNull(foundRequest);
    }

    @Test
    public void testThrowNotFoundExceptionWhenRequestNotFound() {
        assertThrows(NotFoundException.class, () -> itemRequestService.findItemRequestById(999L));
    }

    @Test
    public void testFindAllByRequestorId() {
        ItemRequestDtoCreate dto1 = new ItemRequestDtoCreate();
        dto1.setDescription("Нужна дрель");
        itemRequestService.createItemRequest(dto1, requestorId);

        ItemRequestDtoCreate dto2 = new ItemRequestDtoCreate();
        dto2.setDescription("Нужен молоток");
        itemRequestService.createItemRequest(dto2, requestorId);

        List<ItemRequestDto> requests = (List<ItemRequestDto>) itemRequestService.findAllByRequestorId(requestorId);

        assertNotNull(requests);
        assertEquals(2, requests.size());

        assertTrue(requests.get(0).getCreated().isAfter(requests.get(1).getCreated()));
    }

    @Test
    public void testFindAllOtherRequests() {
        Long otherUserId = createUser("Иван", "ivan@mail.ru");

        ItemRequestDtoCreate dto1 = new ItemRequestDtoCreate();
        dto1.setDescription("Нужна дрель");
        itemRequestService.createItemRequest(dto1, requestorId);

        ItemRequestDtoCreate dto2 = new ItemRequestDtoCreate();
        dto2.setDescription("Нужен молоток");
        itemRequestService.createItemRequest(dto2, otherUserId);

        List<ItemRequestDto> otherRequests = (List<ItemRequestDto>) itemRequestService.findAllOtherRequests(requestorId);

        assertNotNull(otherRequests);
        assertEquals(1, otherRequests.size());
        assertEquals("Нужен молоток", otherRequests.get(0).getDescription());
        assertEquals(otherUserId, otherRequests.get(0).getRequestorId());
    }

    @Test
    public void testOrderByCreatedDesc() {
        ItemRequestDtoCreate dto1 = new ItemRequestDtoCreate();
        dto1.setDescription("Первый запрос");
        itemRequestService.createItemRequest(dto1, requestorId);

        ItemRequestDtoCreate dto2 = new ItemRequestDtoCreate();
        dto2.setDescription("Второй запрос");
        itemRequestService.createItemRequest(dto2, requestorId);

        List<ItemRequestDto> requests = (List<ItemRequestDto>) itemRequestService.findAllByRequestorId(requestorId);

        assertNotNull(requests);
        assertEquals(2, requests.size());
        assertTrue(requests.get(0).getCreated().isAfter(requests.get(1).getCreated()));
    }
}