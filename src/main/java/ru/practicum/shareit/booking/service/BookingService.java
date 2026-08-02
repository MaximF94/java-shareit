package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {

    BookingDto createBooking(BookingCreateDto bookingCreateDto, Long bookerId);

    BookingDto approveBooking(Long bookingId, Boolean approved, Long userId);

    BookingDto findBookingById(Long bookingId, Long userId);

    Collection<BookingDto> findAllByBooker(String state, Long userId);

    Collection<BookingDto> findAllByOwner(String state, Long userId);
}
