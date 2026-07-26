package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto map(Item item) {

        List<CommentDto> commentDtos = item.getComments() != null
                ? item.getComments().stream()
                .map(CommentMapper::map)
                .collect(Collectors.toList())
                : null;

        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId(),
                null,
                null,
                commentDtos);
    }

    public static ItemDto mapWithDates(Item item, LocalDateTime lastBooking, LocalDateTime nextBooking) {

        List<CommentDto> commentDtos = item.getComments() != null
                ? item.getComments().stream()
                .map(CommentMapper::map)
                .collect(Collectors.toList())
                : null;

        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId(),
                lastBooking,
                nextBooking,
                commentDtos);
    }


    public static Item map(ItemDto itemDto) {

        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                null);
    }



    public static Collection<ItemDto> map(Collection<Item> items) {
        return items.stream()
                .map(ItemMapper::map)
                .toList();
    }
}
