package ru.practicum.shareit.exception.exceptionimp;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class BaseAppException extends ResponseStatusException {

    private final int code;

    private final String error;

    private final String description;

    public BaseAppException(HttpStatus status, String reason) {
        super(status, reason);
        this.description =  reason;
        this.code = status.value();
        this.error = status.getReasonPhrase();
    }
}