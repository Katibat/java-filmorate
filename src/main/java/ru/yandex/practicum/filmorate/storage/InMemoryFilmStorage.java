package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.resource.IdGeneratorFilm;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage { // хранение, обновление и поиск фильмов
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate BIRTHDAY_CINEMA = LocalDate.of(1895, 12, 28);

    @Override
    public Film create(Film film) {
        if (validate(film)) {
            for (Film f : films.values()) {
                if (f.getName().equals(film.getName())) {
                    log.debug("Попытка создания фильма с уже используемым названием: {}.", film);
                    throw new FilmAlreadyExistException("В Filmorate уже добавлен фильм с названием: " + film.getName());
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

    @Override
    public Film put(Film film) {
        validate(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновлены данные фильма: {}.", film);
        } else {
            log.debug("Попытка обновления фильма с несуществующим идентификатором: {}.", film);
            throw new FilmNotFoundException("В Filmorate отсутствует фильм с идентификатором № " + film.getId());
        }
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getById(Long id) {
        if (films.containsKey(id)) {
            return Optional.ofNullable(films.get(id));
        } else {
            throw new FilmNotFoundException("В Filmorate отсутствует фильм с идентификатором № " + id);
        }
    }

    private boolean validate(Film film) {
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
        return true;
    }
}
