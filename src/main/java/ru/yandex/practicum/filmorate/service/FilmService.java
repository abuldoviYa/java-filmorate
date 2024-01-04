package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmValidator filmValidator;
    private final Map<Integer, Film> filmMap = new HashMap<>();
    private int nextFilmId = 1;

    public Film addFilm(Film film) {
        filmValidator.validateFilm(film);

        Integer filmId = generateNextFilmId();
        film.setId(filmId);

        filmMap.put(filmId, film);
        log.info("Film added");
        return film;
    }

    public Film updateFilm(Film updatedFilm) {
        filmValidator.validateFilm(updatedFilm);
        Integer filmId = updatedFilm.getId();

        if (filmMap.containsKey(filmId)) {
            updatedFilm.setId(filmId);
            filmMap.put(filmId, updatedFilm);

            log.info("Film updated");

            return updatedFilm;
        } else {
            log.warn("No such id");
            throw new ValidationException("No such id yet");
        }
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(filmMap.values());
    }

    private synchronized Integer generateNextFilmId() {
        return nextFilmId++;
    }
}
