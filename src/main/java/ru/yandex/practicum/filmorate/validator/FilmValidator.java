package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {
    private static final LocalDate BIRTHDAY_CINEMA = LocalDate.of(1895, 12, 28);

    public static boolean validate(Film film) {
        if (film.getName() == null) {
            log.warn("Передано пустое поле film.");
            return false;
        }
        if (film.getDescription() == null) {
            log.warn("Передано пустое поле description.");
            return false;
        }
        if (film.getReleaseDate().isBefore(BIRTHDAY_CINEMA)) {
            log.warn("В поле realeseDate указана дата ранее Дня Рождения кинематогрофа.");
            return false;
        }
        if (film.getMpa() == null) {
            log.warn("Передано пустое поле Mpa.");
            return false;
        }
        return true;
    }
}
