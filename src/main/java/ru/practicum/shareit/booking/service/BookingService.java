package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.BookingSearchState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.exceptionimp.BadRequestException;
import ru.practicum.shareit.exception.exceptionimp.InternalServerException;
import ru.practicum.shareit.exception.exceptionimp.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ModelMapper modelMapper;

    @Transactional
    public BookingDto createBooking(long userId, CreateBookingDto dto) {
        log.debug("Entering createBooking method: CreateBookingDto = {}, userId = {} ", dto, userId);

        if (dto.getEnd().isBefore(dto.getStart()) || dto.getEnd().isEqual(dto.getStart())) {
            throw new BadRequestException("Wrong date");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " is not found"));
        log.debug("User was found");

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() ->
                        new NotFoundException("Item with id " + dto.getItemId() + " is not found")
                );
        log.debug("Item was found");

        if (item.getOwner().equals(user)) {
            throw new NotFoundException("Owner can't create booking for its own item");
        }

        if (!item.isAvailable()) {
            throw new BadRequestException("Item is unavailable");
        }

        try {
            Booking booking = modelMapper.map(dto, Booking.class);
            booking.setUser(user);
            booking.setItem(item);
            booking.setStatus(BookingStatus.WAITING);

            Booking savedBooking = bookingRepository.save(booking);
            BookingDto bookingDto = modelMapper.map(savedBooking, BookingDto.class);
            log.debug("Mapping from Booking to BookingDto: {}", bookingDto);
            log.debug("Exiting createBooking method");

            return bookingDto;
        } catch (Exception exc) {
            log.error("An unexpected exception has occurred " + exc);

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
            log.debug("Entering approveBooking method: userId = {}, bookingId = {}, approved = {} ",
                    userId, bookingId, approved);

            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new NotFoundException("User with id " + userId + " is not found"));
            log.debug("User was found");

            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() ->
                            new NotFoundException("Booking with id " + bookingId + " is not found"));

            if (!booking.getItem().getOwner().equals(user)) {
                throw new NotFoundException("Not found");
            }

            if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                throw new BadRequestException("Can not change status after post has been approved");
            }

            BookingStatus status = approved
                    ? BookingStatus.APPROVED
                    : BookingStatus.REJECTED;
            booking.setStatus(status);

        try {
            Booking updatedBooking = bookingRepository.save(booking);
            BookingDto bookingDto = modelMapper.map(updatedBooking, BookingDto.class);
            log.debug("BookingStatus was changed to {}", status);
            log.debug("Mapping from Booking to BookingDto: {}", bookingDto);
            log.debug("Exiting approveBooking method");

            return bookingDto;
        } catch (Exception exc) {
            log.error("An unexpected exception has occurred " + exc);

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(long userId, long bookingId) {
        log.debug("Entering getBookingById method: userId = {}, bookingId = {}", userId, bookingId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User with id " + userId + " is not found"));
        log.debug("User was found");

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new NotFoundException("Booking with id " + bookingId + " is not found"));

        if (!booking.getUser().equals(user) && !booking.getItem().getOwner().equals(user)) {
            throw new NotFoundException("Not found");
        }

        try {
            BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);
            log.debug("Mapping from Booking to BookingDto: {}", bookingDto);
            log.debug("Exiting getBookingById method");

            return bookingDto;
        } catch (Exception exc) {
            log.error("An unexpected exception has occurred " + exc);

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getAllUserBooking(long userId, String state) {
        log.debug("Entering getAllUserBooking method: userId = {}, BookingSearchState = {}",
                userId, state);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " is not found"));
        log.debug("User was found");

        try {
            BookingSearchState searchState = BookingSearchState.valueOf(state);
            List<Booking> bookings;

            switch (searchState) {
                case PAST:
                    bookings = bookingRepository
                        .findAllByUserIdAndBookingDateEndBeforeOrderByBookingDateEndDesc(
                                userId, LocalDateTime.now()
                        );
                    break;
                case FUTURE:
                    bookings = bookingRepository
                        .findAllByUserIdAndBookingDateStartAfterOrderByBookingDateEndDesc(
                                userId, LocalDateTime.now()
                        );
                    break;
                case CURRENT:
                    bookings = bookingRepository
                        .findAllByUserIdAndBookingDateStartBeforeAndBookingDateEndAfterOrderByBookingDateEndDesc(
                                userId, LocalDateTime.now(), LocalDateTime.now()
                        );
                    break;
                case WAITING:
                    bookings = bookingRepository
                        .findAllByUserIdAndStatusOrderByBookingDateEndDesc(
                                userId, BookingStatus.WAITING
                        );
                    break;
                case REJECTED:
                    bookings = bookingRepository
                        .findAllByUserIdAndStatusOrderByBookingDateEndDesc(
                                userId, BookingStatus.REJECTED
                        );
                    break;
                default:
                    bookings = bookingRepository.findAllByUserIdOrderByBookingDateEndDesc(userId);
            }

            List<BookingDto> bookingDtos = modelMapper
                    .map(bookings, new TypeToken<List<BookingDto>>() {}.getType());
            log.debug("Mapping from List<Booking> to List<BookingDto>: {}", bookingDtos);
            log.debug("Exiting getAllUserBooking method");

            return bookingDtos;
        } catch (IllegalArgumentException exc) {
            log.warn("Error has occurred {}", exc.getMessage());

            throw new BadRequestException("Unknown state: " + state);
        } catch (Exception exc) {
            log.error("An unexpected exception has occurred " + exc);

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getAllOwnerBooking(long ownerId, String state) {
        log.debug("Entering getAllOwnerBooking method: ownerId = {}, BookingSearchState = {}",
                ownerId, state);

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id " + ownerId + " is not found"));
        log.debug("User was found");

        try {
            BookingSearchState searchState = BookingSearchState.valueOf(state);
            List<Booking> bookings;

            switch (searchState) {
                case PAST:
                    bookings = bookingRepository
                            .findAllByItemOwnerIdAndBookingDateEndBeforeOrderByBookingDateEndDesc(
                                    ownerId, LocalDateTime.now()
                            );
                    break;
                case FUTURE:
                    bookings = bookingRepository
                            .findAllByItemOwnerIdAndBookingDateStartAfterOrderByBookingDateEndDesc(
                                    ownerId, LocalDateTime.now()
                            );
                    break;
                case CURRENT:
                    bookings = bookingRepository
                            .findAllByItemOwnerIdAndBookingDateStartBeforeAndBookingDateEndAfterOrderByBookingDateEndDesc(
                                    ownerId, LocalDateTime.now(), LocalDateTime.now()
                            );
                    break;
                case WAITING:
                    bookings = bookingRepository
                            .findAllByItemOwnerIdAndStatusOrderByBookingDateEndDesc(
                                    ownerId, BookingStatus.WAITING
                            );
                    break;
                case REJECTED:
                    bookings = bookingRepository
                            .findAllByItemOwnerIdAndStatusOrderByBookingDateEndDesc(
                                    ownerId, BookingStatus.REJECTED
                            );
                    break;
                default:
                    bookings = bookingRepository.findAllByItemOwnerIdOrderByBookingDateEndDesc(ownerId);
            }

            List<BookingDto> bookingDtos = modelMapper
                    .map(bookings, new TypeToken<List<BookingDto>>() {}.getType());
            log.debug("Mapping from List<Booking> to List<BookingDto>: {}", bookingDtos);
            log.debug("Exiting getAllOwnerBooking method");

            return bookingDtos;
        } catch (IllegalArgumentException exc) {
            log.warn("Error has occurred {}", exc.getMessage());

            throw new BadRequestException("Unknown state: " + state);
        } catch (Exception exc) {
            log.error("An unexpected exception has occurred " + exc);

            throw new InternalServerException("Something went wrong");
        }
    }
}
