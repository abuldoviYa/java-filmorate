package ru.yandex.practicum.filmorate.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(value = {ValidationException.class})
    protected ResponseEntity<ErrorWrapper> handleValidationException(ValidationException ex) {
        return new ResponseEntity<>(new ErrorWrapper(ex.getMessage()), ex.getStatus());
    }
}
