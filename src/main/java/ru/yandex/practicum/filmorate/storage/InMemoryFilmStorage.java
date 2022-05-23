package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.resource.IdGeneratorFilm;

import java.util.*;

import static ru.yandex.practicum.filmorate.Constants.BIRTHDAY_CINEMA;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage { // хранение, обновление и поиск фильмов
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) throws ValidationException, FilmAlreadyExistException {
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
    public Film put(Film film) throws ValidationException, FilmNotFoundException {
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
        return Optional.ofNullable(films.get(id));
    }

    private boolean validate(Film film) {
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
