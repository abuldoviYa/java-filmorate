package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film updatedFilm);

    List<Film> getAllFilms();

    Film getFilm(Integer filmId);

}
