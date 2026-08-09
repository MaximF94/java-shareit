package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void testCreateUser() throws Exception {
        UserDto dto = new UserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");

        UserDto created = new UserDto();
        created.setId(1L);
        created.setName("Иван");
        created.setEmail("ivan@mail.ru");

        when(userService.createUser(any(UserDto.class))).thenReturn(created);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.email").value("ivan@mail.ru"));
    }

    @Test
    void testGetUser() throws Exception {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");

        when(userService.getUser(anyLong())).thenReturn(dto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserDto dto1 = new UserDto();
        dto1.setId(1L);
        dto1.setName("Иван");

        UserDto dto2 = new UserDto();
        dto2.setId(2L);
        dto2.setName("Петр");

        when(userService.getAllUsers()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto dto = new UserDto();
        dto.setName("Петр");
        dto.setEmail("petr@mail.ru");

        UserDto updated = new UserDto();
        updated.setId(1L);
        updated.setName("Петр");
        updated.setEmail("petr@mail.ru");

        when(userService.updateUser(any(UserDto.class), anyLong())).thenReturn(updated);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Петр"));
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}
