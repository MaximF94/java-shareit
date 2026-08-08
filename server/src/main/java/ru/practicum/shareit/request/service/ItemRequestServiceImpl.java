package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;


@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ItemRequestDtoCreate createItemRequest(ItemRequestDtoCreate itemRequestDtoCreate, Long requestorId) {

        validateRequestor(requestorId);

        if (itemRequestDtoCreate.getDescription() == null || itemRequestDtoCreate.getDescription().isBlank()) {
            throw new IllegalArgumentException("Описание запроса не может быть пустым");
        }

        ItemRequest itemRequest = ItemRequestMapper.mapCreate(itemRequestDtoCreate);
        itemRequest.setRequestorId(requestorId);

        ItemRequest createdItemRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.mapCreate(createdItemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto findItemRequestById(Long id) {

        ItemRequest itemRequest = itemRequestRepository.findByIdWithItems(id).orElseThrow(() -> {
            return new NotFoundException("Запрос не найден");
        });

        return ItemRequestMapper.map(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequestDto> findAllByRequestorId(Long requestorId) {

        validateRequestor(requestorId);

        Collection<ItemRequest> itemRequests = itemRequestRepository.findAllByUserId(requestorId);
        return ItemRequestMapper.map(itemRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemRequestDto> findAllOtherRequests(Long requestorId) {

        validateRequestor(requestorId);

        Collection<ItemRequest> itemRequests = itemRequestRepository.findAllOtherRequests(requestorId);
        return ItemRequestMapper.map(itemRequests);
    }

    private void validateRequestor(Long requestorId) {
        userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + requestorId + " не найден"));
    }
}
