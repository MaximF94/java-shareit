package ru.practicum.shareit;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void testCreateBooking() throws Exception {
        BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto created = new BookingDto();
        created.setId(1L);

        when(bookingService.createBooking(any(BookingCreateDto.class), anyLong())).thenReturn(created);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testApproveBooking() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);

        when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong())).thenReturn(dto);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetBooking() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);

        when(bookingService.findBookingById(anyLong(), anyLong())).thenReturn(dto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetBookings() throws Exception {

        BookingDto dto = new BookingDto();
        dto.setId(1L);

        when(bookingService.findAllByBooker(anyString(), anyLong())).thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetOwnerBookings() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);

        when(bookingService.findAllByOwner(anyString(), anyLong())).thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
