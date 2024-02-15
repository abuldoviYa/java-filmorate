package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public ResponseEntity<FilmDTO> addFilm(@Valid @RequestBody FilmDTO film) {
        log.info("Received a request to add a new FilmDTO.");
        FilmDTO addedFilm = filmService.addFilm(film);
        return new ResponseEntity<>(addedFilm, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<FilmDTO> updateFilm(@Valid @RequestBody FilmDTO updatedFilm) {
        log.info("Received a request to update a film with ID: {}", updatedFilm.getId());
        FilmDTO film = filmService.updateFilm(updatedFilm);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<FilmDTO>> getAllFilms() {
        log.info("Received a request to retrieve all films.");
        List<FilmDTO> films = filmService.getAllFilms();
        return new ResponseEntity<>(films, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmDTO> getFilm(@PathVariable Integer id) {
        log.info("Received a request to retrieve film.");
        FilmDTO film = filmService.getFilm(id);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<String> likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Received a request to add like to film.");
        filmService.likeFilm(id, userId);
        return ResponseEntity.ok("Film liked successfully.");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<String> unlikeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Received a request to add unlike to film.");
        filmService.unlikeFilm(id, userId);
        return ResponseEntity.ok("Like removed successfully.");
    }

    @GetMapping("/popular")
    public ResponseEntity<List<FilmDTO>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Received a request to get popular films.");
        List<FilmDTO> popularFilms = filmService.getPopularFilms(count);
        return ResponseEntity.ok(popularFilms);
    }
}
