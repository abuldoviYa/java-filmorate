package ru.yandex.practicum.filmorate.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AppException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "INSERT INTO FILM (FIlM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRating().getId().intValue());
            return stmt;
        }, keyHolder);
        Integer filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        insertFilmGenre(filmId, getGenreIdList(film.getGenres()));
        return findById(filmId);
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        SqlRowSet userRS = jdbcTemplate.queryForRowSet("SELECT * FROM FILM WHERE FILM_ID = ?", updatedFilm.getId());
        if (userRS.next()) {
            String sqlQuery = "UPDATE FILM set FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, " +
                    "RATING_ID = ? where FILM_ID = ?";
            jdbcTemplate.update(sqlQuery, updatedFilm.getName(), updatedFilm.getDescription(), updatedFilm.getReleaseDate(), updatedFilm.getDuration(),
                    updatedFilm.getRating().getId(), updatedFilm.getId());
            updateFilmGenre(updatedFilm);
            updateFilmUsersLikes(updatedFilm);
            return findById(updatedFilm.getId());
        } else throw new AppException("Нет фильма с таким id", HttpStatus.NOT_FOUND);
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILM");
        while (filmRows.next()) {
            Film film = makeFilm(filmRows);
            films.add(film);
        }
        return films;
    }

    @Override
    public Film getFilm(Integer filmId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILM WHERE FILM_ID = ?", filmId);
        if (userRows.next()) {
            Film film = makeFilm(userRows);
            return film;
        } else {
            throw new AppException("Не найден фильм с таким номером", HttpStatus.NOT_FOUND);
        }
    }

    public List<Genre> getAllGenres() {
        List<Genre> filmGenres = new ArrayList<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE ORDER BY GENRE_ID");
        while (genreRows.next()) {
            Genre genre = makeGenre(genreRows);
            filmGenres.add(genre);
        }
        return filmGenres;
    }

    public Genre getGenreById(Integer id) {
        SqlRowSet genreRows =
                jdbcTemplate.queryForRowSet("SELECT *  FROM GENRE WHERE GENRE_ID = ? ORDER BY GENRE_ID", id);
        if (genreRows.next()) {
            return makeGenre(genreRows);
        } else
            throw new AppException("Не найден жанр с таким номером", HttpStatus.NOT_FOUND);
    }

    public List<Rating> getAllRatings() {
        List<Rating> filmRatings = new ArrayList<>();
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet("SELECT * FROM RATING ORDER BY RATING_ID");
        while (ratingRows.next()) {
            Rating Rating = makeRating(ratingRows);
            filmRatings.add(Rating);
        }
        return filmRatings;
    }

    public Film findById(Integer id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILM WHERE FILM_ID = ?", id);
        if (userRows.next()) {
            Film film = makeFilm(userRows);
            return film;
        } else {
            throw new AppException("Не найден фильм с таким номером", HttpStatus.NOT_FOUND);
        }
    }

    public Rating getRatingById(Integer id) {
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet("SELECT * FROM RATING WHERE RATING_ID = ?", id);
        if (ratingRows.next()) {
            return makeRating(ratingRows);
        } else
            throw new AppException("Не найден рейтинг с таким номером", HttpStatus.NOT_FOUND);
    }

    private void updateFilmUsersLikes(Film film) {
        deleteFromFilmUsersLikes(film);
        String sqlQuery = "INSERT INTO FILM_USERS_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
        for (Integer userId : film.getLikedUserIds()) {
            jdbcTemplate.update(sqlQuery, film.getId(), userId);
        }
    }

    private void updateFilmGenre(Film film) {
        deleteFromFilmGenre(film);
        String sqlQuery = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
        Set<Genre> genres = new HashSet<>(film.getGenres());
        for (Genre genre : genres) {
            jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
        }
    }

    private void deleteFromFilm(Film film) {
        String sqlQuery = "DELETE FROM FILM WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private void deleteFromFilmGenre(Film film) {
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private void deleteFromFilmUsersLikes(Film film) {
        String sqlQuery = "DELETE FROM FILM_USERS_LIKES WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private Film makeFilm(SqlRowSet rs) {
        Integer filmId = rs.getInt("FILM_ID");
        String name = rs.getString("FILM_NAME");
        String description = rs.getString("DESCRIPTION");
        LocalDate releaseDate = Objects.requireNonNull(rs.getDate("RELEASE_DATE")).toLocalDate();
        Integer duration = rs.getInt("DURATION");
        Set<Integer> likes = getLikes(filmId);
        List<Genre> genre = getGenres(filmId);
        Rating Rating = getRatingById(rs.getInt("RATING_ID"));
        return new Film(filmId, name, description, releaseDate, duration, likes, genre, Rating);
    }

    private Rating makeRating(SqlRowSet rs) {
        Integer ratingId = rs.getInt("RATING_ID");
        String ratingName = rs.getString("RATING_NAME");
        return new Rating(ratingId, ratingName);
    }

    private Genre makeGenre(SqlRowSet rs) {
        Integer genreId = rs.getInt("GENRE_ID");
        String genreName = rs.getString("GENRE_NAME");
        return new Genre(genreId, genreName);
    }

    private void insertFilmGenre(Integer filmId, Set<Integer> genreIdList) {
        String sqlQuery = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
        for (Integer id : genreIdList) {
            jdbcTemplate.update(sqlQuery, filmId, id);
        }
    }

    private Set<Integer> getLikes(Integer filmId) {
        String sql = "SELECT USER_ID FROM FILM_USERS_LIKES WHERE FILM_ID = ?";
        List<Integer> likes = jdbcTemplate.queryForList(sql, Integer.class, filmId);
        return new HashSet<>(likes);
    }

    private List<Genre> getGenres(Integer filmId) {
        SqlRowSet genreRows =
                jdbcTemplate.queryForRowSet("SELECT g.GENRE_ID, g.GENRE_NAME FROM FILM_GENRE AS f " +
                        "LEFT JOIN GENRE AS g ON g.GENRE_ID = f.GENRE_ID" +
                        " WHERE FILM_ID = ?" +
                        "ORDER BY g.GENRE_ID", filmId);
        List<Genre> genres = new ArrayList<>();
        while (genreRows.next()) {
            Genre genre = makeGenre(genreRows);
            genres.add(genre);
        }
        return genres;
    }

    private Set<Integer> getGenreIdList(List<Genre> genreList) {
        Set<Integer> genreIdList = new HashSet<>();
        if (Objects.isNull(genreList)) {
            return genreIdList;
        } else if (genreList.size() != 0) {
            for (Genre genre : genreList) {
                genreIdList.add(genre.getId());
            }
        }
        return genreIdList;
    }
}
