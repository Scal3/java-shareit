package ru.practicum.shareit.exception.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ErrorResponse  {

    private final int code;

    private final String error;

    private final LocalDateTime time;
}