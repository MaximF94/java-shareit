package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestBody BookingCreateDto dto,
                                                    @RequestHeader(USER_ID_HEADER) Long userId) {

        return ResponseEntity.ok(bookingService.createBooking(dto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader(USER_ID_HEADER) Long userId) {

        return ResponseEntity.ok(bookingService.approveBooking(bookingId, approved, userId));

    }


    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long bookingId,
                                                 @RequestHeader(USER_ID_HEADER) Long userId) {

        return ResponseEntity.ok(bookingService.findBookingById(bookingId, userId));
    }


    @GetMapping
    public ResponseEntity<Collection<BookingDto>> getBookings(@RequestParam(defaultValue = "ALL") String state,
                                                              @RequestHeader(USER_ID_HEADER) Long userId) {

        return ResponseEntity.ok(bookingService.findAllByBooker(state, userId));
    }


    @GetMapping("/owner")
    public ResponseEntity<Collection<BookingDto>> getOwnerBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        return ResponseEntity.ok(bookingService.findAllByOwner(state, userId));
    }
}
