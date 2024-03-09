package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.BookingSearchState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.exceptionimp.BadRequestException;
import ru.practicum.shareit.exception.exceptionimp.ForbiddenException;
import ru.practicum.shareit.exception.exceptionimp.InternalServerException;
import ru.practicum.shareit.exception.exceptionimp.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
        try {
            log.debug("Entering createBooking method");
            log.debug("Got {} value as CreateBookingDto and {} value as userId", dto, userId);

            if (dto.getEnd().isBefore(dto.getStart()) || dto.getEnd().isEqual(dto.getStart())) {
                throw new BadRequestException("Wrong date");
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new NotFoundException("Item with id " + userId + " is not found"));
            log.debug("User was found");

            Item item = itemRepository.findById(dto.getItemId())
                    .orElseThrow(() ->
                            new NotFoundException("Item with id " + dto.getItemId() + " is not found")
                    );
            log.debug("Item was found");

            if (item.getOwner().equals(user) || !item.isAvailable()) {
                String message = item.getOwner().equals(user)
                        ? "Owner can't create booking for its own item"
                        : "Item is unavailable";

                throw new BadRequestException(message);
            }

            Booking booking = modelMapper.map(dto, Booking.class);
            booking.setUser(user);
            booking.setItem(item);
            booking.setStatus(BookingStatus.WAITING);

            Booking savedBooking = bookingRepository.save(booking);
            BookingDto bookingDto = modelMapper.map(savedBooking, BookingDto.class);
            log.debug("Mapping from Booking to BookingDto: {}", bookingDto);
            log.debug("Exiting createBooking method");

            return bookingDto;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting createBooking method");

            throw new NotFoundException(exc.getDescription());
        } catch (BadRequestException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting createBooking method");

            throw new BadRequestException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting createBooking method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    @Transactional
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
        try {
            log.debug("Entering approveBooking method");
            log.debug("Got {} value as userId, {} value as bookingId and {} value as approved",
                    userId, bookingId, approved);

            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new NotFoundException("Item with id " + userId + " is not found"));
            log.debug("User was found");

            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() ->
                            new NotFoundException("Booking with id " + bookingId + " is not found"));

            if (!booking.getItem().getOwner().equals(user)) {
                throw new ForbiddenException("Only owner can approve its items");
            }

            BookingStatus status = approved
                    ? BookingStatus.APPROVED
                    : BookingStatus.REJECTED;
            booking.setStatus(status);

            Booking updatedBooking = bookingRepository.save(booking);
            BookingDto bookingDto = modelMapper.map(updatedBooking, BookingDto.class);
            log.debug("BookingStatus was changed to {}", status);
            log.debug("Mapping from Booking to BookingDto: {}", bookingDto);
            log.debug("Exiting approveBooking method");

            return bookingDto;
        } catch (NotFoundException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting approveBooking method");

            throw new NotFoundException(exc.getDescription());
        } catch (ForbiddenException exc) {
            log.warn("Error has occurred {}", exc.getDescription());
            log.debug("Exiting approveBooking method");

            throw new ForbiddenException(exc.getDescription());
        } catch (Throwable throwable) {
            log.warn("An unexpected exception has occurred " + throwable.getMessage());
            log.debug("Exiting approveBooking method");
            throwable.printStackTrace();

            throw new InternalServerException("Something went wrong");
        }
    }

    public BookingDto getBookingById(long userId, long bookingId) {

        return null;
    }

    public List<BookingDto> getAllUserBooking(long userId, BookingSearchState state) {

        return null;
    }

    public List<BookingDto> getAllOwnerBooking(long userId, BookingSearchState state) {

        return null;
    }
}
