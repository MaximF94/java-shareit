package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ResponseEntity<ItemRequestDtoCreate> createItemRequest(@RequestBody ItemRequestDtoCreate dto,
                                                                  @RequestHeader(USER_ID_HEADER) Long requestorId) {

        return ResponseEntity.ok(itemRequestService.createItemRequest(dto, requestorId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequestById(@PathVariable Long requestId) {
        return ResponseEntity.ok(itemRequestService.findItemRequestById(requestId));
    }


    @GetMapping
    public ResponseEntity<Collection<ItemRequestDto>> getUserRequests(@RequestHeader(USER_ID_HEADER) Long requestorId) {
        return ResponseEntity.ok(itemRequestService.findAllByRequestorId(requestorId));
    }


    @GetMapping("/all")
    public ResponseEntity<Collection<ItemRequestDto>> getAllRequests(@RequestHeader(USER_ID_HEADER) Long requestorId) {
        return ResponseEntity.ok(itemRequestService.findAllOtherRequests(requestorId));
    }
}
