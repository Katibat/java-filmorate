package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidator {
    private static final LocalDate BIRTHDAY_CINEMA = LocalDate.of(1895, 12, 28);

    public static boolean validate(Film film) {
        if (film.getName() == null) {
            log.warn("Поле film заполнено некорректно: {}.", film.getName());
            return false;
        }
        if (film.getDescription() == null) {
            log.warn("Поле description заполнено некорректно: {}. ", film.getDescription());
            return false;
        }
        if (film.getReleaseDate().isBefore(BIRTHDAY_CINEMA)) {
            log.warn("Поле realeseDate заполнено некорректно: {}. Указанная дата релиза раньше {}.",
                    film.getReleaseDate(), BIRTHDAY_CINEMA);
            return false;
        }
        if (film.getMpa() == null) {
            log.warn("Поле Mpa заполнено некорректно: {}.", film.getMpa());
            return false;
        }
        return true;
    }
}
