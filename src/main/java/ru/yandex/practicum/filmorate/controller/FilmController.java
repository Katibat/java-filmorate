package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.resource.IdGeneratorFilm;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private final static LocalDate BIRTHDAY_CINEMA = LocalDate.of(1895, 12, 28);

    @PostMapping  // добавление фильма
    public Film create(@RequestBody Film film) {
        if (validateInput(film)) {
            for (Film f : films.values()) {
                if (f.getName().equals(film.getName())) {
                    log.debug("Попытка создания фильма с уже используемым названием: {}.", film);
                    throw new ValidationException("В Filmorate уже добавлен фильм с названием: " + film.getName());
                }
            }
            film.setId(IdGeneratorFilm.generateId());
            films.put(film.getId(), film);
            log.info("Добавлен фильм: {}", film);
            return film;
        } else {
            throw new ValidationException("Введены некорректные данные, проверьте корректность заполнения полей.");
        }
    }

    @PutMapping // обновление фильма
    public Film put(@RequestBody Film film) {
        validateInput(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновлены данные фильма: {}.", film);
        } else {
            log.debug("Попытка обновления фильма с несуществующим идентификатором: {}.", film);
            throw new ValidationException("В Filmorate отсутствует фильм с идентификатором № " + film.getId());
        }
        return film;
    }

    @GetMapping  // получение всех фильмов
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    private boolean validateInput(Film film) {
        if (film.getName().isBlank() || film.getName() == null) {
            log.warn("Поле film заполнено некорректно: {}.", film.getName());
            return false;
        }
        if (film.getDescription().length() > 200 || film.getDescription().isEmpty() || film.getDescription() == null) {
            log.warn("Поле description заполнено некорректно: {}. " +
                    "Отсутствует описание или превышен лимит 200 символов.", film.getDescription());
            return false;
        }
        if (film.getReleaseDate().isBefore(BIRTHDAY_CINEMA)) {
            log.warn("Поле realeseDate заполнено некорректно: {}. Указанная дата релиза раньше {}.",
                    film.getReleaseDate(), BIRTHDAY_CINEMA);
            return false;
        }
        if (film.getDuration().isNegative() || film.getDuration().getSeconds() == 0) {
            log.warn("Поле duration заполнено некорректно: {}. Продолжительность фильма <= 0.", film.getDuration());
            return false;
        }
        return true;
    }
}
