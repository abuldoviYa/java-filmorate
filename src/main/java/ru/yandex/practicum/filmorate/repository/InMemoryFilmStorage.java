package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.DTO.FilmDTO;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

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

    public void likeFilm(Integer filmId, Integer userId) {
        Film film = filmMap.get(filmId);

        if (film != null) {
            Set<Integer> likedUserIds = film.getLikedUserIds();
            likedUserIds.add(userId);
            log.info("User {} liked film {}", userId, filmId);
        } else {
            log.warn("No such film id");
            throw new ValidationException("No such film id", HttpStatus.NOT_FOUND);
        }
    }

    public void unlikeFilm(Integer filmId, Integer userId) {
        Film film = filmMap.get(filmId);

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
        List<FilmDTO> popularFilms = filmMap.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikedUserIds().size(), f1.getLikedUserIds().size()))
                .limit(count)
                .map(filmMapper::toDto)
                .collect(Collectors.toList());

        log.info("Retrieved popular films");
        return popularFilms;
    }

    @Override
    public FilmDTO getFilm(Integer filmId) {
        if (filmMap.containsKey(filmId)) {
            return filmMapper.toDto(filmMap.get(filmId));
        }
        throw new ValidationException("No such film id", HttpStatus.NOT_FOUND);
    }

    private synchronized Integer generateNextFilmId() {
        return nextFilmId++;
    }
}
