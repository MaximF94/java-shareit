package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

@RestController
@RequestMapping("/bookings")
@Validated
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @Valid @RequestBody BookingCreateDto dto,
            @RequestHeader(USER_ID_HEADER) @Positive Long userId) {

        return bookingClient.createBooking(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @PathVariable @Positive Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader(USER_ID_HEADER) @Positive Long userId) {

        return bookingClient.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @PathVariable @Positive Long bookingId,
            @RequestHeader(USER_ID_HEADER) @Positive Long userId) {

        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(USER_ID_HEADER) @Positive Long userId) {

        return bookingClient.getBookings(state, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader(USER_ID_HEADER) @Positive Long userId) {

        return bookingClient.getOwnerBookings(state, userId);
    }
}
