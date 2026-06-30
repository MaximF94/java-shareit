package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.Collections;


@RestController
@RequestMapping("/items")
public class ItemController {

    ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable long itemId) {
        Item item = itemService.getItem(itemId);
        ItemDto itemDto = ItemMapper.map(item);
        return ResponseEntity.ok(itemDto);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> getAll(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        Collection<Item> items = itemService.getAllItemsFromUser(ownerId);
        Collection<ItemDto> dtos = ItemMapper.map(items);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> searchItems(
            @RequestParam String text) {

        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        Collection<Item> items = itemService.searchItems(text);
        Collection<ItemDto> dtos = ItemMapper.map(items);
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody ItemDto dto,
                                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        Item item = ItemMapper.map(dto);
        Item createdItem = itemService.createItem(item, ownerId);
        ItemDto createdItemDto = ItemMapper.map(createdItem);
        return ResponseEntity.ok(createdItemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@PathVariable Long itemId,
                                          @RequestBody ItemDto dto,
                                          @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        dto.setId(itemId);
        Item updatedItem = itemService.updateItem(ItemMapper.map(dto), ownerId);
        ItemDto updatedItemDto = ItemMapper.map(updatedItem);
        return ResponseEntity.ok(updatedItemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }
}
