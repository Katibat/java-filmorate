package ru.yandex.practicum.filmorate.storage.Dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class MpaDaoEntity implements EntityDaoStorage<Mpa> {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_INSERT_MPA = "INSERT INTO mpa VALUES (?, ?)";
    private static final String SQL_UPDATE_MPA = "UPDATE mpa SET name = ? WHERE mpa_id = ?";
    private static final String SQL_DELETE_MPA = "DELETE FROM mpa WHERE mpa_id = ?";
    private static final String SQL_GET_MPA = "SELECT * FROM mpa WHERE mpa_id = ?";
    private static final String SQL_GET_ALL_MPA = "SELECT * FROM mpa";

    public MpaDaoEntity(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa create(Mpa mpa) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_MPA, mpa.getId());
        if (rowSet.next()) {
            jdbcTemplate.update(SQL_INSERT_MPA, mpa.getId(), mpa.getName());
            log.info("Добавлен рейтинг MPA фильма: {}.", mpa.getName());
            return mpa;
        } else {
            throw new DuplicateKeyException("Рейтинг MPA фильма уже существует.");
        }
    }

    @Override
    public void delete(int id) {
        Mpa mpa = getById(id);
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_MPA, mpa.getId());
        if (rowSet.next()) {
            jdbcTemplate.update(SQL_DELETE_MPA, id, mpa.getName());
            log.info("Удален рейтинг MPA фильма: {}.", mpa.getName());
        } else {
            throw new DuplicateKeyException("Рейтинг MPA фильма еще не создан.");
        }
    }

    @Override
    public Mpa update(Mpa mpa) {
        getById(mpa.getId());
        jdbcTemplate.update(SQL_UPDATE_MPA, mpa.getName(), mpa.getId());
        log.info("Обновлен рейтинг MPA фильма: {}.", mpa);
        return mpa;
    }

    @Override
    public Mpa getById(int id) {
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_MPA, id);
            if (rowSet.next()) {
                Mpa mpa = new Mpa(rowSet.getInt("mpa_id"),
                        rowSet.getString("name"));
                log.info("Получен рейтинг MPA фильма по идентификатору {}.", id);
                return mpa;
            } else {
                log.warn("Рейтинг MPA фильма с идентификатором {} не найден.", id);
                throw new MpaNotFoundException("Рейтинг MPA фильма по идентификатору не найден.");
            }
        } catch (EmptyResultDataAccessException e) {
            log.warn("Рейтинг MPA фильма с идентификатором {} не найден.", id);
            throw new MpaNotFoundException("Рейтинг MPA фильма по идентификатору не найден.");
        }
    }

    @Override
    public List<Mpa> getAll() {
        List<Mpa> mpaList = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_ALL_MPA);
        while (rowSet.next()) {
            mpaList.add(new Mpa(rowSet.getInt("mpa_id"), rowSet.getString("name")));
        }
        return mpaList;
    }
}
