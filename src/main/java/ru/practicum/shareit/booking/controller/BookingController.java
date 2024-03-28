package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BookingDto createBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid CreateBookingDto dto
    ) {
        return bookingService.createBooking(userId, dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved
    ) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId
    ) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<BookingDto> getAllUserBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "15") @Positive int size
    ) {
        return bookingService.getAllUserBooking(userId, state, from, size);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/owner")
    public List<BookingDto> getAllOwnerBooking(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "15") @Positive int size
    ) {
        return bookingService.getAllOwnerBooking(ownerId, state, from, size);
    }
}
