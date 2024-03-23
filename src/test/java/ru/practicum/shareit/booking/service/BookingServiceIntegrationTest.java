package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.BookingSearchState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
class BookingServiceIntegrationTest {

    private static final long INITIAL_OWNER_ID = 1;

    private static final long INITIAL_BOOKER_ID = 2;

    private static final long INITIAL_ITEM_ID = 1;

    private static final long INITIAL_BOOKING_ID = 1;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final BookingService bookingService;

    @BeforeEach
    void setup() {
        User owner = createInitialUser(INITIAL_OWNER_ID, "user1", "user1@mail.ru");
        User booker = createInitialUser(INITIAL_BOOKER_ID, "user2", "user2@mail.ru");

        Item item = createInitialItem(
                INITIAL_ITEM_ID, "item", "item description", true, owner);

        createInitialBooking(
                INITIAL_BOOKING_ID, booker, item, LocalDateTime.now(),
                LocalDateTime.now().plusDays(2));
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
        booking.setStatus(BookingStatus.WAITING);
        booking.setBookingDateStart(start);
        booking.setBookingDateEnd(end);

        return bookingRepository.save(booking);
    }

    @Test
    void createBooking() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(INITIAL_ITEM_ID);
        dto.setStart(LocalDateTime.now());
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto bookingDto = bookingService.createBooking(INITIAL_BOOKER_ID, dto);

        assertEquals(2, bookingDto.getId());
        assertEquals(dto.getStart(), bookingDto.getStart());
        assertEquals(dto.getEnd(), bookingDto.getEnd());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
        assertEquals(INITIAL_BOOKER_ID, bookingDto.getBooker().getId());
        assertEquals(INITIAL_ITEM_ID, bookingDto.getItem().getId());
    }

    @Test
    void approveBooking() {
        BookingDto bookingDto =
                bookingService.approveBooking(INITIAL_OWNER_ID, INITIAL_BOOKING_ID, true);

        assertEquals(INITIAL_BOOKING_ID, bookingDto.getId());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
        assertEquals(INITIAL_BOOKER_ID, bookingDto.getBooker().getId());
        assertEquals(INITIAL_ITEM_ID, bookingDto.getItem().getId());
    }

    @Test
    void getBookingById() {
        BookingDto bookingDto =
                bookingService.getBookingById(INITIAL_OWNER_ID, INITIAL_BOOKING_ID);

        assertEquals(INITIAL_BOOKING_ID, bookingDto.getId());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
        assertEquals(INITIAL_BOOKER_ID, bookingDto.getBooker().getId());
        assertEquals(INITIAL_ITEM_ID, bookingDto.getItem().getId());
    }

    @Test
    void getAllUserBooking() {
        List<BookingDto> bookingDtos =
                bookingService.getAllUserBooking(
                        INITIAL_BOOKER_ID, BookingSearchState.ALL.name(), 0, 15);

        assertEquals(INITIAL_BOOKING_ID, bookingDtos.get(0).getId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
        assertEquals(INITIAL_BOOKER_ID, bookingDtos.get(0).getBooker().getId());
        assertEquals(INITIAL_ITEM_ID, bookingDtos.get(0).getItem().getId());
    }

    @Test
    void getAllOwnerBooking() {
        List<BookingDto> bookingDtos =
                bookingService.getAllOwnerBooking(
                        INITIAL_OWNER_ID, BookingSearchState.ALL.name(), 0, 15);

        assertEquals(INITIAL_BOOKING_ID, bookingDtos.get(0).getId());
        assertEquals(BookingStatus.WAITING, bookingDtos.get(0).getStatus());
        assertEquals(INITIAL_BOOKER_ID, bookingDtos.get(0).getBooker().getId());
        assertEquals(INITIAL_ITEM_ID, bookingDtos.get(0).getItem().getId());
    }
}