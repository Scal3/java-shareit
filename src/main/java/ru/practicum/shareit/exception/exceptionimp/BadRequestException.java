package ru.practicum.shareit.exception.exceptionimp;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseAppException {

    public BadRequestException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);

    }
}
