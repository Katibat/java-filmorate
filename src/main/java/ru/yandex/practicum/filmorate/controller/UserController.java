package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@Validated
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping  // создание пользователя
    public User create(@Valid @RequestBody User user) throws ValidationException, UserAlreadyExistException {
        log.info("Добавлен пользователь: {}", user);
        return userService.create(user);
    }

    @PutMapping // обновление пользователя
    public User put(@Valid @RequestBody User user) throws ValidationException, UserNotFoundException {
        log.info("Обновлены данные пользователя: {}.", user);
        return userService.put(user);
    }

    @PutMapping("/{id}/friends/{friendId}") // добавление друга
    public void addFriend(
            @PathVariable Long id,
            @PathVariable Long friendId) throws UserNotFoundException {
        userService.addFriend(id, friendId);
        log.info("Пользователь {} добавил друга {}.", userService.getUserById(id), userService.getUserById(friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId}") // удаление друга
    public void deleteFriend(
            @PathVariable Long id,
            @PathVariable Long friendId) throws UserNotFoundException {
        userService.deleteFriend(id, friendId);
        log.info("Пользователь {} удалил друга {}.", userService.getUserById(id), userService.getUserById(friendId));
    }

    @GetMapping("/{id}/friends") // получение списка друзей пользователя
    public Collection<User> getUserFriends(@PathVariable Long id) throws UserNotFoundException {
        return userService.getFriendsForUser(id);
    }

    @GetMapping("/{id}/friends/common/{friendId}") // получение списка общих друзей 2х пользователей
    public Collection<User> getCommonFriends(
            @PathVariable Long id,
            @PathVariable Long friendId) throws UserNotFoundException {
        return userService.getCommonFriends(id, friendId);
    }

    @GetMapping  // получение списка всех пользователей
    public Collection<User> findAll() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}") // получение пользователя по id
    public User getUserById(@PathVariable Long id) throws UserNotFoundException {
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            log.debug("Попытка получить пользователя с несуществующим идентификатором: {}.", id);
            throw new FilmNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + id);
        }
        return user.get();
    }
}

