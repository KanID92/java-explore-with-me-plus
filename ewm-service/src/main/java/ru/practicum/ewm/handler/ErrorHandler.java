package ru.practicum.ewm.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exception.AccessException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final MethodArgumentNotValidException e) {
        log.warn("Validation error", e);
        return createApiError(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleValidationException(final ConstraintViolationException e) {
        log.warn("Conflict", e);
        return createApiError(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAccessException(final AccessException e) {
        log.info("Access denied: {}", e.getMessage());
        return createApiError(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        log.warn("Error", e);
        return createApiError(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ApiError createApiError(Exception e, HttpStatus status) {
        return new ApiError(
                e.getStackTrace(),
                e.getMessage(),
                e.getCause().toString(),
                status
        );
    }


}
