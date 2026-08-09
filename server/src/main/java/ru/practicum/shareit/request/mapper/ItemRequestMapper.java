package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDtoShortForRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.request.model.ItemRequest;


import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDtoCreate mapCreate(ItemRequest itemRequest) {
        return new ItemRequestDtoCreate(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestorId(),
                itemRequest.getCreated());
    }

    public static ItemRequest mapCreate(ItemRequestDtoCreate itemRequestDtoCreate) {
        return new ItemRequest(itemRequestDtoCreate.getId(),
                itemRequestDtoCreate.getDescription(),
                itemRequestDtoCreate.getRequestorId(),
                itemRequestDtoCreate.getCreated());
    }

    public static ItemRequestDto map(ItemRequest itemRequest) {

        List<ItemDtoShortForRequest> itemDtos = itemRequest.getItems() != null
                ? itemRequest.getItems().stream()
                .map(ItemMapper::mapForRequest)
                .collect(Collectors.toList())
                : null;

        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestorId(),
                itemRequest.getCreated(),
                itemDtos);
    }


    public static Collection<ItemRequestDto> map(Collection<ItemRequest> itemRequests) {
        return itemRequests.stream().map(ItemRequestMapper::map).toList();
    }
}
