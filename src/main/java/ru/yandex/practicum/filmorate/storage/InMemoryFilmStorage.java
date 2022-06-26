package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage { // хранение, обновление и поиск фильмов
    private final Map<Long, Film> films; // таблица фильмов
    private final Map<Long, Set<Long>> likes; // таблица отметок нравится фильма

    public InMemoryFilmStorage() {
        films = new HashMap<>();
        likes = new HashMap<>();
    }

    @Override
    public Film create(Film film) {
        if (FilmValidator.validate(film)) {
            for (Film f : films.values()) {
                if (f.getName().equals(film.getName())) {
                    log.debug("Попытка создания фильма с уже используемым названием: {}.", film);
                    throw new FilmAlreadyExistException("В Filmorate уже добавлен фильм с названием: " + film.getName());
                }
            }
            films.put(film.getId(), film);
            likes.put(film.getId(), new HashSet<>());
            log.info("Добавлен фильм: {}", film);
            return film;
        } else {
            throw new ValidationException("Введены некорректные данные, проверьте корректность заполнения полей.");
        }
    }

    @Override
    public Film put(Film film) {
        FilmValidator.validate(film);
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

    @Override
    public void addLike(Long filmId, Long userId) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException("В Filmorate отсутствует фильм с идентификатором № " + filmId);
        }
        likes.get(filmId).add(userId);
        log.info("Добавлена отметка нравится фильму: {}", getById(filmId));
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        if (!likes.containsKey(filmId)) {
            throw new FilmNotFoundException("Список отметок нравится у фильма пуст");
        }
        if (!likes.containsKey(filmId)) {
            throw new FilmNotFoundException("В списке отметок нравится отсутствует пользователь с идентификатором № "
                    + userId);
        }
        likes.get(filmId).remove(userId);
        log.info("Удалена отметка нравится фильму: {}", getById(filmId));
    }

    @Override
    public Collection<Film> getPopular(int count) {
        return likes.entrySet().stream()
                .sorted((o1, o2) -> Long.compare(o2.getValue().size(), o1.getValue().size()))
                .map(t -> films.get(t.getKey())).limit(count).collect(Collectors.toList());
    }
}