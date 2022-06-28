package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage { // хранение, обновление и поиск пользователей
    private final Map<Long, User> users; // таблица пользователей

    public InMemoryUserStorage() {
        users = new HashMap<>();
    }

    @Override
    public User create(User user) {
        if (UserValidator.validate(user)) {
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
            users.put(user.getId(), user);
            log.info("Добавлен пользователь: {}", user);
            return user;
        } else {
            throw new ValidationException("Введены некорректные данные, проверьте корректность заполнения полей.");
        }
    }

    @Override
    public User put(User user) {
        UserValidator.validate(user);
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
        if (users.containsKey(id)) {
            return Optional.ofNullable(users.get(id));
        } else {
            throw new UserNotFoundException("Введен не корректный id пользователя. Ваш id № " + id);
        }
    }
}
