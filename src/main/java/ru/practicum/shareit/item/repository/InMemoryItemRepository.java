package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null || item.getId() == 0) {
           item.setId(getNextId());
        }

        items.put(item.getId(),item);
        return item;
    }

    @Override
    public Collection<Item> getAllFromUser(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null && item.getOwner().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> searchItems(String text) {

        String lowerText = text.toLowerCase().trim();

        return items.values().stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable())
                .filter(item -> {
                    boolean nameMatches = item.getName() != null &&
                            item.getName().toLowerCase().contains(lowerText);
                    boolean descriptionMatches = item.getDescription() != null &&
                            item.getDescription().toLowerCase().contains(lowerText);
                    return nameMatches || descriptionMatches;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId) {
        if (itemId != 0) {
            items.remove(itemId);
        }
    }

    private long getNextId() {
        long maxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++maxId;
    }
}
