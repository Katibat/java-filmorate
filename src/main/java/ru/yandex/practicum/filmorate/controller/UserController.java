package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.resource.IdGeneratorUser;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping  // создание пользователя
    public User create(@Valid @RequestBody User user) {
        if (validateInput(user)) {
            for (User i : users.values()) {
                if (i.getEmail().equals(user.getEmail()) || i.getLogin().equals(user.getLogin())) {
                    log.info("Попытка создания {} с уже используемым адресом электронной почты или логином.", user);
                    throw new ValidationException("Адрес электронной почты или логин уже используются.");
                }
            }
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
                log.info("Имя пользователя было незаполнено, автоматически присвоено имя логина {}.", user);
            }
            user.setId(IdGeneratorUser.generateId());
            users.put(user.getId(), user);
            log.info("Добавлен пользователь {}", user);
            return user;
        } else {
            throw new ValidationException("Введены некорректные данные, проверьте корректность заполнения полей.");
        }
    }

    @PutMapping // обновление пользователя
    public User put(@RequestBody User user) {
        validateInput(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновлены данные пользователя {}", user);
        } else {
            log.debug("Попытка обновления данных пользователя с несуществующим идентификатором " + user.getId());
            throw new ValidationException("Введен не корректный id пользователя. Ваш id № " + user.getId());
        }
        return user;
    }

    @GetMapping  // получение списка всех пользователей
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    private boolean validateInput(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Поле email заполнено некорректно: пустое или не содержит символ @.");
            return false;
        }
        if (user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            log.warn("Поле login заполнено некорректно: пустое или содержит пробелы.");
            return false;
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Поле birthday заполнено некорректно: дата дня рождения еще не наступила.");
            return false;
        }
        return true;
    }
}

