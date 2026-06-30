package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item getItem(Long id);

    Collection<Item> getAllItemsFromUser(Long ownerId);

    Item createItem(Item item, Long ownerId);

    Item updateItem(Item item, Long ownerId);

    void deleteItem(Long itemId);

    Collection<Item> searchItems(String text);
}
