package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.DTO.FilmDTO;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;

@Component
public class FilmMapper {

    public Film toModel(FilmDTO filmDTO) {
        if (filmDTO == null) {
            return null;
        }

        Film film = new Film();
        film.setId(filmDTO.getId());
        film.setName(filmDTO.getName());
        film.setDescription(filmDTO.getDescription());
        film.setReleaseDate(filmDTO.getReleaseDate());
        film.setDuration(filmDTO.getDuration());
        if (filmDTO.getLikedUserIds() == null) {
            film.setLikedUserIds(new HashSet<>());
        } else {
            film.setLikedUserIds(filmDTO.getLikedUserIds());
        }

        return film;
    }

    public FilmDTO toDto(Film film) {
        if (film == null) {
            return null;
        }

        FilmDTO filmDTO = new FilmDTO();
        filmDTO.setId(film.getId());
        filmDTO.setName(film.getName());
        filmDTO.setDescription(film.getDescription());
        filmDTO.setReleaseDate(film.getReleaseDate());
        filmDTO.setDuration(film.getDuration());
        if (film.getLikedUserIds() == null) {
            filmDTO.setLikedUserIds(new HashSet<>());
        } else {
            filmDTO.setLikedUserIds(film.getLikedUserIds());
        }

        return filmDTO;
    }

}
