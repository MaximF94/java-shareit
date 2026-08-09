package ru.practicum.shareit;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DtoJsonTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerializeUserDto() throws Exception {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");


        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Иван\"");
        assertThat(json).contains("\"email\":\"ivan@mail.ru\"");
    }

    @Test
    void testDeserializeUserDto() throws Exception {

        String json = "{\"id\":1,\"name\":\"Иван\",\"email\":\"ivan@mail.ru\"}";

        UserDto dto = objectMapper.readValue(json, UserDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Иван");
        assertThat(dto.getEmail()).isEqualTo("ivan@mail.ru");
    }

    @Test
    void testSerializeItemDto() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Дрель");
        dto.setDescription("Мощная дрель");
        dto.setAvailable(true);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Дрель\"");
        assertThat(json).contains("\"description\":\"Мощная дрель\"");
        assertThat(json).contains("\"available\":true");
    }

    @Test
    void testSerializeBookingCreateDto() throws Exception {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2026, 8, 10, 10, 0));
        dto.setEnd(LocalDateTime.of(2026, 8, 12, 18, 0));

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("2026-08-10T10:00:00");
        assertThat(json).contains("2026-08-12T18:00:00");
    }

    @Test
    void shouldDeserializeBookingCreateDto() throws Exception {

        String json = "{\"itemId\":1,\"start\":\"2026-08-10T10:00:00\",\"end\":\"2026-08-12T18:00:00\"}";

        BookingCreateDto dto = objectMapper.readValue(json, BookingCreateDto.class);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 8, 10, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 8, 12, 18, 0));
    }

    @Test
    void shouldSerializeBookingDto() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2026, 8, 10, 10, 0));
        dto.setEnd(LocalDateTime.of(2026, 8, 12, 18, 0));

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("2026-08-10T10:00:00");
        assertThat(json).contains("2026-08-12T18:00:00");
    }
}
