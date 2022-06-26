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
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_INSERT_USER = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String SQL_INSERT_FRIEND = "INSERT INTO friendship (user_id, friend_id) " +
            "VALUES (?, ?)";
    private static final String SQL_UPDATE = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String SQL_DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String SQL_DELETE_FRIEND = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
    private static final String SQL_GET_USER = "SELECT * FROM users WHERE user_id = ?";
    private static final String SQL_GET_ALL_USERS = "SELECT * FROM users";
    private static final String SQL_GET_FRIENDS = "SELECT * FROM users u JOIN friendship f " +
            "ON u.user_id = f.friend_id WHERE f.user_id = ?";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        UserValidator.validate(user);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement stmt = connection.prepareStatement(SQL_INSERT_USER, new String[]{"user_id"});
                        stmt.setString(1, user.getEmail());
                        stmt.setString(2, user.getLogin());
                        stmt.setString(3, user.getName());
                        stmt.setDate(4, Date.valueOf(user.getBirthday()));
                        return stmt;
                    }, keyHolder);
            user.setId(keyHolder.getKey().longValue());
            log.info("Добавлен пользователь с идентификатором: {}", user.getId());
            return user;
        } catch (DuplicateKeyException e) {
            if (e.toString().contains("users(email)")) {
                throw new UserAlreadyExistException("Пользователь с таким email уже существует.");
            } else if (e.toString().contains("users(login)")) {
                throw new UserAlreadyExistException("Пользователь с таким login уже существует.");
            } else {
                throw new UnsupportedOperationException(e.getMessage());
            }
        }
    }

    public void delete(User user) {
        jdbcTemplate.update(SQL_DELETE_USER, user.getId());
    }

    @Override
    public User put(User user) {
        UserValidator.validate(user);
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_USER, user.getId());
        if (rowSet.next()) {
            jdbcTemplate.update(SQL_UPDATE,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    Date.valueOf(user.getBirthday()),
                    user.getId()
            );
            log.info("Обновлены данные пользователя: {}.", user.getName());
            return user;
        } else {
            throw new UserNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + user.getId());
        }
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(SQL_GET_ALL_USERS, this::mapRowToUser);
    }

    @Override
    public Optional<User> getById(Long id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_USER, id);
        if (rowSet.next()) {
            User user = new User(rowSet.getLong("user_id"),
                    rowSet.getString("email"),
                    rowSet.getString("login"),
                    rowSet.getString("name"),
                    rowSet.getDate("birthday").toLocalDate());
            return Optional.of(user);
        } else {
            throw new UserNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + id);
        }
    }

    public Collection<User> getUserFriends(Long userId) {
        return jdbcTemplate.query(SQL_GET_FRIENDS, this::mapRowToUser, userId);
    }

    public void addFriend(Long userId, Long friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SQL_GET_USER, friendId);
        if (!userRows.next()) {
            throw new UserNotFoundException("В Filmorate отсутствует пользователь с идентификатором №: " + friendId);
        }
        jdbcTemplate.update(SQL_INSERT_FRIEND, userId, friendId);
        log.info("Пользователь {} добавлен в список друзей пользователя {}.", userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        jdbcTemplate.update(SQL_DELETE_FRIEND, userId, friendId);
        log.info("Пользователь {} исключен из списка друзей.", friendId);
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        return getUserFriends(userId).stream()
                .distinct()
                .filter(o -> getUserFriends(friendId).contains(o))
                .collect(Collectors.toSet());
    }

    private User mapRowToUser(ResultSet resultSet, int i) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
