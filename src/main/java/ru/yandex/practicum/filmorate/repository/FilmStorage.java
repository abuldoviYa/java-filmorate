package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.DTO.FilmDTO;

import java.util.List;

public interface FilmStorage {
    FilmDTO addFilm(FilmDTO filmDTO);

    FilmDTO updateFilm(FilmDTO updatedFilmDTO);

    List<FilmDTO> getAllFilms();

    void likeFilm(Integer filmId, Integer userId);

    void unlikeFilm(Integer filmId, Integer userId);

    List<FilmDTO> getPopularFilms(int count);

    FilmDTO getFilm(Integer filmId);

}
