package ru.practicum.shareit.booking.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Test
    void findAllByUserIdOrderByBookingDateEndDesc_normal_case_then_return_list_of_Booking() {
        User userModel1 = new User();
        userModel1.setId(1);
        userModel1.setName("user1");
        userModel1.setEmail("user1@email.com");
        User owner = userRepository.save(userModel1);

        User userModel2 = new User();
        userModel2.setId(2);
        userModel2.setName("user2");
        userModel2.setEmail("user2@email.com");
        User booker = userRepository.save(userModel2);

        Item itemModel = new Item();
        itemModel.setId(1);
        itemModel.setName("item");
        itemModel.setDescription("item description");
        itemModel.setAvailable(true);
        itemModel.setOwner(owner);
        Item item = itemRepository.save(itemModel);

        Booking bookingModel1 = new Booking();
        bookingModel1.setId(1);
        bookingModel1.setBookingDateStart(LocalDateTime.now().minusDays(3));
        bookingModel1.setBookingDateEnd(LocalDateTime.now().plusDays(3));
        bookingModel1.setStatus(BookingStatus.WAITING);
        bookingModel1.setUser(booker);
        bookingModel1.setItem(item);
        bookingRepository.save(bookingModel1);

        Booking bookingModel2 = new Booking();
        bookingModel2.setId(2);
        bookingModel2.setBookingDateStart(LocalDateTime.now().plusDays(3));
        bookingModel2.setBookingDateEnd(LocalDateTime.now().plusDays(6));
        bookingModel2.setStatus(BookingStatus.WAITING);
        bookingModel2.setUser(booker);
        bookingModel2.setItem(item);
        bookingRepository.save(bookingModel2);

        List<Booking> bookings = bookingRepository
                .findAllByUserIdOrderByBookingDateEndDesc(booker.getId(), Pageable.ofSize(10));

        assertEquals(2, bookings.size());
        assertEquals(bookingModel2.getId(), bookings.get(0).getId());
        assertEquals(bookingModel1.getId(), bookings.get(1).getId());
    }

    @Test
    void findAllByUserIdOrderByBookingDateEndDesc_no_bookings_in_db_then_return_empty_list() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User booker = userRepository.save(userModel);

        List<Booking> bookings = bookingRepository
                .findAllByUserIdOrderByBookingDateEndDesc(booker.getId(), Pageable.ofSize(10));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByUserIdAndBookingDateEndBeforeOrderByBookingDateEndDesc_normal_case_then_return_list_of_Booking() {
        User userModel1 = new User();
        userModel1.setId(1);
        userModel1.setName("user1");
        userModel1.setEmail("user1@email.com");
        User owner = userRepository.save(userModel1);

        User userModel2 = new User();
        userModel2.setId(2);
        userModel2.setName("user2");
        userModel2.setEmail("user2@email.com");
        User booker = userRepository.save(userModel2);

        Item itemModel = new Item();
        itemModel.setId(1);
        itemModel.setName("item");
        itemModel.setDescription("item description");
        itemModel.setAvailable(true);
        itemModel.setOwner(owner);
        Item item = itemRepository.save(itemModel);

        Booking bookingModel1 = new Booking();
        bookingModel1.setId(1);
        bookingModel1.setBookingDateStart(LocalDateTime.now().minusDays(6));
        bookingModel1.setBookingDateEnd(LocalDateTime.now().minusDays(3));
        bookingModel1.setStatus(BookingStatus.APPROVED);
        bookingModel1.setUser(booker);
        bookingModel1.setItem(item);
        bookingRepository.save(bookingModel1);

        Booking bookingModel2 = new Booking();
        bookingModel2.setId(2);
        bookingModel2.setBookingDateStart(LocalDateTime.now().plusDays(3));
        bookingModel2.setBookingDateEnd(LocalDateTime.now().plusDays(6));
        bookingModel2.setStatus(BookingStatus.WAITING);
        bookingModel2.setUser(booker);
        bookingModel2.setItem(item);
        bookingRepository.save(bookingModel2);

        List<Booking> bookings = bookingRepository
                .findAllByUserIdAndBookingDateEndBeforeOrderByBookingDateEndDesc(
                        booker.getId(), LocalDateTime.now(), Pageable.ofSize(10));

        assertEquals(1, bookings.size());
        assertEquals(bookingModel1.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByUserIdAndBookingDateEndBeforeOrderByBookingDateEndDesc_no_bookings_in_db_then_return_empty_list() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User booker = userRepository.save(userModel);

        List<Booking> bookings = bookingRepository
                .findAllByUserIdAndBookingDateEndBeforeOrderByBookingDateEndDesc(
                        booker.getId(), LocalDateTime.now(), Pageable.ofSize(10));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByUserIdAndBookingDateStartBeforeAndBookingDateEndAfterOrderByBookingDateEndDesc_normal_case_then_return_list_of_Booking() {
        User userModel1 = new User();
        userModel1.setId(1);
        userModel1.setName("user1");
        userModel1.setEmail("user1@email.com");
        User owner = userRepository.save(userModel1);

        User userModel2 = new User();
        userModel2.setId(2);
        userModel2.setName("user2");
        userModel2.setEmail("user2@email.com");
        User booker = userRepository.save(userModel2);

        Item itemModel = new Item();
        itemModel.setId(1);
        itemModel.setName("item");
        itemModel.setDescription("item description");
        itemModel.setAvailable(true);
        itemModel.setOwner(owner);
        Item item = itemRepository.save(itemModel);

        Booking bookingModel1 = new Booking();
        bookingModel1.setId(1);
        bookingModel1.setBookingDateStart(LocalDateTime.now().minusDays(6));
        bookingModel1.setBookingDateEnd(LocalDateTime.now().minusDays(3));
        bookingModel1.setStatus(BookingStatus.APPROVED);
        bookingModel1.setUser(booker);
        bookingModel1.setItem(item);
        bookingRepository.save(bookingModel1);

        Booking bookingModel2 = new Booking();
        bookingModel2.setId(2);
        bookingModel2.setBookingDateStart(LocalDateTime.now().plusDays(3));
        bookingModel2.setBookingDateEnd(LocalDateTime.now().plusDays(6));
        bookingModel2.setStatus(BookingStatus.WAITING);
        bookingModel2.setUser(booker);
        bookingModel2.setItem(item);
        bookingRepository.save(bookingModel2);

        List<Booking> bookings = bookingRepository
                .findAllByUserIdAndBookingDateStartBeforeAndBookingDateEndAfterOrderByBookingDateEndDesc(
                        booker.getId(), LocalDateTime.now(), LocalDateTime.now().minusDays(4), Pageable.ofSize(10));

        assertEquals(1, bookings.size());
        assertEquals(bookingModel1.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByUserIdAndBookingDateStartBeforeAndBookingDateEndAfterOrderByBookingDateEndDesc_no_bookings_in_db_then_return_empty_list() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User booker = userRepository.save(userModel);

        List<Booking> bookings = bookingRepository
                .findAllByUserIdAndBookingDateStartBeforeAndBookingDateEndAfterOrderByBookingDateEndDesc(
                        booker.getId(), LocalDateTime.now(), LocalDateTime.now().minusDays(4), Pageable.ofSize(10));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByUserIdAndStatusOrderByBookingDateEndDesc_normal_case_then_return_list_of_Booking() {
        User userModel1 = new User();
        userModel1.setId(1);
        userModel1.setName("user1");
        userModel1.setEmail("user1@email.com");
        User owner = userRepository.save(userModel1);

        User userModel2 = new User();
        userModel2.setId(2);
        userModel2.setName("user2");
        userModel2.setEmail("user2@email.com");
        User booker = userRepository.save(userModel2);

        Item itemModel = new Item();
        itemModel.setId(1);
        itemModel.setName("item");
        itemModel.setDescription("item description");
        itemModel.setAvailable(true);
        itemModel.setOwner(owner);
        Item item = itemRepository.save(itemModel);

        Booking bookingModel1 = new Booking();
        bookingModel1.setId(1);
        bookingModel1.setBookingDateStart(LocalDateTime.now().minusDays(20));
        bookingModel1.setBookingDateEnd(LocalDateTime.now().minusDays(17));
        bookingModel1.setStatus(BookingStatus.APPROVED);
        bookingModel1.setUser(booker);
        bookingModel1.setItem(item);
        bookingRepository.save(bookingModel1);

        Booking bookingModel2 = new Booking();
        bookingModel2.setId(2);
        bookingModel2.setBookingDateStart(LocalDateTime.now().minusDays(15));
        bookingModel2.setBookingDateEnd(LocalDateTime.now().minusDays(10));
        bookingModel2.setStatus(BookingStatus.APPROVED);
        bookingModel2.setUser(booker);
        bookingModel2.setItem(item);
        bookingRepository.save(bookingModel2);

        List<Booking> bookings = bookingRepository
                .findAllByUserIdAndStatusOrderByBookingDateEndDesc(booker.getId(), BookingStatus.APPROVED, Pageable.ofSize(10));

        assertEquals(2, bookings.size());
        assertEquals(bookingModel2.getId(), bookings.get(0).getId());
        assertEquals(bookingModel1.getId(), bookings.get(1).getId());
    }

    @Test
    void findAllByUserIdAndStatusOrderByBookingDateEndDesc_no_bookings_in_db_then_return_empty_list() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User booker = userRepository.save(userModel);

        List<Booking> bookings = bookingRepository
                .findAllByUserIdAndStatusOrderByBookingDateEndDesc(booker.getId(), BookingStatus.APPROVED, Pageable.ofSize(10));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByUserIdAndBookingDateStartAfterOrderByBookingDateEndDesc_normal_case_then_return_list_of_Booking() {
        User userModel1 = new User();
        userModel1.setId(1);
        userModel1.setName("user1");
        userModel1.setEmail("user1@email.com");
        User owner = userRepository.save(userModel1);

        User userModel2 = new User();
        userModel2.setId(2);
        userModel2.setName("user2");
        userModel2.setEmail("user2@email.com");
        User booker = userRepository.save(userModel2);

        Item itemModel = new Item();
        itemModel.setId(1);
        itemModel.setName("item");
        itemModel.setDescription("item description");
        itemModel.setAvailable(true);
        itemModel.setOwner(owner);
        Item item = itemRepository.save(itemModel);

        Booking bookingModel1 = new Booking();
        bookingModel1.setId(1);
        bookingModel1.setBookingDateStart(LocalDateTime.now().minusDays(3));
        bookingModel1.setBookingDateEnd(LocalDateTime.now().plusDays(3));
        bookingModel1.setStatus(BookingStatus.WAITING);
        bookingModel1.setUser(booker);
        bookingModel1.setItem(item);
        bookingRepository.save(bookingModel1);

        Booking bookingModel2 = new Booking();
        bookingModel2.setId(2);
        bookingModel2.setBookingDateStart(LocalDateTime.now().plusDays(3));
        bookingModel2.setBookingDateEnd(LocalDateTime.now().plusDays(6));
        bookingModel2.setStatus(BookingStatus.WAITING);
        bookingModel2.setUser(booker);
        bookingModel2.setItem(item);
        bookingRepository.save(bookingModel2);

        List<Booking> bookings = bookingRepository
                .findAllByUserIdAndBookingDateStartAfterOrderByBookingDateEndDesc(booker.getId(), LocalDateTime.now(), Pageable.ofSize(10));

        assertEquals(1, bookings.size());
        assertEquals(bookingModel2.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByUserIdAndBookingDateStartAfterOrderByBookingDateEndDesc_no_bookings_in_db_then_return_empty_list() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User booker = userRepository.save(userModel);

        List<Booking> bookings = bookingRepository
                .findAllByUserIdAndBookingDateStartAfterOrderByBookingDateEndDesc(booker.getId(), LocalDateTime.now(), Pageable.ofSize(10));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByItemOwnerIdOrderByBookingDateEndDesc_normal_case_then_return_list_of_Booking() {
        User userModel1 = new User();
        userModel1.setId(1);
        userModel1.setName("user1");
        userModel1.setEmail("user1@email.com");
        User owner = userRepository.save(userModel1);

        User userModel2 = new User();
        userModel2.setId(2);
        userModel2.setName("user2");
        userModel2.setEmail("user2@email.com");
        User booker = userRepository.save(userModel2);

        Item itemModel = new Item();
        itemModel.setId(1);
        itemModel.setName("item");
        itemModel.setDescription("item description");
        itemModel.setAvailable(true);
        itemModel.setOwner(owner);
        Item item = itemRepository.save(itemModel);

        Booking bookingModel1 = new Booking();
        bookingModel1.setId(1);
        bookingModel1.setBookingDateStart(LocalDateTime.now().minusDays(3));
        bookingModel1.setBookingDateEnd(LocalDateTime.now().plusDays(3));
        bookingModel1.setStatus(BookingStatus.WAITING);
        bookingModel1.setUser(booker);
        bookingModel1.setItem(item);
        bookingRepository.save(bookingModel1);

        Booking bookingModel2 = new Booking();
        bookingModel2.setId(2);
        bookingModel2.setBookingDateStart(LocalDateTime.now().plusDays(3));
        bookingModel2.setBookingDateEnd(LocalDateTime.now().plusDays(6));
        bookingModel2.setStatus(BookingStatus.WAITING);
        bookingModel2.setUser(booker);
        bookingModel2.setItem(item);
        bookingRepository.save(bookingModel2);

        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdOrderByBookingDateEndDesc(owner.getId(), Pageable.ofSize(10));

        assertEquals(2, bookings.size());
        assertEquals(bookingModel2.getId(), bookings.get(0).getId());
        assertEquals(bookingModel1.getId(), bookings.get(1).getId());
    }

    @Test
    void findAllByItemOwnerIdOrderByBookingDateEndDesc_no_bookings_in_db_then_return_empty_list() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User owner = userRepository.save(userModel);

        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdOrderByBookingDateEndDesc(owner.getId(), Pageable.ofSize(10));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByItemOwnerIdAndBookingDateEndBeforeOrderByBookingDateEndDesc_normal_case_then_return_list_of_Booking() {
        User userModel1 = new User();
        userModel1.setId(1);
        userModel1.setName("user1");
        userModel1.setEmail("user1@email.com");
        User owner = userRepository.save(userModel1);

        User userModel2 = new User();
        userModel2.setId(2);
        userModel2.setName("user2");
        userModel2.setEmail("user2@email.com");
        User booker = userRepository.save(userModel2);

        Item itemModel = new Item();
        itemModel.setId(1);
        itemModel.setName("item");
        itemModel.setDescription("item description");
        itemModel.setAvailable(true);
        itemModel.setOwner(owner);
        Item item = itemRepository.save(itemModel);

        Booking bookingModel1 = new Booking();
        bookingModel1.setId(1);
        bookingModel1.setBookingDateStart(LocalDateTime.now().minusDays(3));
        bookingModel1.setBookingDateEnd(LocalDateTime.now().plusDays(3));
        bookingModel1.setStatus(BookingStatus.WAITING);
        bookingModel1.setUser(booker);
        bookingModel1.setItem(item);
        bookingRepository.save(bookingModel1);

        Booking bookingModel2 = new Booking();
        bookingModel2.setId(2);
        bookingModel2.setBookingDateStart(LocalDateTime.now().plusDays(3));
        bookingModel2.setBookingDateEnd(LocalDateTime.now().plusDays(6));
        bookingModel2.setStatus(BookingStatus.WAITING);
        bookingModel2.setUser(booker);
        bookingModel2.setItem(item);
        bookingRepository.save(bookingModel2);

        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdAndBookingDateEndBeforeOrderByBookingDateEndDesc(owner.getId(), LocalDateTime.now().plusDays(7), Pageable.ofSize(10));

        assertEquals(2, bookings.size());
        assertEquals(bookingModel2.getId(), bookings.get(0).getId());
        assertEquals(bookingModel1.getId(), bookings.get(1).getId());
    }

    @Test
    void findAllByItemOwnerIdAndBookingDateEndBeforeOrderByBookingDateEndDesc_no_bookings_in_db_then_return_empty_list() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User owner = userRepository.save(userModel);

        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdAndBookingDateEndBeforeOrderByBookingDateEndDesc(owner.getId(), LocalDateTime.now().plusDays(7), Pageable.ofSize(10));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByItemOwnerIdAndBookingDateStartBeforeAndBookingDateEndAfterOrderByBookingDateEndDesc_normal_case_then_return_list_of_Booking() {
        User userModel1 = new User();
        userModel1.setId(1);
        userModel1.setName("user1");
        userModel1.setEmail("user1@email.com");
        User owner = userRepository.save(userModel1);

        User userModel2 = new User();
        userModel2.setId(2);
        userModel2.setName("user2");
        userModel2.setEmail("user2@email.com");
        User booker = userRepository.save(userModel2);

        Item itemModel = new Item();
        itemModel.setId(1);
        itemModel.setName("item");
        itemModel.setDescription("item description");
        itemModel.setAvailable(true);
        itemModel.setOwner(owner);
        Item item = itemRepository.save(itemModel);

        Booking bookingModel1 = new Booking();
        bookingModel1.setId(1);
        bookingModel1.setBookingDateStart(LocalDateTime.now().minusDays(3));
        bookingModel1.setBookingDateEnd(LocalDateTime.now().plusDays(3));
        bookingModel1.setStatus(BookingStatus.WAITING);
        bookingModel1.setUser(booker);
        bookingModel1.setItem(item);
        bookingRepository.save(bookingModel1);

        Booking bookingModel2 = new Booking();
        bookingModel2.setId(2);
        bookingModel2.setBookingDateStart(LocalDateTime.now().plusDays(3));
        bookingModel2.setBookingDateEnd(LocalDateTime.now().plusDays(6));
        bookingModel2.setStatus(BookingStatus.WAITING);
        bookingModel2.setUser(booker);
        bookingModel2.setItem(item);
        bookingRepository.save(bookingModel2);

        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdAndBookingDateStartBeforeAndBookingDateEndAfterOrderByBookingDateEndDesc(
                        owner.getId(), LocalDateTime.now(), LocalDateTime.now(), Pageable.ofSize(10));

        assertEquals(1, bookings.size());
        assertEquals(bookingModel1.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByItemOwnerIdAndBookingDateStartBeforeAndBookingDateEndAfterOrderByBookingDateEndDesc_no_bookings_in_db_then_return_empty_list() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User owner = userRepository.save(userModel);

        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdAndBookingDateStartBeforeAndBookingDateEndAfterOrderByBookingDateEndDesc(
                        owner.getId(), LocalDateTime.now(), LocalDateTime.now(), Pageable.ofSize(10));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByBookingDateEndDesc_normal_case_then_return_list_of_Booking() {
        User userModel1 = new User();
        userModel1.setId(1);
        userModel1.setName("user1");
        userModel1.setEmail("user1@email.com");
        User owner = userRepository.save(userModel1);

        User userModel2 = new User();
        userModel2.setId(2);
        userModel2.setName("user2");
        userModel2.setEmail("user2@email.com");
        User booker = userRepository.save(userModel2);

        Item itemModel = new Item();
        itemModel.setId(1);
        itemModel.setName("item");
        itemModel.setDescription("item description");
        itemModel.setAvailable(true);
        itemModel.setOwner(owner);
        Item item = itemRepository.save(itemModel);

        Booking bookingModel1 = new Booking();
        bookingModel1.setId(1);
        bookingModel1.setBookingDateStart(LocalDateTime.now().minusDays(3));
        bookingModel1.setBookingDateEnd(LocalDateTime.now().plusDays(3));
        bookingModel1.setStatus(BookingStatus.APPROVED);
        bookingModel1.setUser(booker);
        bookingModel1.setItem(item);
        bookingRepository.save(bookingModel1);

        Booking bookingModel2 = new Booking();
        bookingModel2.setId(2);
        bookingModel2.setBookingDateStart(LocalDateTime.now().plusDays(3));
        bookingModel2.setBookingDateEnd(LocalDateTime.now().plusDays(6));
        bookingModel2.setStatus(BookingStatus.APPROVED);
        bookingModel2.setUser(booker);
        bookingModel2.setItem(item);
        bookingRepository.save(bookingModel2);

        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdAndStatusOrderByBookingDateEndDesc(owner.getId(), BookingStatus.APPROVED, Pageable.ofSize(10));

        assertEquals(2, bookings.size());
        assertEquals(bookingModel2.getId(), bookings.get(0).getId());
        assertEquals(bookingModel1.getId(), bookings.get(1).getId());
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByBookingDateEndDesc_no_bookings_in_db_then_return_empty_list() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User owner = userRepository.save(userModel);

        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdAndStatusOrderByBookingDateEndDesc(owner.getId(), BookingStatus.APPROVED, Pageable.ofSize(10));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByItemOwnerIdAndBookingDateStartAfterOrderByBookingDateEndDesc_normal_case_then_return_list_of_Booking() {
        User userModel1 = new User();
        userModel1.setId(1);
        userModel1.setName("user1");
        userModel1.setEmail("user1@email.com");
        User owner = userRepository.save(userModel1);

        User userModel2 = new User();
        userModel2.setId(2);
        userModel2.setName("user2");
        userModel2.setEmail("user2@email.com");
        User booker = userRepository.save(userModel2);

        Item itemModel = new Item();
        itemModel.setId(1);
        itemModel.setName("item");
        itemModel.setDescription("item description");
        itemModel.setAvailable(true);
        itemModel.setOwner(owner);
        Item item = itemRepository.save(itemModel);

        Booking bookingModel1 = new Booking();
        bookingModel1.setId(1);
        bookingModel1.setBookingDateStart(LocalDateTime.now().minusDays(3));
        bookingModel1.setBookingDateEnd(LocalDateTime.now().plusDays(3));
        bookingModel1.setStatus(BookingStatus.WAITING);
        bookingModel1.setUser(booker);
        bookingModel1.setItem(item);
        bookingRepository.save(bookingModel1);

        Booking bookingModel2 = new Booking();
        bookingModel2.setId(2);
        bookingModel2.setBookingDateStart(LocalDateTime.now().plusDays(3));
        bookingModel2.setBookingDateEnd(LocalDateTime.now().plusDays(6));
        bookingModel2.setStatus(BookingStatus.WAITING);
        bookingModel2.setUser(booker);
        bookingModel2.setItem(item);
        bookingRepository.save(bookingModel2);

        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdAndBookingDateStartAfterOrderByBookingDateEndDesc(owner.getId(), LocalDateTime.now(), Pageable.ofSize(10));

        assertEquals(1, bookings.size());
        assertEquals(bookingModel2.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByItemOwnerIdAndBookingDateStartAfterOrderByBookingDateEndDesc_no_bookings_in_db_then_return_empty_list() {
        User userModel = new User();
        userModel.setId(1);
        userModel.setName("user");
        userModel.setEmail("user@email.com");
        User owner = userRepository.save(userModel);

        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdAndBookingDateStartAfterOrderByBookingDateEndDesc(owner.getId(), LocalDateTime.now(), Pageable.ofSize(10));

        assertTrue(bookings.isEmpty());
    }

    @Test
    void findAllByUserIdAndItemIdAndStatusAndBookingDateEndBefore_normal_case_then_return_list_of_Booking() {
        User userModel1 = new User();
        userModel1.setId(1);
        userModel1.setName("user1");
        userModel1.setEmail("user1@email.com");
        User owner = userRepository.save(userModel1);

        User userModel2 = new User();
        userModel2.setId(2);
        userModel2.setName("user2");
        userModel2.setEmail("user2@email.com");
        User booker = userRepository.save(userModel2);

        Item itemModel = new Item();
        itemModel.setId(1);
        itemModel.setName("item");
        itemModel.setDescription("item description");
        itemModel.setAvailable(true);
        itemModel.setOwner(owner);
        Item item = itemRepository.save(itemModel);

        Booking bookingModel1 = new Booking();
        bookingModel1.setId(1);
        bookingModel1.setBookingDateStart(LocalDateTime.now().minusDays(3));
        bookingModel1.setBookingDateEnd(LocalDateTime.now().minusDays(1));
        bookingModel1.setStatus(BookingStatus.WAITING);
        bookingModel1.setUser(booker);
        bookingModel1.setItem(item);
        bookingRepository.save(bookingModel1);

        Booking bookingModel2 = new Booking();
        bookingModel2.setId(2);
        bookingModel2.setBookingDateStart(LocalDateTime.now().plusDays(3));
        bookingModel2.setBookingDateEnd(LocalDateTime.now().plusDays(6));
        bookingModel2.setStatus(BookingStatus.WAITING);
        bookingModel2.setUser(booker);
        bookingModel2.setItem(item);
        bookingRepository.save(bookingModel2);

        List<Booking> bookings = bookingRepository
                .findAllByUserIdAndItemIdAndStatusAndBookingDateEndBefore(booker.getId(), item.getId(), BookingStatus.WAITING, LocalDateTime.now());

        assertEquals(1, bookings.size());
        assertEquals(bookingModel1.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByUserIdAndItemIdAndStatusAndBookingDateEndBefore_no_bookings_in_db_then_return_empty_list() {
        User userModel1 = new User();
        userModel1.setId(1);
        userModel1.setName("user1");
        userModel1.setEmail("user1@email.com");
        User owner = userRepository.save(userModel1);

        User userModel2 = new User();
        userModel2.setId(2);
        userModel2.setName("user2");
        userModel2.setEmail("user2@email.com");
        User booker = userRepository.save(userModel2);

        Item itemModel = new Item();
        itemModel.setId(1);
        itemModel.setName("item");
        itemModel.setDescription("item description");
        itemModel.setAvailable(true);
        itemModel.setOwner(owner);
        Item item = itemRepository.save(itemModel);


        List<Booking> bookings = bookingRepository
                .findAllByUserIdAndItemIdAndStatusAndBookingDateEndBefore(booker.getId(), item.getId(), BookingStatus.WAITING, LocalDateTime.now());

        assertTrue(bookings.isEmpty());
    }
}