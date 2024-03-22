package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.ShareItConfig;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.BookingSearchState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.exceptionimp.BadRequestException;
import ru.practicum.shareit.exception.exceptionimp.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private ItemRepository itemRepositoryMock;

    @Test
    void createBooking_normal_case_then_return_BookingDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        CreateBookingDto dto = new CreateBookingDto();
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setItemId(1);

        User user = new User();
        user.setId(1);

        Item itemForBooking = new Item();
        itemForBooking.setId(1);
        itemForBooking.setOwner(new User());
        itemForBooking.setAvailable(true);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setUser(user);
        booking.setItem(itemForBooking);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBookingDateStart(dto.getStart());
        booking.setBookingDateEnd(dto.getEnd());

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForBooking));

        Mockito
                .when(bookingRepositoryMock.save(any()))
                .thenReturn(booking);

        BookingDto bookingDto = bookingService.createBooking(user.getId(), dto);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getUser().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getBookingDateStart(), bookingDto.getStart());
        assertEquals(booking.getBookingDateEnd(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());

        Mockito.verify(bookingRepositoryMock, Mockito.times(1))
                .save(any(Booking.class));
    }

    @Test
    void createBooking_user_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        long notFoundUser = 1000;
        CreateBookingDto dto = new CreateBookingDto();
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setItemId(1);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(notFoundUser, dto));

        Mockito.verify(bookingRepositoryMock, Mockito.never())
                .save(any(Booking.class));
    }

    @Test
    void createBooking_item_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        CreateBookingDto dto = new CreateBookingDto();
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setItemId(1);

        User user = new User();
        user.setId(1);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(1, dto));

        Mockito.verify(bookingRepositoryMock, Mockito.never())
                .save(any(Booking.class));
    }

    @Test
    void createBooking_start_date_is_before_end_date_then_throw_BadRequestException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        CreateBookingDto dto = new CreateBookingDto();
        dto.setStart(LocalDateTime.now().plusDays(2));
        dto.setEnd(LocalDateTime.now().plusDays(1));
        dto.setItemId(1);

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(1, dto));

        Mockito.verify(bookingRepositoryMock, Mockito.never())
                .save(any(Booking.class));
    }

    @Test
    void createBooking_start_date_equals_end_date_then_throw_BadRequestException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        LocalDateTime sameTime = LocalDateTime.now().plusDays(2);
        CreateBookingDto dto = new CreateBookingDto();
        dto.setStart(sameTime);
        dto.setEnd(sameTime);
        dto.setItemId(1);

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(1, dto));

        Mockito.verify(bookingRepositoryMock, Mockito.never())
                .save(any(Booking.class));
    }

    @Test
    void createBooking_user_is_owner_of_item_then_throw_BadRequestException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        CreateBookingDto dto = new CreateBookingDto();
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setItemId(1);

        User user = new User();
        user.setId(1);

        Item itemForBooking = new Item();
        itemForBooking.setId(1);
        itemForBooking.setOwner(user);
        itemForBooking.setAvailable(true);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForBooking));

        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(user.getId(), dto));

        Mockito.verify(bookingRepositoryMock, Mockito.never())
                .save(any(Booking.class));
    }

    @Test
    void createBooking_item_is_unavailable_then_throw_BadRequestException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        CreateBookingDto dto = new CreateBookingDto();
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setItemId(1);

        User user = new User();
        user.setId(1);

        Item itemForBooking = new Item();
        itemForBooking.setId(1);
        itemForBooking.setOwner(new User());
        itemForBooking.setAvailable(false);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemForBooking));

        assertThrows(BadRequestException.class,
                () -> bookingService.createBooking(user.getId(), dto));

        Mockito.verify(bookingRepositoryMock, Mockito.never())
                .save(any(Booking.class));
    }

    @Test
    void approveBooking_normal_approve_case_then_return_BookingDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User user = new User();
        user.setId(2);

        User owner = new User();
        owner.setId(1);

        Item itemForBooking = new Item();
        itemForBooking.setId(1);
        itemForBooking.setOwner(owner);
        itemForBooking.setAvailable(true);

        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking();
        booking.setId(1);
        booking.setUser(user);
        booking.setItem(itemForBooking);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBookingDateStart(startTime);
        booking.setBookingDateEnd(endTime);

        Booking approvedBooking = new Booking();
        approvedBooking.setId(1);
        approvedBooking.setUser(user);
        approvedBooking.setItem(itemForBooking);
        approvedBooking.setStatus(BookingStatus.APPROVED);
        approvedBooking.setBookingDateStart(startTime);
        approvedBooking.setBookingDateEnd(endTime);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(owner));

        Mockito
                .when(bookingRepositoryMock.findById(any()))
                .thenReturn(Optional.of(booking));

        Mockito
                .when(bookingRepositoryMock.save(any()))
                .thenReturn(approvedBooking);

        BookingDto bookingDto =
                bookingService.approveBooking(owner.getId(), booking.getId(), true);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getUser().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getBookingDateStart(), bookingDto.getStart());
        assertEquals(booking.getBookingDateEnd(), bookingDto.getEnd());
        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());

        Mockito.verify(bookingRepositoryMock, Mockito.times(1))
                .save(any(Booking.class));
    }

    @Test
    void approveBooking_normal_reject_case_then_return_BookingDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User user = new User();
        user.setId(2);

        User owner = new User();
        owner.setId(1);

        Item itemForBooking = new Item();
        itemForBooking.setId(1);
        itemForBooking.setOwner(owner);
        itemForBooking.setAvailable(true);

        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking();
        booking.setId(1);
        booking.setUser(user);
        booking.setItem(itemForBooking);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBookingDateStart(startTime);
        booking.setBookingDateEnd(endTime);

        Booking rejectedBooking = new Booking();
        rejectedBooking.setId(1);
        rejectedBooking.setUser(user);
        rejectedBooking.setItem(itemForBooking);
        rejectedBooking.setStatus(BookingStatus.REJECTED);
        rejectedBooking.setBookingDateStart(startTime);
        rejectedBooking.setBookingDateEnd(endTime);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(owner));

        Mockito
                .when(bookingRepositoryMock.findById(any()))
                .thenReturn(Optional.of(booking));

        Mockito
                .when(bookingRepositoryMock.save(any()))
                .thenReturn(rejectedBooking);

        BookingDto bookingDto =
                bookingService.approveBooking(owner.getId(), booking.getId(), true);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getUser().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getBookingDateStart(), bookingDto.getStart());
        assertEquals(booking.getBookingDateEnd(), bookingDto.getEnd());
        assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());

        Mockito.verify(bookingRepositoryMock, Mockito.times(1))
                .save(any(Booking.class));
    }

    @Test
    void approveBooking_user_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(1000, 1, true));

        Mockito.verify(bookingRepositoryMock, Mockito.times(0))
                .save(any(Booking.class));
    }

    @Test
    void approveBooking_booking_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User user = new User();
        user.setId(2);

        User owner = new User();
        owner.setId(1);

        Item itemForBooking = new Item();
        itemForBooking.setId(1);
        itemForBooking.setOwner(owner);
        itemForBooking.setAvailable(true);

        long notFoundBooking = 1000;

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(owner));

        Mockito
                .when(bookingRepositoryMock.findById(notFoundBooking))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(owner.getId(), notFoundBooking, true));

        Mockito.verify(bookingRepositoryMock, Mockito.times(0))
                .save(any(Booking.class));
    }

    @Test
    void approveBooking_user_is_not_owner_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User user = new User();
        user.setId(2);

        User owner = new User();
        owner.setId(1);

        Item itemForBooking = new Item();
        itemForBooking.setId(1);
        itemForBooking.setOwner(new User());
        itemForBooking.setAvailable(true);

        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking();
        booking.setId(1);
        booking.setUser(user);
        booking.setItem(itemForBooking);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBookingDateStart(startTime);
        booking.setBookingDateEnd(endTime);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(owner));

        Mockito
                .when(bookingRepositoryMock.findById(any()))
                .thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(owner.getId(), booking.getId(), true));

        Mockito.verify(bookingRepositoryMock, Mockito.times(0))
                .save(any(Booking.class));
    }

    @Test
    void approveBooking_booking_is_already_approved_then_throw_BadRequestException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User user = new User();
        user.setId(2);

        User owner = new User();
        owner.setId(1);

        Item itemForBooking = new Item();
        itemForBooking.setId(1);
        itemForBooking.setOwner(owner);
        itemForBooking.setAvailable(true);

        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking();
        booking.setId(1);
        booking.setUser(user);
        booking.setItem(itemForBooking);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBookingDateStart(startTime);
        booking.setBookingDateEnd(endTime);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(owner));

        Mockito
                .when(bookingRepositoryMock.findById(any()))
                .thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class,
                () -> bookingService.approveBooking(owner.getId(), booking.getId(), true));

        Mockito.verify(bookingRepositoryMock, Mockito.times(0))
                .save(any(Booking.class));
    }

    @Test
    void getBookingById_normal_case_owner_request_then_return_BookingDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User booker = new User();
        booker.setId(2);

        User owner = new User();
        owner.setId(1);

        Item itemForBooking = new Item();
        itemForBooking.setId(1);
        itemForBooking.setOwner(owner);
        itemForBooking.setAvailable(true);

        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking();
        booking.setId(1);
        booking.setUser(booker);
        booking.setItem(itemForBooking);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBookingDateStart(startTime);
        booking.setBookingDateEnd(endTime);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(owner));

        Mockito
                .when(bookingRepositoryMock.findById(any()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto =
                bookingService.getBookingById(owner.getId(), booking.getId());

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getUser().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getBookingDateStart(), bookingDto.getStart());
        assertEquals(booking.getBookingDateEnd(), bookingDto.getEnd());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
    }

    @Test
    void getBookingById_normal_case_booker_request_then_return_BookingDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User booker = new User();
        booker.setId(2);

        User owner = new User();
        owner.setId(1);

        Item itemForBooking = new Item();
        itemForBooking.setId(1);
        itemForBooking.setOwner(owner);
        itemForBooking.setAvailable(true);

        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking();
        booking.setId(1);
        booking.setUser(booker);
        booking.setItem(itemForBooking);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBookingDateStart(startTime);
        booking.setBookingDateEnd(endTime);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(owner));

        Mockito
                .when(bookingRepositoryMock.findById(any()))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto =
                bookingService.getBookingById(booker.getId(), booking.getId());

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getUser().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getBookingDateStart(), bookingDto.getStart());
        assertEquals(booking.getBookingDateEnd(), bookingDto.getEnd());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
    }

    @Test
    void getBookingById_normal_case_random_user_request_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User randomUser = new User();
        randomUser.setId(3);

        User booker = new User();
        booker.setId(2);

        User owner = new User();
        owner.setId(1);

        Item itemForBooking = new Item();
        itemForBooking.setId(1);
        itemForBooking.setOwner(owner);
        itemForBooking.setAvailable(true);

        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking();
        booking.setId(1);
        booking.setUser(booker);
        booking.setItem(itemForBooking);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBookingDateStart(startTime);
        booking.setBookingDateEnd(endTime);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(randomUser));

        Mockito
                .when(bookingRepositoryMock.findById(any()))
                .thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(randomUser.getId(), booking.getId()));
    }

    @Test
    void getBookingById_user_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User booker = new User();
        booker.setId(2);

        User owner = new User();
        owner.setId(1);

        Item itemForBooking = new Item();
        itemForBooking.setId(1);
        itemForBooking.setOwner(owner);
        itemForBooking.setAvailable(true);

        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking();
        booking.setId(1);
        booking.setUser(booker);
        booking.setItem(itemForBooking);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBookingDateStart(startTime);
        booking.setBookingDateEnd(endTime);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(1000, booking.getId()));
    }

    @Test
    void getBookingById_booking_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User booker = new User();
        booker.setId(2);

        User owner = new User();
        owner.setId(1);

        Item itemForBooking = new Item();
        itemForBooking.setId(1);
        itemForBooking.setOwner(owner);
        itemForBooking.setAvailable(true);

        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = LocalDateTime.now().plusDays(2);
        Booking booking = new Booking();
        booking.setId(1);
        booking.setUser(booker);
        booking.setItem(itemForBooking);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBookingDateStart(startTime);
        booking.setBookingDateEnd(endTime);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(owner));

        Mockito
                .when(bookingRepositoryMock.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(owner.getId(), 1000));
    }

    @Test
    void getAllUserBooking_normal_case_then_return_list_of_BookingDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User booker = new User();
        booker.setId(1);

        Item itemForBooking = new Item();
        itemForBooking.setId(1);
        itemForBooking.setOwner(new User());
        itemForBooking.setAvailable(true);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setUser(booker);
        booking.setItem(itemForBooking);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBookingDateStart(LocalDateTime.now().plusDays(1));
        booking.setBookingDateEnd(LocalDateTime.now().plusDays(2));

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booker));

        Mockito
                .when(bookingRepositoryMock
                        .findAllByUserIdOrderByBookingDateEndDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingDtos =
                bookingService.getAllUserBooking(
                        booker.getId(), BookingSearchState.ALL.name(), 0, 15);

        assertEquals(booking.getId(), bookingDtos.get(0).getId());
        assertEquals(booking.getUser().getId(), bookingDtos.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDtos.get(0).getItem().getId());
        assertEquals(booking.getBookingDateStart(), bookingDtos.get(0).getStart());
        assertEquals(booking.getBookingDateEnd(), bookingDtos.get(0).getEnd());
        assertEquals(booking.getStatus(), bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllUserBooking_normal_case_no_booking_then_return_empty_list_of_BookingDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User booker = new User();
        booker.setId(1);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booker));

        Mockito
                .when(bookingRepositoryMock
                        .findAllByUserIdOrderByBookingDateEndDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(Collections.emptyList());

        List<BookingDto> bookingDtos =
                bookingService.getAllUserBooking(
                        booker.getId(), BookingSearchState.ALL.name(), 0, 15);

        assertEquals(0, bookingDtos.size());
    }

    @Test
    void getAllUserBooking_user_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getAllUserBooking(
                        10, BookingSearchState.ALL.name(), 0, 15));
    }

    @Test
    void getAllUserBooking_wrong_state_then_throw_BadRequestException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User booker = new User();
        booker.setId(1);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booker));

        assertThrows(BadRequestException.class,
                () -> bookingService.getAllUserBooking(
                        10, "WRONG", 0, 15));
    }

    @Test
    void getAllOwnerBooking_normal_case_then_return_list_of_BookingDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User owner = new User();
        owner.setId(2);

        User booker = new User();
        booker.setId(1);

        Item itemForBooking = new Item();
        itemForBooking.setId(1);
        itemForBooking.setOwner(owner);
        itemForBooking.setAvailable(true);

        Booking booking = new Booking();
        booking.setId(1);
        booking.setUser(booker);
        booking.setItem(itemForBooking);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBookingDateStart(LocalDateTime.now().plusDays(1));
        booking.setBookingDateEnd(LocalDateTime.now().plusDays(2));

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(owner));

        Mockito
                .when(bookingRepositoryMock
                        .findAllByItemOwnerIdOrderByBookingDateEndDesc(
                                Mockito.anyLong(), Mockito.any()))
                .thenReturn(List.of(booking));

        List<BookingDto> bookingDtos =
                bookingService.getAllOwnerBooking(
                        owner.getId(), BookingSearchState.ALL.name(), 0, 15);

        assertEquals(booking.getId(), bookingDtos.get(0).getId());
        assertEquals(booking.getUser().getId(), bookingDtos.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDtos.get(0).getItem().getId());
        assertEquals(booking.getBookingDateStart(), bookingDtos.get(0).getStart());
        assertEquals(booking.getBookingDateEnd(), bookingDtos.get(0).getEnd());
        assertEquals(booking.getStatus(), bookingDtos.get(0).getStatus());
    }

    @Test
    void getAllOwnerBooking_normal_case_no_booking_then_return_empty_list_of_BookingDto() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User owner = new User();
        owner.setId(2);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(owner));

        Mockito
                .when(bookingRepositoryMock
                        .findAllByItemOwnerIdOrderByBookingDateEndDesc(
                                Mockito.anyLong(), Mockito.any()))
                .thenReturn(Collections.emptyList());

        List<BookingDto> bookingDtos =
                bookingService.getAllOwnerBooking(
                        owner.getId(), BookingSearchState.ALL.name(), 0, 15);

        assertEquals(0, bookingDtos.size());
    }

    @Test
    void getAllOwnerBooking_owner_is_not_found_then_throw_NotFoundException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getAllOwnerBooking(
                        10, BookingSearchState.ALL.name(), 0, 15));
    }

    @Test
    void getAllOwnerBooking_wrong_state_then_throw_BadRequestException() {
        ShareItConfig mapperConfig = new ShareItConfig();
        ModelMapper mapper = mapperConfig.modelMapper();

        BookingService bookingService = new BookingService(
                bookingRepositoryMock, userRepositoryMock, itemRepositoryMock, mapper
        );

        User owner = new User();
        owner.setId(2);

        Mockito
                .when(userRepositoryMock.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(owner));

        assertThrows(BadRequestException.class,
                () -> bookingService.getAllOwnerBooking(
                        10, "WRONG", 0, 15));
    }
}