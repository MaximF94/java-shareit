package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;


@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable long itemId) {
        return ResponseEntity.ok(itemService.getItem(itemId));
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> getAll(@RequestHeader(USER_ID_HEADER) Long ownerId) {
        return ResponseEntity.ok(itemService.getAllItemsFromUser(ownerId));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> searchItems(
            @RequestParam String text) {

        return ResponseEntity.ok(itemService.searchItems(text));
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody ItemDto dto,
                                              @RequestHeader(USER_ID_HEADER) Long ownerId) {

        return ResponseEntity.ok(itemService.createItem(dto, ownerId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@PathVariable Long itemId,
                                          @RequestBody ItemDto dto,
                                          @RequestHeader(USER_ID_HEADER) Long ownerId) {

        return ResponseEntity.ok(itemService.updateItem(dto, itemId, ownerId));
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }
}
