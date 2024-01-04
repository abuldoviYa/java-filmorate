package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
@Slf4j
public class FilmValidator {
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public void validateFilm(Film film) {
        validateName(film.getName());
        validateDescription(film.getDescription());
        validateReleaseDate(film.getReleaseDate());
        validateDuration(film.getDuration());
        log.info("Film validated");
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Film name cannot be empty");
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException("Description exceeds the maximum length of " + MAX_DESCRIPTION_LENGTH + " characters.");
        }
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate != null && releaseDate.isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Release date cannot be earlier than 28 December 1895.");
        }
    }

    private void validateDuration(int duration) {
        if (duration <= 0) {
            throw new ValidationException("Film duration must be a positive value.");
        }
    }
}
