package ru.yandex.practicum.filmorate.storage.Dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Component
public class MpaDaoStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_MPA = "SELECT mpa_id, name FROM mpa WHERE mpa_id = ?";
    private static final String SQL_GET_ALL_MPA = "SELECT mpa_id, name FROM mpa";

    public MpaDaoStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaById(int id) {
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_MPA, id);
            if (rowSet.next()) {
                return new Mpa(
                        rowSet.getInt("mpa_id"),
                        rowSet.getString("name")
                );
            } else {
                throw new MpaNotFoundException(String.format("Mpa-рейтинг фильма по идентификатору {} не найден.", id)
                );
            }
        } catch (DataAccessException e) {
            throw new MpaNotFoundException(String.format("Mpa-рейтинг фильма по идентификатору {} не найден.", id));
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query(SQL_GET_ALL_MPA, (rs, rowNum) -> new Mpa(
                rs.getInt("mpa_id"),
                rs.getString("name"))
        );
    }
}
