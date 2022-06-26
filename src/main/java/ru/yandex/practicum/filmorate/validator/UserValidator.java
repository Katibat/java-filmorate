package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
public class UserValidator {
    public static boolean validate(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Поле email заполнено некорректно: {}.", user.getEmail());
            return false;
        }
        if (user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            log.warn("Поле login заполнено некорректно: {}.", user.getLogin());
            return false;
        }
        return true;
    }
}
