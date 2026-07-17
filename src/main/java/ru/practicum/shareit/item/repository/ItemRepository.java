package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> findById(Long id);

    Item save(Item item);

    Collection<Item> getAllFromUser(Long ownerId);

    void deleteItem(Long itemId);

    Collection<Item> searchItems(String text);
}
