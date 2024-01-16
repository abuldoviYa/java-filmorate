package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.DTO.FilmDTO;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmMapper filmMapper;
    private final Map<Integer, Film> filmMap = new HashMap<>();
    private int nextFilmId = 1;

    public FilmDTO addFilm(FilmDTO filmDTO) {
        Film film = filmMapper.toModel(filmDTO);

        Integer filmId = generateNextFilmId();
        film.setId(filmId);

        filmMap.put(filmId, film);
        log.info("Film added");
        return filmMapper.toDto(film);
    }

    public FilmDTO updateFilm(FilmDTO updatedFilmDTO) {
        Film updatedFilm = filmMapper.toModel(updatedFilmDTO);
        Integer filmId = updatedFilm.getId();

        if (filmMap.containsKey(filmId)) {
            updatedFilm.setId(filmId);
            filmMap.put(filmId, updatedFilm);

            log.info("Film updated");

            return filmMapper.toDto(updatedFilm);
        } else {
            log.warn("No such id");
            throw new ValidationException("No such id yet", HttpStatus.NOT_FOUND);
        }
    }

    public List<FilmDTO> getAllFilms() {
        return filmMap.values().stream().map(filmMapper::toDto).collect(Collectors.toList());
    }

    private synchronized Integer generateNextFilmId() {
        return nextFilmId++;
    }
}
