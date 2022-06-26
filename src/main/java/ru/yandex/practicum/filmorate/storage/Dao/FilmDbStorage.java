package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Slf4j
@Repository
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDaoStorage genre;
    private static final String SQL_INSERT_FILM =
            "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES( ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String SQL_MERGE_GENRE_FOR_FILM = "MERGE INTO film_genre (film_id, genre_id) " +
            "KEY (film_id, genre_id) VALUES (?, ?)";
    private static final String SQL_GET_FILM = "SELECT * FROM films WHERE film_id = ?";
    private static final String SQL_GET_ALL_FILMS = "SELECT * FROM films";
    private static final String SQL_GET_POPULAR_FILMS =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, COUNT (l.user_id) " +
                    "FROM films f LEFT JOIN likes AS l ON f.film_id = l.film_id GROUP BY f.film_id  " +
                    "ORDER BY COUNT (l.user_id) DESC LIMIT ?";
    private static final String SQL_GET_MPA_FOR_FILM = "SELECT f.mpa_id, m.name FROM films AS f " +
            "LEFT OUTER JOIN mpa AS m ON f.mpa_id = m.mpa_id WHERE f.film_id=?";
    private static final String SQL_GET_GENRE_FOR_FILM = "SELECT fg.genre_id, g.name FROM film_genre AS fg " +
            "LEFT OUTER JOIN genre AS g ON fg.genre_id = g.genre_id WHERE fg.film_id=?";


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDaoStorage genre) {
        this.jdbcTemplate = jdbcTemplate;
        this.genre = genre;
    }

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement stmt = connection.prepareStatement(SQL_INSERT_FILM, new String[]{"film_id"});
                        stmt.setString(1, film.getName());
                        stmt.setString(2, film.getDescription());
                        stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                        stmt.setLong(4, film.getDuration());
                        stmt.setLong(5, film.getMpa().getId());
                        return stmt;
                    }, keyHolder);
            film.setId(keyHolder.getKey().longValue());
            linkFilmGenre(film);
            log.info("Добавлен фильм с идентификатором: {}", film.getId());
            return film;
        } catch (DuplicateKeyException e) {
            if (e.toString().contains("film_id")) {
                log.warn("Попытка добавления фильма с существующем идентификатором: {}", film.getId());
                throw new FilmAlreadyExistException("Filmorate содержит фильм с идентификатором: " + film.getId());
            } else if (e.toString().contains("IDX_FILMS_NAME_DATE")) {
                log.warn("Попытка добавления фильма с существующем названием {} и датой релиза: {}.",
                        film.getName(), film.getId());
                throw new FilmAlreadyExistException("Фильм с аналогичным названием и датой релиза уже существует.");
            } else {
                throw new UnsupportedOperationException(e.getMessage());
            }
        }
    }

    @Override
    public Film put(Film film) {
        if (film.getId() != null && getById(film.getId()) != null) {
            jdbcTemplate.update(SQL_UPDATE,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId()
            );
            linkFilmGenre(film);
            log.info("Обновлены данные фильма: {}.", film.getName());
            return film;
        } else {
            return null;
        }
    }

    @Override
    public Collection<Film> findAll() {
        return jdbcTemplate.query(SQL_GET_ALL_FILMS, this::mapRowToFilm);
    }

    @Override
    public Film getById(Long id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_FILM, id);
        if (rowSet.next()) {
            Film film = new Film();
            film.setId(rowSet.getLong("film_id"));
            film.setName(rowSet.getString("name"));
            film.setDescription(rowSet.getString("description"));
            film.setReleaseDate(rowSet.getDate("release_date").toLocalDate());
            film.setDuration(rowSet.getInt("duration"));
            film.setMpa(getFilmMpa(id));
            film.setGenre(getFilmGenres(id));
            log.info("Получен фильм с идентификатором {}.", id);
            return film;
        } else {
            throw new UserNotFoundException("В Filmorate отсутствует фильм с идентификатором № " + id);
        }
    }

    @Override
    public Collection<Film> getPopular(int count) {
        log.info("Составлен список популярных фильмов");
        return jdbcTemplate.query(SQL_GET_POPULAR_FILMS, this::mapRowToFilm, count == 0 ? 10 : count);
    }

    private Mpa getFilmMpa(Long id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_MPA_FOR_FILM, id);
        if (rowSet.next()) {
            Mpa mpa = new Mpa(rowSet.getInt("mpa_id"), rowSet.getString("name"));
            return mpa;
        } else {
            return null;
        }
    }

    private Set<Genre> getFilmGenres(Long id) {
        List<Genre> genresList = jdbcTemplate.query(SQL_GET_GENRE_FOR_FILM, (rs, rowNum) ->
                new Genre(rs.getInt("genre_id"), rs.getString("name")), id);
        if (!genresList.isEmpty()) {
            return new TreeSet<>(genresList);
        } else {
            return null;
        }
    }

    private void linkFilmGenre(Film film) {
        if (film.getGenre() != null && !film.getGenre().isEmpty()) {
            for (Genre genre : film.getGenre()) {
                jdbcTemplate.update(SQL_MERGE_GENRE_FOR_FILM, film.getId(), genre.getId());
            }
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Long filmId = resultSet.getLong("film_id");
        return Film.builder()
                .id(filmId)
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(new Mpa(resultSet.getInt("mpa_id")))
                .build();
    }
}