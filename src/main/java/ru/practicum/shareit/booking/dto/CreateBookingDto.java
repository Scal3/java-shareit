package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
public class CreateBookingDto {

    @Positive
    private long itemId;

    @Future
    @NotNull
    private LocalDateTime start;

    @Future
    @NotNull
    private LocalDateTime end;
}
