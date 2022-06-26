package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class GenreDaoLink implements LinkDaoStorage<Genre> {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDaoEntity genreDaoEntity;
    private static final String SQL_CREATE = "MERGE INTO film_genre KEY (film_id, genre_id) VALUES (?, ?)";
    private static final String SQL_DELETE = "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?";
    private static final String SQL_GET_ALL_GENRE = "SELECT g.* FROM genre g JOIN film_genre fg " +
            "ON fg.genre_id = g.genre_id WHERE fg.film_id = ?";

    public GenreDaoLink(JdbcTemplate jdbcTemplate, GenreDaoEntity genreDaoEntity) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDaoEntity = genreDaoEntity;
    }

    @Override
    public void create(Long filmId, Long genreId) {
        Genre genre = genreDaoEntity.getById(Math.toIntExact(genreId));
        jdbcTemplate.update(SQL_CREATE, filmId, genreId);
        log.info("Фильму {} добавлен жанр {}.", filmId, genre.getName());
    }

    @Override
    public void delete(Long filmId, Long genreId) {
        Genre genre = genreDaoEntity.getById(Math.toIntExact(genreId));
        int count = jdbcTemplate.update(SQL_DELETE, filmId, genreId);
        if (count == 0) {
            log.warn("Попытка удалить несуществующий жанр фильма: {}.", genre.getName());
            throw new GenreNotFoundException("Жанр фильма не найден.");
        } else {
            log.info("Жанр {} для фильма {} удален.", genre.getName(), filmId);
        }
    }

    @Override
    public void merge(Long filmId, List<Genre> genres) {
        List<Integer> from = new ArrayList<>();
        List<Integer> toDel = new ArrayList<>();
        List<Integer> toIns = new ArrayList<>();
        for (Genre g : getAllGenresForFilm(filmId))
            from.add(g.getId());
        if (genres != null)
            for (Genre g : genres)
                toIns.add(g.getId());
        toDel.addAll(from);
        toDel.removeAll(toIns);
        toIns.removeAll(from);
        for (Integer i : toDel)
            delete(filmId, Long.valueOf(i));
        for (Integer i : toIns)
            create(filmId, Long.valueOf(i));
    }

    public List<Genre> getAllGenresForFilm(Long filmId) {
        List<Genre> genreList = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_ALL_GENRE, filmId);
        while (rowSet.next()) {
            genreList.add(new Genre(rowSet.getInt("genre_id"),
                    rowSet.getString("name")));
        }
        return genreList;
    }
}
