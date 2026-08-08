package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl,
                         RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .build());
    }

    public ResponseEntity<Object> createBooking(BookingCreateDto dto, long userId) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> approveBooking(long bookingId, boolean approved, long userId) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> getBooking(long bookingId, long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookings(String state, long userId) {
        Map<String, Object> parameters = Map.of("state", state);
        return get("?state={state}", userId, parameters);
    }


    public ResponseEntity<Object> getOwnerBookings(String state, long userId) {
        Map<String, Object> parameters = Map.of("state", state);
        return get("/owner?state={state}", userId, parameters);
    }
}
