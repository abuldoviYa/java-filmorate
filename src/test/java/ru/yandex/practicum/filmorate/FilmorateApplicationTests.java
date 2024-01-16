package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.yandex.practicum.filmorate.DTO.UserDTO;
import ru.yandex.practicum.filmorate.DTO.FilmDTO;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDateValidator;

import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

    private static LocalValidatorFactoryBean validator;
    private static FilmReleaseDateValidator filmValidator;

    @BeforeAll
    static void setUpValidator() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        filmValidator = new FilmReleaseDateValidator();
    }

    @Test
    void validUserDTOShouldPassValidation() {
        UserDTO user = new UserDTO();
        user.setEmail("test@example.com");
        user.setLogin("validLogin");
        user.setName("John Doe");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidEmailShouldFailValidation() {
        UserDTO user = new UserDTO();
        user.setEmail("invalid-email");
        user.setLogin("validLogin");
        user.setName("John Doe");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void blankLoginShouldFailValidation() {
        UserDTO user = new UserDTO();
        user.setEmail("test@example.com");
        user.setLogin("");
        user.setName("John Doe");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void futureBirthDateShouldFailValidation() {
        UserDTO user = new UserDTO();
        user.setEmail("test@example.com");
        user.setLogin("validLogin");
        user.setName("John Doe");
        user.setBirthday(LocalDate.now().plusDays(1)); // Set future birth date
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void nullEmailShouldFailValidation() {
        UserDTO user = new UserDTO();
        user.setLogin("validLogin");
        user.setName("John Doe");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void validFilmDTOShouldPassValidation() {
        FilmDTO film = new FilmDTO();
        film.setName("Interstellar");
        film.setDescription("A space epic");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(169);
        Set<ConstraintViolation<FilmDTO>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankNameShouldFailValidation() {
        FilmDTO film = new FilmDTO();
        film.setName("");
        film.setDescription("A space epic");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(169);
        Set<ConstraintViolation<FilmDTO>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void longDescriptionShouldFailValidation() {
        FilmDTO film = new FilmDTO();
        film.setName("Inception");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2010, 7, 8));
        film.setDuration(148);
        Set<ConstraintViolation<FilmDTO>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void zeroDurationShouldFailValidation() {
        FilmDTO film = new FilmDTO();
        film.setName("The Dark Knight");
        film.setDescription("A superhero film");
        film.setReleaseDate(LocalDate.of(2008, 7, 18));
        film.setDuration(0);
        Set<ConstraintViolation<FilmDTO>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void negativeDurationShouldFailValidation() {
        FilmDTO film = new FilmDTO();
        film.setName("Pulp Fiction");
        film.setDescription("A crime film");
        film.setReleaseDate(LocalDate.of(1994, 5, 12));
        film.setDuration(-154);
        Set<ConstraintViolation<FilmDTO>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void releaseDateBefore1895ShouldFailValidation() {
        FilmDTO film = new FilmDTO();
        film.setName("Early FilmDTO");
        film.setDescription("A film from the 1800s");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(120);
        Set<ConstraintViolation<FilmDTO>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void nullReleaseDateShouldNotPassValidation() {
        FilmDTO film = new FilmDTO();
        film.setName("No Release Date FilmDTO");
        film.setDescription("A film without a release date");
        film.setDuration(90);
        Set<ConstraintViolation<FilmDTO>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

}
