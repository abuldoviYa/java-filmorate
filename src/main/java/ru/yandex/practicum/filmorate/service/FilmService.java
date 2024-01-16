package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.DTO.FilmDTO;
import ru.yandex.practicum.filmorate.repository.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;

    public FilmDTO addFilm(FilmDTO filmDTO) {
        return filmStorage.addFilm(filmDTO);
    }

    public FilmDTO updateFilm(FilmDTO updatedFilmDTO) {
        return filmStorage.updateFilm(updatedFilmDTO);
    }

    public List<FilmDTO> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void likeFilm(Integer filmId, Integer userId) {
        filmStorage.likeFilm(filmId, userId);
    }

    public FilmDTO getFilm(Integer filmId) {
        return filmStorage.getFilm(filmId);
    }

    public void unlikeFilm(Integer filmId, Integer userId) {
        filmStorage.unlikeFilm(filmId, userId);
    }

    public List<FilmDTO> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }
}
