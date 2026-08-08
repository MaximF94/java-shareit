package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreate;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    public void testCreateItemRequest() throws Exception {
        ItemRequestDtoCreate dto = new ItemRequestDtoCreate();
        dto.setDescription("Need a drill");

        ItemRequestDtoCreate created = new ItemRequestDtoCreate();
        created.setId(1L);
        created.setDescription("Need a drill");

        when(itemRequestService.createItemRequest(any(ItemRequestDtoCreate.class), anyLong())).thenReturn(created);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    public void testGetItemRequestById() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Need a drill");

        when(itemRequestService.findItemRequestById(anyLong())).thenReturn(dto);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    public void testGetUserRequests() throws Exception {
        ItemRequestDto dto1 = new ItemRequestDto();
        dto1.setId(1L);
        dto1.setDescription("Need a drill");

        ItemRequestDto dto2 = new ItemRequestDto();
        dto2.setId(2L);
        dto2.setDescription("Need a hammer");

        when(itemRequestService.findAllByRequestorId(anyLong())).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testGetAllRequests() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Need a drill");

        when(itemRequestService.findAllOtherRequests(anyLong())).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
