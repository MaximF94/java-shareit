package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Service
public class ItemServiceImpl implements ItemService {

    ItemRepository itemRepository;
    UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Item getItem(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> {
            return new NotFoundException("Вещь не найдена");
        });
    }

    @Override
    public Collection<Item> getAllItemsFromUser(Long ownerId) {
        return itemRepository.getAllFromUser(ownerId);
    }

    @Override
    public Collection<Item> searchItems(String text) {
        return itemRepository.searchItems(text);
    }

    @Override
    public Item createItem(Item item, Long ownerId) {

        item.setOwner(ownerId);

        Set<String> errors = validate(item);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        boolean userExists = userRepository.findById(item.getOwner()).isPresent();
        if (!userExists) {
            throw new NotFoundException("пользователь с id " + item.getOwner() + " не найден");
        }

        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Item item, Long ownerId) {

        item.setOwner(ownerId);

        if (item.getId() == null || item.getId() == 0) {
            throw new ValidationException("Id должен быть указан");
        }

        Item existingItem = itemRepository.findById(item.getId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!Objects.equals(item.getOwner(), existingItem.getOwner())) {
            throw new AccessDeniedException("Несанкционированное редактирование вещи");
        }

        Set<String> errors = validate(existingItem);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        boolean userExists = userRepository.findById(item.getOwner()).isPresent();
        if (!userExists) {
            throw new NotFoundException("пользователь с id " + item.getOwner() + " не найден");
        }

        updateItemFields(existingItem, item);

        itemRepository.save(existingItem);

        return existingItem;
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteItem(itemId);
    }

    private void updateItemFields(Item oldItem, Item newItem) {

        if (!(newItem.getAvailable() == null)) {
            oldItem.setAvailable(newItem.getAvailable());
        }

        if (!(newItem.getDescription() == null)) {
            oldItem.setDescription(newItem.getDescription());
        }

        if (!(newItem.getName() == null)) {
            oldItem.setName(newItem.getName());
        }

    }

    private Set<String> validate(Item item) {

        Set<String> errors = new HashSet<>();

        if (item.getName().isBlank()) {
            errors.add("название не может быть пустым");
        }

        if (item.getAvailable() == null) {
            errors.add("параметр доступности вещи должен быть указан");
        }

        if (item.getDescription() == null) {
            errors.add("описание не может быть пустым");
        }

        if (item.getOwner() == null) {
            errors.add("владелец должен быть указан");
        }

        return errors;
    }
}
