package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.UserDbStorage;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureCache
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmRepositoryTests {

    private final UserDbStorage userStorage;
    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private User firstUser;
    private User secondUser;
    private User thirdUser;
    private Film firstFilm;
    private Film secondFilm;
    private Film thirdFilm;

    @BeforeEach
    public void beforeEach() {
        firstUser = User.builder()
                .name("MisterFirst")
                .login("First")
                .email("1@ya.ru")
                .birthday(LocalDate.of(1980, 12, 23))
                .build();

        secondUser = User.builder()
                .name("MisterSecond")
                .login("Second")
                .email("2@ya.ru")
                .birthday(LocalDate.of(1980, 12, 24))
                .build();

        thirdUser = User.builder()
                .name("MisterThird")
                .login("Third")
                .email("3@ya.ru")
                .birthday(LocalDate.of(1980, 12, 25))
                .build();

        firstFilm = Film.builder()
                .name("Breakfast at Tiffany's")
                .description("American romantic comedy film directed by Blake Edwards, written by George Axelrod," +
                        " adapted from Truman Capote's 1958 novella of the same name.")
                .releaseDate(LocalDate.of(1961, 10, 5))
                .duration(114)
                .build();
        firstFilm.setRating(new Rating(1, "G"));
        firstFilm.setLikedUserIds(new HashSet<>());
        firstFilm.setGenres(Arrays.asList(new Genre(2, "Драма"),
                new Genre(1, "Комедия")));

        secondFilm = Film.builder()
                .name("Avatar")
                .description("American epic science fiction film directed, written, produced, and co-edited" +
                        " by James Cameron. It is set in the mid-22nd century when humans are colonizing Pandora...")
                .releaseDate(LocalDate.of(2009, 12, 10))
                .duration(162)
                .build();
        secondFilm.setRating(new Rating(3, "PG-13"));
        secondFilm.setLikedUserIds(new HashSet<>());
        secondFilm.setGenres(Arrays.asList(new Genre(6, "Боевик")));

        thirdFilm = Film.builder()
                .name("One Flew Over the Cuckoo's Nest")
                .description("American psychological comedy drama film directed by Milos Forman, based on" +
                        " the 1962 novel of the same name by Ken Kesey. The film stars Jack Nicholson...")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .build();
        thirdFilm.setRating(new Rating(4, "R"));
        thirdFilm.setLikedUserIds(new HashSet<>());
        thirdFilm.setGenres(Arrays.asList(new Genre(2, "Драма")));
    }

    @Test
    public void createFilmAndGetFilmById_FilmCreatedAndRetrievedById() {
        firstFilm = filmStorage.addFilm(firstFilm);
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilm(firstFilm.getId()));
        assertThat(filmOptional)
                .hasValueSatisfying(film -> assertThat(film)
                        .hasFieldOrPropertyWithValue("id", firstFilm.getId())
                        .hasFieldOrPropertyWithValue("name", "Breakfast at Tiffany's")
                );
    }

    @Test
    public void getFilms_FilmListRetrievedSuccessfully() {
        firstFilm = filmStorage.addFilm(firstFilm);
        secondFilm = filmStorage.addFilm(secondFilm);
        thirdFilm = filmStorage.addFilm(thirdFilm);
        List<Film> listFilms = filmStorage.getAllFilms();
        AssertionsForInterfaceTypes.assertThat(listFilms).contains(firstFilm);
        AssertionsForInterfaceTypes.assertThat(listFilms).contains(secondFilm);
        AssertionsForInterfaceTypes.assertThat(listFilms).contains(thirdFilm);
    }

    @Test
    public void updateFilm_FilmUpdatedSuccessfully() {
        firstFilm = filmStorage.addFilm(firstFilm);
        Film updateFilm = Film.builder()
                .id(firstFilm.getId())
                .name("UpdateName")
                .description("UpdateDescription")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .build();
        updateFilm.setRating(new Rating(1, "G"));
        Optional<Film> testUpdateFilm = Optional.ofNullable(filmStorage.updateFilm(updateFilm));
        assertThat(testUpdateFilm)
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "UpdateName")
                                .hasFieldOrPropertyWithValue("description", "UpdateDescription")
                );
    }

    @Test
    public void addLike_LikeAddedSuccessfully() {
        firstUser = userStorage.createUser(firstUser);
        firstFilm = filmStorage.addFilm(firstFilm);
        filmService.likeFilm(firstFilm.getId(), firstUser.getId());
        firstFilm = filmStorage.getFilm(firstFilm.getId());
        AssertionsForInterfaceTypes.assertThat(firstFilm.getLikedUserIds()).hasSize(1);
        AssertionsForInterfaceTypes.assertThat(firstFilm.getLikedUserIds()).contains(firstUser.getId());
    }

    @Test
    public void getPopularFilms_PopularFilmsRetrievedSuccessfully() {

        firstUser = userStorage.createUser(firstUser);
        secondUser = userStorage.createUser(secondUser);
        thirdUser = userStorage.createUser(thirdUser);

        firstFilm = filmStorage.addFilm(firstFilm);
        filmService.likeFilm(firstFilm.getId(), firstUser.getId());

        secondFilm = filmStorage.addFilm(secondFilm);
        filmService.likeFilm(secondFilm.getId(), firstUser.getId());
        filmService.likeFilm(secondFilm.getId(), secondUser.getId());
        filmService.likeFilm(secondFilm.getId(), thirdUser.getId());

        thirdFilm = filmStorage.addFilm(thirdFilm);
        filmService.likeFilm(thirdFilm.getId(), firstUser.getId());
        filmService.likeFilm(thirdFilm.getId(), secondUser.getId());

        List<FilmDTO> listFilms = filmService.getPopularFilms(5);

        AssertionsForInterfaceTypes.assertThat(listFilms).hasSize(3);

        assertThat(Optional.of(listFilms.get(0)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Avatar"));

        assertThat(Optional.of(listFilms.get(1)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "One Flew Over the Cuckoo's Nest"));

        assertThat(Optional.of(listFilms.get(2)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Breakfast at Tiffany's"));
    }

}
