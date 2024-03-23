package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceIntegrationTest {

    private static final long INITIAL_USER_ID = 1;

    private static final long INITIAL_REQUEST_ID = 1;

    private static final String INITIAL_REQUEST_DESCRIPTION = "INITIAL_REQUEST_DESCRIPTION";

    private final UserRepository userRepository;

    private final ItemRequestRepository itemRequestRepository;

    private final ItemRequestService itemRequestService;

    @BeforeEach
    void setup() {
        User user = createInitialUser(INITIAL_USER_ID, "user1", "user1@mail.ru");

        createInitialRequest(INITIAL_REQUEST_ID, INITIAL_REQUEST_DESCRIPTION, user);
    }

    private User createInitialUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return userRepository.save(user);
    }

    private ItemRequest createInitialRequest(long id, String description, User user) {
        ItemRequest request = new ItemRequest();
        request.setId(id);
        request.setDescription(description);
        request.setCreated(LocalDateTime.now());
        request.setUser(user);

        return itemRequestRepository.save(request);
    }

    @Test
    void createRequest() {
        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("i need a ps3");

        ItemRequestDto itemRequestDto = itemRequestService.createRequest(INITIAL_USER_ID, dto);

        assertEquals(2, itemRequestDto.getId());
        assertEquals(dto.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void getUserRequestsById() {
        List<ItemRequestWithItemsDto> itemRequestDtos =
                itemRequestService.getUserRequestsById(INITIAL_USER_ID);

        assertEquals(INITIAL_REQUEST_ID, itemRequestDtos.get(0).getId());
        assertEquals(INITIAL_REQUEST_DESCRIPTION, itemRequestDtos.get(0).getDescription());
    }

    @Test
    void getAllUsersRequests() {
        List<ItemRequestWithItemsDto> itemRequestDtos =
                itemRequestService.getAllUsersRequests(10, 0, 15);

        assertEquals(INITIAL_REQUEST_ID, itemRequestDtos.get(0).getId());
        assertEquals(INITIAL_REQUEST_DESCRIPTION, itemRequestDtos.get(0).getDescription());
    }

    @Test
    void getOneRequestById() {
        ItemRequestWithItemsDto itemRequestDto =
                itemRequestService.getOneRequestById(INITIAL_USER_ID, INITIAL_REQUEST_ID);

        assertEquals(INITIAL_REQUEST_ID, itemRequestDto.getId());
        assertEquals(INITIAL_REQUEST_DESCRIPTION, itemRequestDto.getDescription());
    }
}