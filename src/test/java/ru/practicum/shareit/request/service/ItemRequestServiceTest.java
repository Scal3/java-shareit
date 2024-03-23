package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.ShareItConfig;
import ru.practicum.shareit.exception.exceptionimp.NotFoundException;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Test
    void createRequest_normal_case_then_return_ItemRequestDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemRequestService itemRequestService = new ItemRequestService(
                itemRequestRepositoryMock, userRepositoryMock, mapper);

        User user = new User();
        user.setId(1);

        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("CreateItemRequestDto description");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setUser(user);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRequestRepositoryMock.save(Mockito.any()))
                .thenReturn(itemRequest);

        ItemRequestDto itemRequestDto = itemRequestService.createRequest(user.getId(), dto);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());

        Mockito.verify(itemRequestRepositoryMock, Mockito.times(1))
                .save(any(ItemRequest.class));
    }

    @Test
    void createRequest_user_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemRequestService itemRequestService = new ItemRequestService(
                itemRequestRepositoryMock, userRepositoryMock, mapper);

        User user = new User();
        user.setId(1000);

        CreateItemRequestDto dto = new CreateItemRequestDto();
        dto.setDescription("CreateItemRequestDto description");

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.createRequest(user.getId(), dto));

        Mockito.verify(itemRequestRepositoryMock, Mockito.times(0))
                .save(any(ItemRequest.class));
    }

    @Test
    void getUserRequestsById_normal_case_then_return_list_of_ItemRequestWithItemsDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemRequestService itemRequestService = new ItemRequestService(
                itemRequestRepositoryMock, userRepositoryMock, mapper);

        User user = new User();
        user.setId(1);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("CreateItemRequestDto description");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setUser(user);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRequestRepositoryMock.findAllByUserOrderByCreatedDesc(Mockito.any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestWithItemsDto> itemsDtos =
                itemRequestService.getUserRequestsById(user.getId());

        assertEquals(itemRequest.getId(), itemsDtos.get(0).getId());
        assertEquals(itemRequest.getCreated(), itemsDtos.get(0).getCreated());
        assertEquals(itemRequest.getDescription(), itemsDtos.get(0).getDescription());
    }

    @Test
    void getUserRequestsById_user_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemRequestService itemRequestService = new ItemRequestService(
                itemRequestRepositoryMock, userRepositoryMock, mapper);

        User user = new User();
        user.setId(1000);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getUserRequestsById(user.getId()));
    }

    @Test
    void getAllUsersRequests_normal_case_then_return_list_of_ItemRequestWithItemsDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemRequestService itemRequestService = new ItemRequestService(
                itemRequestRepositoryMock, userRepositoryMock, mapper);

        User user = new User();
        user.setId(1);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("CreateItemRequestDto description");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setUser(new User());

        Mockito
                .when(itemRequestRepositoryMock
                        .findAllByUserIdNotOrderByCreatedDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestWithItemsDto> itemsDtos =
                itemRequestService.getAllUsersRequests(user.getId(), 0 , 15);

        assertEquals(itemRequest.getId(), itemsDtos.get(0).getId());
        assertEquals(itemRequest.getCreated(), itemsDtos.get(0).getCreated());
        assertEquals(itemRequest.getDescription(), itemsDtos.get(0).getDescription());
    }

    @Test
    void getOneRequestById_normal_case_then_return_ItemRequestWithItemsDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemRequestService itemRequestService = new ItemRequestService(
                itemRequestRepositoryMock, userRepositoryMock, mapper);

        User user = new User();
        user.setId(1);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setDescription("CreateItemRequestDto description");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setUser(user);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRequestRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest));

        ItemRequestWithItemsDto itemsDto =
                itemRequestService.getOneRequestById(user.getId(), itemRequest.getId());

        assertEquals(itemRequest.getId(), itemsDto.getId());
        assertEquals(itemRequest.getCreated(), itemsDto.getCreated());
        assertEquals(itemRequest.getDescription(), itemsDto.getDescription());
    }

    @Test
    void getOneRequestById_user_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemRequestService itemRequestService = new ItemRequestService(
                itemRequestRepositoryMock, userRepositoryMock, mapper);

        User user = new User();
        user.setId(1000);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getOneRequestById(user.getId(), 1));
    }

    @Test
    void getOneRequestById_itemRequest_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemRequestService itemRequestService = new ItemRequestService(
                itemRequestRepositoryMock, userRepositoryMock, mapper);

        User user = new User();
        user.setId(1);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRequestRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getOneRequestById(user.getId(), 1000));
    }
}