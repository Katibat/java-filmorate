package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@Validated
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")  // добавление фильма
    public Film create(@Valid @RequestBody Film film) throws ValidationException, FilmAlreadyExistException {
        log.info("Добавлен фильм: {}", film);
        return filmService.create(film);
    }

    @PutMapping("/films") // обновление фильма
    public Film put(@Valid @RequestBody Film film) throws ValidationException, FilmNotFoundException {
        log.info("Обновлены данные фильма: {}.", film);
        return filmService.put(film);
    }

    @PutMapping("/films/{id}/like/{userId}") // добавление лайка
    public void createLike(
            @PathVariable Long id,
            @PathVariable Long userId) throws FilmNotFoundException, UserNotFoundException {
        filmService.addLike(id, userId);
        log.info("Фильму {} добавлена отметка нравится.", filmService.getFilmById(id));
    }

    @DeleteMapping("/films/{id}/like/{userId}") // удаление лайка
    public void deleteLike(
            @PathVariable Long id,
            @PathVariable Long userId) throws FilmNotFoundException, UserNotFoundException {
        filmService.deleteLike(id, userId);
        log.info("Фильму {} удалена отметка нравится.", filmService.getFilmById(id));
    }

    @GetMapping ("/films") // получение всех фильмов
    public Collection<Film> getAllFilms() {
        return filmService.findAllFilms();
    }

    @GetMapping("/films/popular")  // получение 10 популярных фильмов
    public Collection<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) int count) {
        return filmService.findPopularFilms(count);
    }

    @GetMapping("/films/{id}") // получение фильма по id
    public Film getFilmById(@PathVariable Long id) throws FilmNotFoundException {
        Film film = filmService.getFilmById(id);
        if (film == null) {
            log.debug("Попытка получить фильм с несуществующим идентификатором: {}.", id);
            throw new FilmNotFoundException("В Filmorate отсутствует фильм с идентификатором № " + id);
        }
        return film;
    }

    @GetMapping("/mpa")  // получение списка mpa-рейтинга фильмов
    public Collection<Mpa> getMpa() {
        return filmService.getAllMpa();
    }

    @GetMapping("/mpa/{id}")  // получение mpa-рейтинга фильмов
    public Mpa getMpaById(@PathVariable int id) {
        return filmService.getMpaById(id);
    }

    @GetMapping("/genres")  // получение списка жанров фильмов
    public Collection<Genre> getGenre() {
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")  // получение жанра фильмов
    public Genre getGenreById(@PathVariable int id) {
        return filmService.getGenreById(id);
    }
}
