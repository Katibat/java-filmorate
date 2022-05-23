package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.resource.IdGeneratorUser;

import java.util.*;

import static ru.yandex.practicum.filmorate.Constants.TODAY_DATE;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage { // хранение, обновление и поиск пользователей

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) throws ValidationException, UserAlreadyExistException {
        if (validate(user)) {
            for (User u : users.values()) {
                if (u.getEmail().equals(user.getEmail()) || u.getLogin().equals(user.getLogin())) {
                    log.info("Попытка создания пользователя с уже используемым адресом электронной почты " +
                            "или логином: {}.", user);
                    throw new UserAlreadyExistException("Адрес электронной почты или логин уже используются.");
                }
            }
            if (user.getName().isEmpty()) {
                user.setName(user.getLogin());
                log.info("Имя пользователя не было заполнено, автоматически присвоено имя логина: {}.", user.getName());
            }
            user.setId(IdGeneratorUser.generateId());
            users.put(user.getId(), user);
            log.info("Добавлен пользователь: {}", user);
            return user;
        } else {
            throw new ValidationException("Введены некорректные данные, проверьте корректность заполнения полей.");
        }
    }

    @Override
    public User put(User user) throws ValidationException, UserNotFoundException {
        validate(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновлены данные пользователя: {}.", user);
        } else {
            log.debug("Попытка обновления данных пользователя с несуществующим идентификатором: {}", user.getId());
            throw new UserNotFoundException("Введен не корректный id пользователя. Ваш id № " + user.getId());
        }
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    private boolean validate(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Поле email заполнено некорректно: {}.", user.getEmail());
            return false;
        }
        if (user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            log.warn("Поле login заполнено некорректно: {}.", user.getLogin());
            return false;
        }
        if (user.getBirthday().isAfter(TODAY_DATE)) {
            log.warn("Поле birthday заполнено некорректно: {}. Указанная дата дня рождения позже {}.",
                    user.getBirthday(), TODAY_DATE);
            return false;
        }
        return true;
    }
}
