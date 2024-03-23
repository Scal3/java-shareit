package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {

    private final ObjectMapper mapper;

    @MockBean
    private final ItemRequestService itemRequestServiceMock;

    private final MockMvc mvc;

    @Test
    void createRequest() throws Exception {
        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("some request description");

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1);
        requestDto.setDescription(dto.getDescription());
        requestDto.setCreated(LocalDateTime.now());

        Mockito
                .when(itemRequestServiceMock.createRequest(anyLong(), any()))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(requestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(requestDto.getDescription()));
    }

    @Test
    void getUserRequestsById() throws Exception {
        ItemRequestWithItemsDto requestDto = new ItemRequestWithItemsDto();
        requestDto.setId(1);
        requestDto.setDescription("some request description");
        requestDto.setCreated(LocalDateTime.now());

        Mockito
                .when(itemRequestServiceMock.getUserRequestsById(anyLong()))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(requestDto.getDescription()));

    }

    @Test
    void getAllUsersRequests() throws Exception {
        ItemRequestWithItemsDto requestDto = new ItemRequestWithItemsDto();
        requestDto.setId(1);
        requestDto.setDescription("some request description");
        requestDto.setCreated(LocalDateTime.now());

        Mockito
                .when(itemRequestServiceMock.getAllUsersRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(requestDto.getDescription()));
    }

    @Test
    void getOneRequestById() throws Exception {
        ItemRequestWithItemsDto requestDto = new ItemRequestWithItemsDto();
        requestDto.setId(1);
        requestDto.setDescription("some request description");
        requestDto.setCreated(LocalDateTime.now());

        Mockito
                .when(itemRequestServiceMock.getOneRequestById(anyLong(), anyLong()))
                .thenReturn(requestDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(requestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(requestDto.getDescription()));
    }
}