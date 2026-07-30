package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ItemServiceImpl implements ItemService {

    ItemRepository itemRepository;
    UserRepository userRepository;
    CommentRepository commentRepository;
    BookingRepository bookingRepository;


    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, CommentRepository commentRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
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

        Collection<Item> items = itemRepository.findAllByOwner(ownerId);

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        List<Object[]> lastResults = bookingRepository.findLastBookingEndsForItems(itemIds);
        Map<Long, LocalDateTime> lastMap = new HashMap<>();
        for (Object[] row : lastResults) {
            lastMap.put((Long) row[0], (LocalDateTime) row[1]);
        }

        List<Object[]> nextResults = bookingRepository.findNextBookingStartsForItems(itemIds);
        Map<Long, LocalDateTime> nextMap = new HashMap<>();
        for (Object[] row : nextResults) {
            nextMap.put((Long) row[0], (LocalDateTime) row[1]);
        }

        List<Comment> comments = commentRepository.findAllWithAuthorByItemIds(itemIds);
        Map<Long, List<Comment>> commentsMap = new HashMap<>();
        for (Comment c : comments) {
            Long itemId = c.getItem().getId();

            List<Comment> list = commentsMap.computeIfAbsent(itemId, k -> new ArrayList<>());
            list.add(c);
        }

        Collection<ItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            LocalDateTime lastDate = lastMap.get(item.getId());
            LocalDateTime nextDate = nextMap.get(item.getId());

            item.setComments(commentsMap.get(item.getId()));

            ItemDto dto = ItemMapper.mapWithDates(item, lastDate, nextDate);
            itemDtos.add(dto);
        }

        return itemDtos;
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
        itemRepository.deleteById(itemId);
    }

    @Override
    public CommentDto createComment(CommentDto dto, Long itemId, Long userId) {

        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            return new NotFoundException("Вещь не найдена");
        });

        User user = userRepository.findById(userId).orElseThrow(() -> {
            return new NotFoundException("Пользователь не найден");
        });

        List<Booking> completedBookings = bookingRepository
                .findCompletedBookingsByUserAndItem(userId, itemId);

        if (completedBookings.isEmpty()) {
            throw new IllegalArgumentException(
                    "Пользователь не арендовал эту вещь или срок аренды ещё не закончился"
            );
        }

        Comment comment = new Comment();

        comment.setItem(item);
        comment.setAuthor(user);
        comment.setText(dto.getText());
        comment.setCreated(LocalDateTime.now());

        Comment createdComment = commentRepository.save(comment);

        return CommentMapper.map(createdComment);
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
