package ru.practicum.shareit.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;

@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @Valid @RequestBody ItemRequestDtoCreate dto,
            @RequestHeader(USER_ID_HEADER) @Positive Long userId) {

        return itemRequestClient.createItemRequest(dto, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @PathVariable @Positive Long requestId) {

        return itemRequestClient.getItemRequestById(requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId) {

        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId) {

        return itemRequestClient.getAllRequests(userId);
    }
}
