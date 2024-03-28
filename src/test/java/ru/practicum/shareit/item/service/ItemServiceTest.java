package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.ShareItConfig;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.exceptionimp.BadRequestException;
import ru.practicum.shareit.exception.exceptionimp.ForbiddenException;
import ru.practicum.shareit.exception.exceptionimp.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepositoryMock;

    @Mock
    private CommentRepository commentRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private BookingRepository bookingRepositoryMock;

    @Mock
    private ItemRequestRepository itemRequestRepositoryMock;

    @Test
    void createItem_normal_case_then_return_ItemDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemService itemService = new ItemService(
                itemRepositoryMock, commentRepositoryMock, userRepositoryMock,
                bookingRepositoryMock, itemRequestRepositoryMock, mapper);

        User owner = new User();
        owner.setId(1);
        owner.setName("owner");

        CreateItemDto dto = new CreateItemDto();
        dto.setName("item");
        dto.setDescription("new item description");
        dto.setAvailable(true);

        Item item = new Item();
        item.setId(1);
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(owner);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(owner));

        Mockito
                .when(itemRepositoryMock.save(any()))
                .thenReturn(item);

        ItemDto itemDto = itemService.createItem(owner.getId(), new CreateItemDto());

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(),itemDto.getDescription());
        assertEquals(item.isAvailable(), itemDto.isAvailable());
        assertNull(itemDto.getRequestId());

        Mockito.verify(itemRepositoryMock, Mockito.times(1))
                .save(any(Item.class));
    }

    @Test
    void createItem_case_creating_for_item_request_then_return_ItemDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemService itemService = new ItemService(
                itemRepositoryMock, commentRepositoryMock, userRepositoryMock,
                bookingRepositoryMock, itemRequestRepositoryMock, mapper);

        User owner = new User();
        owner.setId(1);
        owner.setName("owner");

        User booker = new User();
        booker.setId(2);

        ItemRequest request = new ItemRequest();
        request.setId(1);
        request.setDescription("Item request description");
        request.setUser(booker);
        request.setCreated(LocalDateTime.now());

        CreateItemDto dto = new CreateItemDto();
        dto.setName("item");
        dto.setDescription("new item description");
        dto.setAvailable(true);
        dto.setRequestId(request.getId());

        Item item = new Item();
        item.setId(1);
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(owner);
        item.setRequest(request);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(owner));

        Mockito
                .when(itemRequestRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(request));

        Mockito
                .when(itemRepositoryMock.save(any()))
                .thenReturn(item);

        ItemDto itemDto = itemService.createItem(owner.getId(), dto);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(),itemDto.getDescription());
        assertEquals(item.isAvailable(), itemDto.isAvailable());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());

        Mockito.verify(itemRepositoryMock, Mockito.times(1))
                .save(any(Item.class));
    }

    @Test
    void createItem_user_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemService itemService = new ItemService(
                itemRepositoryMock, commentRepositoryMock, userRepositoryMock,
                bookingRepositoryMock, itemRequestRepositoryMock, mapper);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.createItem(1000, new CreateItemDto()));

        Mockito.verify(itemRepositoryMock, Mockito.times(0))
                .save(any(Item.class));
    }

    @Test
    void updateItem_normal_case_then_return_ItemDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemService itemService = new ItemService(
                itemRepositoryMock, commentRepositoryMock, userRepositoryMock,
                bookingRepositoryMock, itemRequestRepositoryMock, mapper);

        User owner = new User();
        owner.setId(1);
        owner.setName("owner");

        UpdateItemDto dto = new UpdateItemDto();
        dto.setName("updated item");
        dto.setDescription("updated item description");
        dto.setAvailable(true);
        dto.setItemId(1L);
        dto.setUserId(owner.getId());

        Item item = new Item();
        item.setId(1);
        item.setName("item");
        item.setDescription("new item description");
        item.setAvailable(true);
        item.setOwner(owner);

        Item updatedItem = new Item();
        updatedItem.setId(1);
        updatedItem.setName(dto.getName());
        updatedItem.setDescription(dto.getDescription());
        updatedItem.setAvailable(dto.getAvailable());
        updatedItem.setOwner(owner);

        Mockito
                .when(userRepositoryMock.findById(Mockito.any()))
                .thenReturn(Optional.of(owner));

        Mockito
                .when(itemRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(itemRepositoryMock.save(any()))
                .thenReturn(updatedItem);

        ItemDto itemDto = itemService.updateItem(dto);

        assertEquals(updatedItem.getId(), itemDto.getId());
        assertEquals(updatedItem.getName(), itemDto.getName());
        assertEquals(updatedItem.getDescription(),itemDto.getDescription());
        assertEquals(updatedItem.isAvailable(), itemDto.isAvailable());
        assertNull(itemDto.getRequestId());

        Mockito.verify(itemRepositoryMock, Mockito.times(1))
                .save(any(Item.class));
    }

    @Test
    void updateItem_user_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemService itemService = new ItemService(
                itemRepositoryMock, commentRepositoryMock, userRepositoryMock,
                bookingRepositoryMock, itemRequestRepositoryMock, mapper);

        User owner = new User();
        owner.setId(1000);
        owner.setName("owner");

        UpdateItemDto dto = new UpdateItemDto();
        dto.setName("updated item");
        dto.setDescription("updated item description");
        dto.setAvailable(true);
        dto.setItemId(1L);
        dto.setUserId(owner.getId());

        Mockito
                .when(userRepositoryMock.findById(Mockito.any()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(dto));

        Mockito.verify(itemRepositoryMock, Mockito.times(0))
                .save(any(Item.class));
    }

    @Test
    void updateItem_user_is_not_owner_then_throw_ForbiddenException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemService itemService = new ItemService(
                itemRepositoryMock, commentRepositoryMock, userRepositoryMock,
                bookingRepositoryMock, itemRequestRepositoryMock, mapper);

        User owner = new User();
        owner.setId(1);
        owner.setName("owner");

        User randomUser = new User();
        randomUser.setId(2);
        randomUser.setName("random user");

        UpdateItemDto dto = new UpdateItemDto();
        dto.setName("updated item");
        dto.setDescription("updated item description");
        dto.setAvailable(true);
        dto.setItemId(1L);
        dto.setUserId(randomUser.getId());

        Item item = new Item();
        item.setId(1);
        item.setName("item");
        item.setDescription("new item description");
        item.setAvailable(true);
        item.setOwner(owner);

        Item updatedItem = new Item();
        updatedItem.setId(1);
        updatedItem.setName(dto.getName());
        updatedItem.setDescription(dto.getDescription());
        updatedItem.setAvailable(dto.getAvailable());
        updatedItem.setOwner(owner);

        Mockito
                .when(userRepositoryMock.findById(Mockito.any()))
                .thenReturn(Optional.of(randomUser));

        Mockito
                .when(itemRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class, () -> itemService.updateItem(dto));

        Mockito.verify(itemRepositoryMock, Mockito.times(0))
                .save(any(Item.class));
    }

    @Test
    void getOneItemById_normal_case_then_return_ItemDtoWithBooking() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemService itemService = new ItemService(
                itemRepositoryMock, commentRepositoryMock, userRepositoryMock,
                bookingRepositoryMock, itemRequestRepositoryMock, mapper);

        User user = new User();
        user.setId(1);

        Item item = new Item();
        item.setId(1);
        item.setName("item");
        item.setDescription("new item description");
        item.setAvailable(true);
        item.setOwner(new User());

        Mockito
                .when(itemRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));


        ItemDtoWithBooking itemDto = itemService.getOneItemById(user.getId(), item.getId());

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(),itemDto.getDescription());
        assertEquals(item.isAvailable(), itemDto.isAvailable());
    }

    @Test
    void getOneItemById_item_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemService itemService = new ItemService(
                itemRepositoryMock, commentRepositoryMock, userRepositoryMock,
                bookingRepositoryMock, itemRequestRepositoryMock, mapper);

        User user = new User();
        user.setId(1);

        Mockito
                .when(itemRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.getOneItemById(user.getId(), 1000));
    }

    @Test
    void getOwnersItems_normal_case_then_return_list_of_ItemDtoWithBooking() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemService itemService = new ItemService(
                itemRepositoryMock, commentRepositoryMock, userRepositoryMock,
                bookingRepositoryMock, itemRequestRepositoryMock, mapper);

        User owner = new User();
        owner.setId(1);
        owner.setName("owner");

        Item item = new Item();
        item.setId(1);
        item.setName("item");
        item.setDescription("new item description");
        item.setAvailable(true);
        item.setOwner(owner);

        Mockito
                .when(itemRepositoryMock.findAllByOwnerIdWithComments(Mockito.anyLong(), any()))
                .thenReturn(List.of(item));

        List<ItemDtoWithBooking> itemDtos =
                itemService.getOwnersItems(owner.getId(), 0, 15);

        assertEquals(item.getId(), itemDtos.get(0).getId());
        assertEquals(item.getName(), itemDtos.get(0).getName());
        assertEquals(item.getDescription(),itemDtos.get(0).getDescription());
        assertEquals(item.isAvailable(), itemDtos.get(0).isAvailable());
    }

    @Test
    void getAvailableItemsBySearchString_normal_case_then_return_list_of_ItemDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemService itemService = new ItemService(
                itemRepositoryMock, commentRepositoryMock, userRepositoryMock,
                bookingRepositoryMock, itemRequestRepositoryMock, mapper);

        User owner = new User();
        owner.setId(1);
        owner.setName("owner");

        Item item = new Item();
        item.setId(1);
        item.setName("item");
        item.setDescription("new item description");
        item.setAvailable(true);
        item.setOwner(owner);

        Mockito
                .when(itemRepositoryMock.findByAvailableAndKeyword(Mockito.anyString(), any()))
                .thenReturn(List.of(item));

        List<ItemDto> itemDtos =
                itemService.getAvailableItemsBySearchString(item.getName(), 0, 15);

        assertEquals(item.getId(), itemDtos.get(0).getId());
        assertEquals(item.getName(), itemDtos.get(0).getName());
        assertEquals(item.getDescription(),itemDtos.get(0).getDescription());
        assertEquals(item.isAvailable(), itemDtos.get(0).isAvailable());
    }

    @Test
    void getAvailableItemsBySearchString_searchString_is_blank_then_return_empty_list() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemService itemService = new ItemService(
                itemRepositoryMock, commentRepositoryMock, userRepositoryMock,
                bookingRepositoryMock, itemRequestRepositoryMock, mapper);

        Mockito
                .when(itemRepositoryMock.findByAvailableAndKeyword(Mockito.anyString(), any()))
                .thenReturn(Collections.emptyList());

        List<ItemDto> itemDtos =
                itemService.getAvailableItemsBySearchString("lala", 0, 15);

        assertEquals(0, itemDtos.size());
    }

    @Test
    void createComment_normal_case_then_return_CommentDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemService itemService = new ItemService(
                itemRepositoryMock, commentRepositoryMock, userRepositoryMock,
                bookingRepositoryMock, itemRequestRepositoryMock, mapper);

        CreateCommentDto dto = new CreateCommentDto();
        dto.setText("new comment");

        User user = new User();
        user.setId(1);
        user.setName("user");

        Item item = new Item();
        item.setId(1);
        item.setName("item");
        item.setDescription("new item description");
        item.setAvailable(true);
        item.setOwner(new User());

        Comment comment = new Comment();
        comment.setId(1);
        comment.setText(dto.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setUser(user);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepositoryMock.findById(any()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(bookingRepositoryMock
                        .findAllByUserIdAndItemIdAndStatusAndBookingDateEndBefore(
                                Mockito.anyLong(), Mockito.anyLong(), any(), any()))
                .thenReturn(List.of(new Booking()));

        Mockito
                .when(commentRepositoryMock.save(any()))
                .thenReturn(comment);

        CommentDto commentDto = itemService.createComment(user.getId(), item.getId(), dto);

        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(commentDto.getCreated(),comment.getCreated());
        assertEquals(commentDto.getAuthorName(), comment.getUser().getName());

        Mockito.verify(commentRepositoryMock, Mockito.times(1))
                .save(any(Comment.class));
    }

    @Test
    void createComment_user_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemService itemService = new ItemService(
                itemRepositoryMock, commentRepositoryMock, userRepositoryMock,
                bookingRepositoryMock, itemRequestRepositoryMock, mapper);

        CreateCommentDto dto = new CreateCommentDto();
        dto.setText("new comment");

        User user = new User();
        user.setId(1000);
        user.setName("user");

        Item item = new Item();
        item.setId(1);
        item.setName("item");
        item.setDescription("new item description");
        item.setAvailable(true);
        item.setOwner(new User());

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(user.getId(), item.getId(), dto));

        Mockito.verify(commentRepositoryMock, Mockito.times(0))
                .save(any(Comment.class));
    }

    @Test
    void createComment_item_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemService itemService = new ItemService(
                itemRepositoryMock, commentRepositoryMock, userRepositoryMock,
                bookingRepositoryMock, itemRequestRepositoryMock, mapper);

        CreateCommentDto dto = new CreateCommentDto();
        dto.setText("new comment");

        User user = new User();
        user.setId(1);
        user.setName("user");

        Item item = new Item();
        item.setId(1);
        item.setName("item");
        item.setDescription("new item description");
        item.setAvailable(true);
        item.setOwner(new User());

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepositoryMock.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(user.getId(), item.getId(), dto));

        Mockito.verify(commentRepositoryMock, Mockito.times(0))
                .save(any(Comment.class));
    }

    @Test
    void createComment_booking_is_not_found_then_throw_BadRequestException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        ItemService itemService = new ItemService(
                itemRepositoryMock, commentRepositoryMock, userRepositoryMock,
                bookingRepositoryMock, itemRequestRepositoryMock, mapper);

        CreateCommentDto dto = new CreateCommentDto();
        dto.setText("new comment");

        User user = new User();
        user.setId(1);
        user.setName("user");

        Item item = new Item();
        item.setId(1);
        item.setName("item");
        item.setDescription("new item description");
        item.setAvailable(true);
        item.setOwner(new User());

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepositoryMock.findById(any()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(bookingRepositoryMock
                        .findAllByUserIdAndItemIdAndStatusAndBookingDateEndBefore(
                                Mockito.anyLong(), Mockito.anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class,
                () -> itemService.createComment(user.getId(), item.getId(), dto));

        Mockito.verify(commentRepositoryMock, Mockito.times(0))
                .save(any(Comment.class));
    }
}