package ru.practicum.shareit.exception.handler;

import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.exceptionimp.*;
import ru.practicum.shareit.exception.response.ErrorResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleError(NotFoundException e) {
        return new ErrorResponse(
                e.getCode(),
                e.getError(),
                e.getDescription(),
                e.getTime()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleError(ConstraintViolationException e) {
        return new ErrorResponse(
                400,
                null,
                e.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResponse handleError(DataIntegrityViolationException e) {
        return new ErrorResponse(
                409,
                null,
                e.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleError(InternalServerException e) {
        return new ErrorResponse(
                e.getCode(),
                e.getError(),
                e.getDescription(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResponse handleError(ConflictException e) {
        return new ErrorResponse(
                e.getCode(),
                e.getError(),
                e.getDescription(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleError(BadRequestException e) {
        return new ErrorResponse(
                e.getCode(),
                e.getError(),
                e.getDescription(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ErrorResponse handleError(ForbiddenException e) {
        return new ErrorResponse(
                e.getCode(),
                e.getError(),
                e.getDescription(),
                LocalDateTime.now()
        );
    }
}
