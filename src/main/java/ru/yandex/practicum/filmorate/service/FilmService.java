package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.repository.FilmStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {


    private final FilmStorage filmStorage;

    private final FilmMapper filmMapper;

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, FilmMapper filmMapper) {
        this.filmStorage = filmStorage;
        this.filmMapper = filmMapper;
    }

    public FilmDTO addFilm(FilmDTO filmDTO) {
        return filmMapper.toDto(filmStorage.addFilm(filmMapper.toModel(filmDTO)));
    }

    public FilmDTO updateFilm(FilmDTO updatedFilmDTO) {
        return filmMapper.toDto(filmStorage.updateFilm(filmMapper.toModel(updatedFilmDTO)));
    }

    public List<FilmDTO> getAllFilms() {
        return filmStorage.getAllFilms().stream().map(filmMapper::toDto).collect(Collectors.toList());
    }

    public FilmDTO getFilm(Integer filmId) {
        return filmMapper.toDto(filmStorage.getFilm(filmId));
    }


    public void likeFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilm(filmId);

        if (film != null) {
            Set<Integer> likedUserIds = film.getLikedUserIds();
            likedUserIds.add(userId);
            filmStorage.updateFilm(film);
            log.info("User {} liked film {}", userId, filmId);
        } else {
            log.warn("No such film id");
            throw new ValidationException("No such film id", HttpStatus.NOT_FOUND);
        }
    }

    public void unlikeFilm(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilm(filmId);

        if (film != null) {
            Set<Integer> likedUserIds = film.getLikedUserIds();
            if (!likedUserIds.contains(userId)) {
                throw new ValidationException("No such user", HttpStatus.NOT_FOUND);
            }
            likedUserIds.remove(userId);
            log.info("User {} unliked film {}", userId, filmId);
        } else {
            log.warn("No such film id");
            throw new ValidationException("No such film id", HttpStatus.NOT_FOUND);
        }
    }


    public List<FilmDTO> getPopularFilms(int count) {
        List<FilmDTO> popularFilms = filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikedUserIds().size(), f1.getLikedUserIds().size()))
                .limit(count)
                .map(filmMapper::toDto).collect(Collectors.toList());

        log.info("Retrieved popular films");
        return popularFilms;
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(Integer id) {
        return filmStorage.getGenreById(id);
    }

    public List<Rating> getAllRatings() {
        return filmStorage.getAllRatings();
    }

    public Rating getRatingById(Integer id) {
        return filmStorage.getRatingById(id);
    }
}
