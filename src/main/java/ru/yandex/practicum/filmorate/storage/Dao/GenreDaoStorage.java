package ru.yandex.practicum.filmorate.storage.Dao;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Component
public class GenreDaoStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_GENRE = "SELECT * FROM genre WHERE genre_id=?";
    private static final String SQL_GET_ALL_GENRE = "SELECT * FROM genre";

    public GenreDaoStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(int id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_GENRE, id);
        if (rowSet.next()) {
            return new Genre(
                    rowSet.getInt("genre_id"),
                    rowSet.getString("name")
            );
        } else {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("Жанр по идентификатору {} не найден.", id)
            );
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(SQL_GET_ALL_GENRE, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name"))
        );
    }
}
