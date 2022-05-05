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

    @PostMapping  // добавление фильма
    public Film create(@RequestBody Film film) {
        if (validateInput(film)) {
            for (Film i : films.values()) {
                if (i.getName().equals(film.getName())) {
                    log.debug("Попытка создания {} фильма с таким же названием.", film);
                    throw new ValidationException("Фильм с названием " + film.getName() + " уже добавлен в Filmorate.");
                }
            }
            film.setId(IdGeneratorFilm.generateId());
            films.put(film.getId(), film);
            log.info("Добавлен фильм {}", film);
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
            log.info("Обновлены данные фильма {}", film);
        } else {
            log.debug("Попытка обновления фильма с несуществующим идентификатором " + film.getId());
            throw new ValidationException("Фильм с идентификатором " + film.getId() + " еще не добавлен в Filmorate.");
        }
        return film;
    }

    @GetMapping  // получение всех фильмов
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    private boolean validateInput(Film film) {
        if (film.getName().isBlank() || film.getName() == null) {
            log.warn("Поле film заполнено некорректно: название фильма пустое.");
            return false;
        }
        if (film.getDescription().length() > 200) {
            log.warn("Поле description заполнено некорректно: превышен лимит 200 символов.");
            return false;
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Поле realeseDate заполнено некорректно: дата релиза раньше 28 декабря 1895 года.");
            return false;
        }
        if (film.getDuration().isNegative() || film.getDuration().getSeconds() == 0) {
            log.warn("Поле duration заполнено некорректно: продолжительность фильма <= 0." + film);
            return false;
        }
        return true;
    }
}
