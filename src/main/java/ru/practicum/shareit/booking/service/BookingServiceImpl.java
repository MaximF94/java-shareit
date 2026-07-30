package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingRole;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.AccessDeniedException;
import ru.practicum.shareit.exeption.AccessDeniedExceptionForBooking;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;
    ItemRepository itemRepository;
    UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto createBooking(BookingCreateDto bookingCreateDto, Long bookerId) {

        Item item = itemRepository.findById(bookingCreateDto.getItemId()).orElseThrow(() -> {
            return new NotFoundException("Вещь не найдена");
        });

        User booker = userRepository.findById(bookerId).orElseThrow(() -> {
            return new NotFoundException("Владелец не найден");
        });

        validate(bookingCreateDto);

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().equals(bookerId)) {
            throw new AccessDeniedException("Владелец не может бронировать свою вещь");
        }

        Booking booking = new Booking();

        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());

        Booking createdBooking = bookingRepository.save(booking);

        return BookingMapper.map(createdBooking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long bookingId, Boolean approved, Long userId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            return new NotFoundException("Бронирование не найдено");
        });

        Item item = booking.getItem();

        if (!(item.getOwner().equals(userId))) {
            throw new AccessDeniedExceptionForBooking("Несанкционированный доступ");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Можно подтверждать только бронирования со статусом WAITING");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.save(booking);

        return BookingMapper.map(booking);
    }

    @Override
    public BookingDto findBookingById(Long bookingId, Long userId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            return new NotFoundException("Бронирование не найдено");
        });

        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().equals(userId))) {
            throw new AccessDeniedExceptionForBooking("Текущий пользователь не может видеть данное бронирование");
        }

        return BookingMapper.map(booking);
    }

    @Override
    public Collection<BookingDto> findAllByBooker(String state, Long userId) {
        return getBookingsByRole(state, userId, BookingRole.BOOKER);
    }

    @Override
    public Collection<BookingDto> findAllByOwner(String state, Long userId) {
        return getBookingsByRole(state, userId, BookingRole.OWNER);
    }

    private Collection<BookingDto> getBookingsByRole(String state, Long userId, BookingRole role) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Некорректный параметр state: " + state);
        }

        Collection<Booking> bookings;

        switch (bookingState) {
            case ALL:
                bookings = role == BookingRole.BOOKER
                        ? bookingRepository.findAllByBookerId(userId)
                        : bookingRepository.findAllByOwnerId(userId);
                break;
            case CURRENT:
                bookings = role == BookingRole.BOOKER
                        ? bookingRepository.findCurrentByBookerId(userId)
                        : bookingRepository.findCurrentByOwnerId(userId);
                break;
            case PAST:
                bookings = role == BookingRole.BOOKER
                        ? bookingRepository.findPastByBookerId(userId)
                        : bookingRepository.findPastByOwnerId(userId);
                break;
            case FUTURE:
                bookings = role == BookingRole.BOOKER
                        ? bookingRepository.findFutureByBookerId(userId)
                        : bookingRepository.findFutureByOwnerId(userId);
                break;
            case WAITING:
                bookings = role == BookingRole.BOOKER
                        ? bookingRepository.findWaitingByBookerId(userId)
                        : bookingRepository.findWaitingByOwnerId(userId);
                break;
            case REJECTED:
                bookings = role == BookingRole.BOOKER
                        ? bookingRepository.findRejectedByBookerId(userId)
                        : bookingRepository.findRejectedByOwnerId(userId);
                break;
            default:
                bookings = Collections.emptyList();
        }

        return BookingMapper.map(bookings);
    }

    private void validate(BookingCreateDto booking) {

        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new IllegalArgumentException("Дата начала и окончания бронирования должны быть заполнены");
        }

        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Дата окончания не может быть в прошлом");
        }

        if (booking.getStart().equals(booking.getEnd())) {
            throw new IllegalArgumentException("Дата окончания не должна быть равна дате начала");
        }

        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Дата начала должна быть в будущем");
        }

        if(booking.getEnd().isBefore(booking.getStart())) {
            throw new IllegalArgumentException("Дата окончания не может быть раньше даты начала");
        }

    }

}
