package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.LikesNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class LikesDaoLink implements LinkDaoStorage<Long> {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_CREATE = "MERGE INTO likes (user_id, film_id) VALUES (?, ?)";
    private static final String SQL_DELETE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String SQL_GET_LIKES = "SELECT user_id FROM likes WHERE film_id = ?";

    public LikesDaoLink(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Long userId, Long filmId) {
        jdbcTemplate.update(SQL_CREATE, userId, filmId);
        log.info("Пользователь {} поставил отметку нравится фильму {}.", userId, Long.valueOf(filmId));
    }

    @Override
    public void delete(Long userId, Long filmId) {
        int count = jdbcTemplate.update(SQL_DELETE, userId, Long.valueOf(filmId));
        if (count == 0) {
            log.warn("Попытка удалить несуществующую отметку нравится фильму: {}.", filmId);
            throw new LikesNotFoundException("Отметка нравится фильма не найдена.");
        } else {
            log.info("Отметка нравится от пользователя {} для фильма {} удалена.", userId, filmId);
        }
    }

    @Override
    public void merge(Long filmId, List<Long> likes) {
        List<Long> from = getAllLikesForFilm(filmId);
        List<Long> toDel = new ArrayList<>();
        List<Long> toIns = new ArrayList<>();
        if (likes != null) {
            toIns.addAll(likes);
        }
        toDel.removeAll(toIns);
        toIns.removeAll(from);
        for (Long i : toDel)
            delete(i, filmId);
        for (Long i : toIns)
            create(i, filmId);
    }

    public List<Long> getAllLikesForFilm(Long filmId) {
        List<Long> likesList = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_LIKES, filmId);
        while (rowSet.next()) {
            likesList.add(rowSet.getLong("user_id"));
        }
        return likesList;
    }
}
