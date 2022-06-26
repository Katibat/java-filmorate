package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;
import java.util.List;

@Component
public class GenreDaoEntity implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_INSERT_GENRE = "INSERT INTO genre VALUES (?, ?)";
    private static final String SQL_UPDATE_GENRE = "UPDATE genre SET name = ? WHERE genre_id = ?";
    private static final String SQL_DELETE_GENRE = "DELETE FROM genre WHERE genre_id = ?";
    private static final String SQL_GET_GENRE = "SELECT * FROM genre WHERE genre_id=?";
    private static final String SQL_GET_ALL_GENRE = "SELECT * FROM genre";

    public GenreDaoEntity(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre create(Genre genre) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_GENRE, genre.getId());
        if (rowSet.next()) {
            jdbcTemplate.update(SQL_INSERT_GENRE, genre.getId(), genre.getName());
            log.info("Добавлен жанр фильма: {}.", genre.getName());
            return genre;
        } else {
            throw new DuplicateKeyException("Жанр фильма уже существует.");
        }
    }

    @Override
    public void delete(int id) {
        Genre genre = getById(id);
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_GENRE, genre.getId());
        if (rowSet.next()) {
            jdbcTemplate.update(SQL_DELETE_GENRE, id, genre.getName());
            log.info("Удален жанр фильма: {}.", genre.getName());
        } else {
            throw new DuplicateKeyException("Жанр фильма еще не создан.");
        }
    }

    @Override
    public Genre update(Genre genre) {
        getById(genre.getId());
        jdbcTemplate.update(SQL_UPDATE_GENRE, genre.getName(), genre.getId());
        log.info("Обновлен жанр фильма: {}.", genre);
        return genre;
    }

    @Override
    public Genre getById(int id) {
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_GENRE, id);
            if (rowSet.next()) {
                Genre genre = new Genre(rowSet.getInt("genre_id"),
                        rowSet.getString("name"));
                log.info("Получен жанр фильма по идентификатору {}.", id);
                return genre;
            } else {
                log.warn("Жанр фильма с идентификатором {} не найден.", id);
                throw new GenreNotFoundException("Жанр фильма по идентификатору не найден.");
            }
        } catch (EmptyResultDataAccessException e) {
            log.warn("Жанр фильма с идентификатором {} не найден.", id);
            throw new GenreNotFoundException("Жанр фильма по идентификатору не найден.");
        }
    }

    @Override
    public List<Genre> getAll() {
        List<Genre> genreList = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_ALL_GENRE);
        while (rowSet.next()) {
            genreList.add(new Genre(rowSet.getInt("genre_id"), rowSet.getString("name")));
        }
        return genreList;
    }
}
