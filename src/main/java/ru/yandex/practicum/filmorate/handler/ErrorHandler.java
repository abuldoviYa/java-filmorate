package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@ControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(value = {ValidationException.class})
    protected ResponseEntity<ErrorWrapper> handleValidationException(ValidationException ex) {
        log.warn("Validation error: " + ex.getMessage());
        return new ResponseEntity<>(new ErrorWrapper(ex.getMessage()), ex.getStatus());
    }
}
