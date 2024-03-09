package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.enums.BookingSearchState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BookingDto createBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid CreateBookingDto dto
    ) {
        return bookingService.createBooking(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved
    ) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId
    ) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllUserBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") BookingSearchState state
    ) {
        return bookingService.getAllUserBooking(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnerBooking(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "ALL") BookingSearchState state
    ) {
        return bookingService.getAllOwnerBooking(ownerId, state);
    }
}
