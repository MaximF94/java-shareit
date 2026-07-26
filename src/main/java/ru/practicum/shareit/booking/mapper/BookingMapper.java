package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public class BookingMapper {

    public static BookingDto map(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new ItemDtoShort(booking.getItem().getId(), booking.getItem().getName()),
                new UserDtoShort(booking.getBooker().getId(), booking.getBooker().getName()),
                booking.getStatus()
        );
    }

    public static Booking map(BookingDto bookingDto, Item item, User booker) {

        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                bookingDto.getStatus()
        );
    }

    public static Collection<BookingDto> map(Collection<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::map)
                .toList();
    }
}
