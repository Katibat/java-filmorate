package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service("FilmDbService")
public class FilmDbService implements FilmService {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private static final String SQL_CREATE_LIKE_FOR_FILM = "MERGE INTO likes (user_id, film_id) VALUES (?, ?)";
    private static final String SQL_DELETE_LIKE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String SQL_GET_LIKES = "SELECT user_id FROM likes WHERE film_id = ?";

    @Autowired
    public FilmDbService(JdbcTemplate jdbcTemplate,
                         @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                         @Qualifier("UserDbStorage") UserStorage userStorage,
                         GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public Film create(Film film) { // добавить фильм
        return filmStorage.create(film);
    }

    public Film put(Film film) { // обновить фильм
        return filmStorage.put(film);
    }

    public Collection<Film> findAllFilms() { // найти все фильмы
        return filmStorage.findAll();
    }

    public Film getFilmById(Long id) { // найти фильм по идентификатору
        return filmStorage.getById(id);
    }

    public Collection<Film> findPopularFilms(int count) { // найти популярные фильмы
        return filmStorage.getPopular(count);
    }

    public void addLike(Long filmId, Long userId) { // добавить отметку нравится фильму
        if (filmStorage.getById(filmId) != null && userStorage.getById(userId).isPresent()) {
            jdbcTemplate.update(SQL_CREATE_LIKE_FOR_FILM, userId, filmId);
            log.info("Пользователь {} поставил отметку нравится фильму {}.", userId, Long.valueOf(filmId));
        }
    }

    public void deleteLike(Long filmId, Long userId) { // удалить отметку нравится фильму
        if (filmStorage.getById(filmId) != null && userStorage.getById(userId).isPresent()) {
            jdbcTemplate.update(SQL_DELETE_LIKE, filmId, userId);
            log.info("Отметка нравится от пользователя {} для фильма {} удалена.", userId, filmId);
        }
    }

    public List<Long> getAllLikesForFilm(Long filmId) {
        List<Long> likesList = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_LIKES, filmId);
        while (rowSet.next()) {
            likesList.add(rowSet.getLong("user_id"));
        }
        return likesList;
    }

    public Mpa getMpaById(int id) {
        return mpaStorage.getMpaById(id);
    }

    public Collection<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id);
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }
}
