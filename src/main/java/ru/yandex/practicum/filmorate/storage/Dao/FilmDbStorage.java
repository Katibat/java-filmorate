package ru.yandex.practicum.filmorate.storage;

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
import ru.yandex.practicum.filmorate.storage.Dao.LikesDaoLink;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Slf4j
@Repository
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDaoLink genre;
    private final LikesDaoLink likes;
    private static final String SQL_INSERT_FILM =
            "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES( ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String SQL_GET_FILM = "SELECT * FROM films WHERE film_id = ?";
    private static final String SQL_GET_ALL_FILMS = "SELECT * FROM films";
    private static final String SQL_GET_POPULAR_FILMS =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, COUNT (l.user_id) " +
                    "FROM films f LEFT JOIN likes AS l ON f.film_id = l.film_id GROUP BY f.film_id  " +
                    "ORDER BY COUNT (l.user_id) DESC LIMIT ?";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDaoLink genre, LikesDaoLink likes) {
        this.jdbcTemplate = jdbcTemplate;
        this.genre = genre;
        this.likes = likes;
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
            log.info("Добавлен фильм с идентификатором: {}", film.getId());
            if(film.getGenre() != null)
                genre.merge(film.getId(), film.getGenre());
            return film;
        } catch (DuplicateKeyException e) {
            if (e.toString().contains("film_id")) {
                log.warn("Попытка добавления фильма с существующем идентификатором: {}", film.getId());
                throw new FilmAlreadyExistException("Filmorate содержит фильм с идентификатором: " + film.getId());
            } else if (e.toString().contains("IDX_FILM_NAME_DATE")) {
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
        try {
            jdbcTemplate.update(SQL_UPDATE,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId()
            );
            genre.merge(film.getId(), film.getGenre());

            if (film.getGenre() != null) {
                if (!film.getGenre().isEmpty()) {
                    film.setGenre(genre.getAllGenresForFilm(film.getId()));
                }
            }
            log.info("Обновлены данные фильма: {}.", film.getName());
            return film;
        } catch (DuplicateKeyException e) {
            if (e.toString().contains("film_id")) {
                log.warn("Попытка обновления фильма с несуществующим идентификатором: {}.", film.getId());
                throw new FilmAlreadyExistException("В Filmorate отсутствует фильм с идентификатором № " + film.getId());
            } else if (e.toString().contains("IDX_FILM_NAME_DATE")) {
                log.warn("Попытка обновления фильма с уже существующим названием: {}.", film.getName());
                throw new FilmAlreadyExistException("Фильм с аналогичным названием и датой релиза уже существует.");
            } else {
                throw new UnsupportedOperationException(e.getMessage());
            }
        }
    }

    @Override
    public Collection<Film> findAll() {
        return jdbcTemplate.query(SQL_GET_ALL_FILMS, this::mapRowToFilm);
    }

    @Override
    public Optional<Film> getById(Long id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_FILM, id);
        if (rowSet.next()) {
            Film film = new Film();
            film.setId(rowSet.getLong("film_id"));
            film.setName(rowSet.getString("name"));
            film.setDescription(rowSet.getString("description"));
            film.setReleaseDate(rowSet.getDate("release_date").toLocalDate());
            film.setDuration(rowSet.getInt("duration"));
            film.setMpa(new Mpa(rowSet.getInt("mpa_id"), rowSet.getString("name")));
            List<Genre> genres = genre.getAllGenresForFilm(id);
            film.setGenre((genres.size() == 0) ? null:genres);
            log.info("Получен фильм с идентификатором {}.", id);
            return Optional.of(film);
        } else {
            throw new UserNotFoundException("В Filmorate отсутствует фильм с идентификатором № " + id);
        }
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        likes.create(userId, filmId);
        log.info("Добавлена отметка нравится фильму: {}", getById(filmId));
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        likes.delete(userId, filmId);
        log.info("Удалена отметка нравится фильму: {}", getById(filmId));
    }

    @Override
    public Collection<Film> getPopular(int count) {
        log.info("Составлен список популярных фильмов");
        return jdbcTemplate.query(SQL_GET_POPULAR_FILMS, this::mapRowToFilm, count == 0 ? 10 : count);
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