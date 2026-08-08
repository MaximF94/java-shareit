package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDtoCreate createItemRequest(ItemRequestDtoCreate itemRequestDtoCreate, Long requestorId);

    ItemRequestDto findItemRequestById(Long id);

    Collection<ItemRequestDto> findAllByRequestorId(Long requestorId);

    Collection<ItemRequestDto> findAllOtherRequests(Long requestorId);
}
