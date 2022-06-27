package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
public class UserValidator {
    public static boolean validate(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Поле email заполнено некорректно: пустое или не содержит символ @.");
            return false;
        }
        if (user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
            log.warn("Передано пустое поле login.");
            return false;
        }
        return true;
    }
}
