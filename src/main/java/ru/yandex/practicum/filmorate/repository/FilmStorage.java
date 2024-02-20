package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film updatedFilm);

    List<Film> getAllFilms();

    Film getFilm(Integer filmId);

    List<Rating> getAllRatings();

    Rating getRatingById(Integer id);

    Genre getGenreById(Integer id);

    List<Genre> getAllGenres();

}
