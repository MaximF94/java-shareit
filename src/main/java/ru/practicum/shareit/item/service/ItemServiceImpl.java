package ru.practicum.shareit.item.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
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
    public ItemDto getItem(Long id) {

        Item item = itemRepository.findById(id).orElseThrow(() -> {
            return new NotFoundException("Вещь не найдена");
        });

        return ItemMapper.map(item);
    }

    @Override
    public Collection<ItemDto> getAllItemsFromUser(Long ownerId) {
        Collection<Item> items = itemRepository.getAllFromUser(ownerId);
        return ItemMapper.map(items);
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        Collection<Item> items = itemRepository.searchItems(text);

        return ItemMapper.map(items);

    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {

        Item item = ItemMapper.map(itemDto);

        item.setOwner(ownerId);

        validate(item);

        Item createdItem = itemRepository.save(item);

        return ItemMapper.map(createdItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {

        Item item = ItemMapper.map(itemDto);
        item.setId(itemId);
        item.setOwner(ownerId);


        if (item.getId() == null || item.getId() == 0) {
            throw new ValidationException("Id должен быть указан");
        }

        Item existingItem = findExistedItem(item);

        validate(existingItem);

        updateItemFields(existingItem, item);

        itemRepository.save(existingItem);

        return ItemMapper.map(existingItem);
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteItem(itemId);
    }

    private void updateItemFields(Item oldItem, Item newItem) {

        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }

        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }

        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }

    }

    private Set<String> checkDataField(Item item) {

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

    private Item findExistedItem(Item item) {

        Item existingItem = itemRepository.findById(item.getId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!Objects.equals(item.getOwner(), existingItem.getOwner())) {
            throw new AccessDeniedException("Несанкционированное редактирование вещи");
        }

        return existingItem;

    }

    private void validate(Item item) {

        Set<String> errors = checkDataField(item);
        if (!errors.isEmpty()) {
            throw new ValidationException(String.join(", ", errors));
        }

        boolean userExists = userRepository.findById(item.getOwner()).isPresent();
        if (!userExists) {
            throw new NotFoundException("пользователь с id " + item.getOwner() + " не найден");
        }
    }
}
