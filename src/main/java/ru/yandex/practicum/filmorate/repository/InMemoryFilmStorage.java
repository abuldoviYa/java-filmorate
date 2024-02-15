package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmMap = new HashMap<>();
    private int nextFilmId = 1;

    public Film addFilm(Film film) {

        Integer filmId = generateNextFilmId();
        film.setId(filmId);

        filmMap.put(filmId, film);
        log.info("Film added");
        return film;
    }

    public Film updateFilm(Film updatedFilm) {
        Integer filmId = updatedFilm.getId();

        if (filmMap.containsKey(filmId)) {
            updatedFilm.setId(filmId);
            filmMap.put(filmId, updatedFilm);

            log.info("Film updated");

            return updatedFilm;
        } else {
            log.warn("No such id");
            throw new ValidationException("No such id yet", HttpStatus.NOT_FOUND);
        }
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film getFilm(Integer filmId) {
        if (filmMap.containsKey(filmId)) {
            return filmMap.get(filmId);
        }
        throw new ValidationException("No such film id", HttpStatus.NOT_FOUND);
    }

    private synchronized Integer generateNextFilmId() {
        return nextFilmId++;
    }
}
