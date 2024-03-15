package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.exceptionimp.*;
import ru.practicum.shareit.exception.response.ErrorResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleError(NotFoundException e) {
        log.warn("Error has occurred {}", e.getDescription());

        return new ErrorResponse(
                e.getCode(),
                e.getDescription(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleError(ConstraintViolationException e) {
        log.warn("Error has occurred {}", e.getMessage());

        return new ErrorResponse(
                400,
                e.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResponse handleError(DataIntegrityViolationException e) {
        log.warn("Error has occurred {}", e.getMessage());

        return new ErrorResponse(
                409,
                e.getMessage(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleError(InternalServerException e) {
        return new ErrorResponse(
                e.getCode(),
                e.getDescription(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResponse handleError(ConflictException e) {
        log.warn("Error has occurred {}", e.getDescription());

        return new ErrorResponse(
                e.getCode(),
                e.getDescription(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleError(BadRequestException e) {
        log.warn("Error has occurred {}", e.getDescription());

        return new ErrorResponse(
                e.getCode(),
                e.getDescription(),
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ErrorResponse handleError(ForbiddenException e) {
        log.warn("Error has occurred {}", e.getDescription());

        return new ErrorResponse(
                e.getCode(),
                e.getDescription(),
                LocalDateTime.now()
        );
    }
}
