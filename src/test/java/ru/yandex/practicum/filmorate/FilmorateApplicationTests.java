package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {

	private final FilmValidator filmValidator = new FilmValidator();
	private int MAX_DESCRIPTION_LENGTH = 200;

	private final UserValidator userValidator = new UserValidator();

	@Test
	void validateFilm_ValidFilm_NoExceptionThrown() {
		Film validFilm = createValidFilm();
		filmValidator.validateFilm(validFilm); // Should not throw any exception
	}

	@Test
	void validateName_NullName_ValidationExceptionThrown() {
		Film filmWithNullName = createValidFilm();
		filmWithNullName.setName(null);

		assertThrows(ValidationException.class, () -> filmValidator.validateFilm(filmWithNullName));
	}

	@Test
	void validateName_EmptyName_ValidationExceptionThrown() {
		Film filmWithEmptyName = createValidFilm();
		filmWithEmptyName.setName("");

		assertThrows(ValidationException.class, () -> filmValidator.validateFilm(filmWithEmptyName));
	}

	@Test
	void validateDescription_LongDescription_ValidationExceptionThrown() {
		Film filmWithLongDescription = createValidFilm();
		filmWithLongDescription.setDescription("A".repeat(MAX_DESCRIPTION_LENGTH + 1));

		assertThrows(ValidationException.class, () -> filmValidator.validateFilm(filmWithLongDescription));
	}

	@Test
	void validateReleaseDate_WrongReleaseDate_ValidationExceptionThrown() {
		Film filmWithWrongReleaseDate = createValidFilm();
		filmWithWrongReleaseDate.setReleaseDate(LocalDate.of(1890, 12, 28));

		assertThrows(ValidationException.class, () -> filmValidator.validateFilm(filmWithWrongReleaseDate));
	}

	@Test
	void validateDuration_NegativeDuration_ValidationExceptionThrown() {
		Film filmWithNegativeDuration = createValidFilm();
		filmWithNegativeDuration.setDuration(-1);

		assertThrows(ValidationException.class, () -> filmValidator.validateFilm(filmWithNegativeDuration));
	}

	private Film createValidFilm() {
		Film validFilm = new Film();
		validFilm.setName("Valid Film");
		validFilm.setDescription("Short description");
		validFilm.setReleaseDate(LocalDate.of(2022, 1, 1));
		validFilm.setDuration(120);
		return validFilm;
	}


	@Test
	void validate_ValidUser_NoExceptionThrown() {
		User validUser = createValidUser();
		assertDoesNotThrow(() -> userValidator.validate(validUser));
	}

	@Test
	void validateEmail_InvalidEmailFormat_ValidationExceptionThrown() {
		User userWithInvalidEmail = createValidUser();
		userWithInvalidEmail.setEmail("invalid.email");

		assertThrows(ValidationException.class, () -> userValidator.validate(userWithInvalidEmail));
	}

	@Test
	void validateLogin_InvalidLoginFormat_ValidationExceptionThrown() {
		User userWithInvalidLogin = createValidUser();
		userWithInvalidLogin.setLogin("invalid login");

		assertThrows(ValidationException.class, () -> userValidator.validate(userWithInvalidLogin));
	}

	@Test
	void validateBirthday_FutureBirthday_ValidationExceptionThrown() {
		User userWithFutureBirthday = createValidUser();
		userWithFutureBirthday.setBirthday(LocalDate.now().plusDays(1));

		assertThrows(ValidationException.class, () -> userValidator.validate(userWithFutureBirthday));
	}

	private User createValidUser() {
		User validUser = new User();
		validUser.setEmail("valid.email@example.com");
		validUser.setLogin("validLogin");
		validUser.setBirthday(LocalDate.of(1990, 1, 1));
		return validUser;
	}

}
