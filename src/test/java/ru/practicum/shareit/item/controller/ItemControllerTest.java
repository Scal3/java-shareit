package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {

    private final ObjectMapper mapper;

    @MockBean
    private final ItemService itemServiceMock;

    private final MockMvc mvc;

    @Test
    void createItem() throws Exception {
        CreateItemDto dto = new CreateItemDto();
        dto.setName("item");
        dto.setDescription("new item description");
        dto.setAvailable(true);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName(dto.getName());
        itemDto.setDescription(dto.getDescription());
        itemDto.setAvailable(dto.getAvailable());

        when(itemServiceMock.createItem(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(itemDto.isAvailable()));
    }

    @Test
    void updateItem() throws Exception {
        UpdateItemDto dto = new UpdateItemDto();
        dto.setUserId(1L);
        dto.setItemId(1L);
        dto.setName("updated item");
        dto.setDescription("updated item description");
        dto.setAvailable(true);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName(dto.getName());
        itemDto.setDescription(dto.getDescription());
        itemDto.setAvailable(dto.getAvailable());

        when(itemServiceMock.updateItem(any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(itemDto.isAvailable()));
    }

    @Test
    void getOneItemById() throws Exception {
        ItemDtoWithBooking itemDto = new ItemDtoWithBooking();
        itemDto.setId(1);
        itemDto.setName("item");
        itemDto.setDescription("new item description");
        itemDto.setAvailable(true);

        when(itemServiceMock.getOneItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(itemDto.isAvailable()));
    }

    @Test
    void getOwnersItems() throws Exception {
        ItemDtoWithBooking itemDto = new ItemDtoWithBooking();
        itemDto.setId(1);
        itemDto.setName("item");
        itemDto.setDescription("new item description");
        itemDto.setAvailable(true);

        when(itemServiceMock.getOwnersItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(itemDto.isAvailable()));
    }

    @Test
    void getAvailableItemsBySearchString() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("item");
        itemDto.setDescription("new item description");
        itemDto.setAvailable(true);

        when(itemServiceMock.getAvailableItemsBySearchString(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .param("text", "text")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(itemDto.isAvailable()));
    }

    @Test
    void createComment() throws Exception {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setText("new comment");

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1);
        commentDto.setText(dto.getText());
        commentDto.setCreated(LocalDateTime.now());
        commentDto.setAuthorName("ItWasMeeeee");

        when(itemServiceMock.createComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(commentDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text").value(commentDto.getText()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorName").value(commentDto.getAuthorName()));
    }
}