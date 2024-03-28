package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {

    private static final long INITIAL_OWNER_ID = 1;

    private static final long INITIAL_BOOKER_ID = 2;

    private static final long INITIAL_ITEM_ID = 1;

    private static final String INITIAL_ITEM_NAME = "item";

    private static final String INITIAL_ITEM_DESCRIPTION = "item description";

    private static final long INITIAL_BOOKING_ID = 1;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final ItemService itemService;

    @BeforeEach
    void setup() {
        User owner = createInitialUser(INITIAL_OWNER_ID, "user1", "user1@mail.ru");
        User booker = createInitialUser(INITIAL_BOOKER_ID, "user2", "user2@mail.ru");

        Item item = createInitialItem(
                INITIAL_ITEM_ID, INITIAL_ITEM_NAME, INITIAL_ITEM_DESCRIPTION, true, owner);

        createInitialBooking(
                INITIAL_BOOKING_ID, booker, item, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1));
    }

    private Item createInitialItem(
            long id, String name, String description, boolean available, User owner) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);

        return itemRepository.save(item);
    }

    private User createInitialUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return userRepository.save(user);
    }

    private Booking createInitialBooking(
            long id, User user, Item item, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setUser(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBookingDateStart(start);
        booking.setBookingDateEnd(end);

        return bookingRepository.save(booking);
    }

    @Test
    void createItem() {
        CreateItemDto dto = new CreateItemDto();
        dto.setName("new item");
        dto.setDescription("new item description");
        dto.setAvailable(true);

        ItemDto itemDto = itemService.createItem(INITIAL_OWNER_ID, dto);

        assertEquals(2, itemDto.getId());
        assertEquals(dto.getName(), itemDto.getName());
        assertEquals(dto.getDescription(), itemDto.getDescription());
        assertEquals(dto.getAvailable(), itemDto.isAvailable());
    }

    @Test
    void updateItem() {
        UpdateItemDto dto = new UpdateItemDto();
        dto.setItemId(INITIAL_ITEM_ID);
        dto.setUserId(INITIAL_OWNER_ID);
        dto.setName("updated item");
        dto.setDescription("updated item description");
        dto.setAvailable(true);

        ItemDto itemDto = itemService.updateItem(dto);

        assertEquals(INITIAL_ITEM_ID, itemDto.getId());
        assertEquals(dto.getName(), itemDto.getName());
        assertEquals(dto.getDescription(), itemDto.getDescription());
        assertEquals(dto.getAvailable(), itemDto.isAvailable());
    }

    @Test
    void getOneItemById() {
        ItemDtoWithBooking itemDto = itemService.getOneItemById(INITIAL_OWNER_ID, INITIAL_ITEM_ID);

        assertEquals(INITIAL_ITEM_ID, itemDto.getId());
        assertEquals(INITIAL_ITEM_NAME, itemDto.getName());
        assertEquals(INITIAL_ITEM_DESCRIPTION, itemDto.getDescription());
        assertTrue(itemDto.isAvailable());
    }

    @Test
    void getOwnersItems() {
        List<ItemDtoWithBooking> itemDtos =
                itemService.getOwnersItems(INITIAL_OWNER_ID, 0, 15);

        assertEquals(INITIAL_ITEM_ID, itemDtos.get(0).getId());
        assertEquals(INITIAL_ITEM_NAME, itemDtos.get(0).getName());
        assertEquals(INITIAL_ITEM_DESCRIPTION, itemDtos.get(0).getDescription());
        assertTrue(itemDtos.get(0).isAvailable());
    }

    @Test
    void getAvailableItemsBySearchString() {
        List<ItemDto> itemDtos =
                itemService.getAvailableItemsBySearchString(INITIAL_ITEM_NAME, 0, 15);

        assertEquals(INITIAL_ITEM_ID, itemDtos.get(0).getId());
        assertEquals(INITIAL_ITEM_NAME, itemDtos.get(0).getName());
        assertEquals(INITIAL_ITEM_DESCRIPTION, itemDtos.get(0).getDescription());
        assertTrue(itemDtos.get(0).isAvailable());
    }

    @Test
    void createComment() {
        CreateCommentDto dto = new CreateCommentDto();
        dto.setText("some comment text");

        CommentDto commentDto = itemService.createComment(INITIAL_BOOKER_ID, INITIAL_ITEM_ID, dto);

        assertEquals(1, commentDto.getId());
        assertEquals(dto.getText(), commentDto.getText());
    }
}