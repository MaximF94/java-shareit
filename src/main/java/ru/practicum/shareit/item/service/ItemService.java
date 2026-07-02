package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto getItem(Long id);

    Collection<ItemDto> getAllItemsFromUser(Long ownerId);

    ItemDto createItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId);

    void deleteItem(Long itemId);

    Collection<ItemDto> searchItems(String text);
}
