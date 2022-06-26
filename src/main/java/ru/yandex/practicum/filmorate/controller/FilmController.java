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
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping  // добавление фильма
    public Film create(@Valid @RequestBody Film film) throws ValidationException, FilmAlreadyExistException {
        log.info("Добавлен фильм: {}", film);
        return filmService.create(film);
    }

    @PutMapping // обновление фильма
    public Film put(@Valid @RequestBody Film film) throws ValidationException, FilmNotFoundException {
        log.info("Обновлены данные фильма: {}.", film);
        return filmService.put(film);
    }

    @PutMapping("/{id}/like/{userId}") // добавление лайка
    public void createLike(
            @PathVariable Long id,
            @PathVariable Long userId) throws FilmNotFoundException, UserNotFoundException {
        filmService.addLike(id, userId);
        log.info("Фильму {} добавлена отметка нравится.", filmService.getFilmById(id));
    }

    @DeleteMapping("/{id}/like/{userId}") // удаление лайка
    public void deleteLike(
            @PathVariable Long id,
            @PathVariable Long userId) throws FilmNotFoundException, UserNotFoundException {
        filmService.deleteLike(id, userId);
        log.info("Фильму {} удалена отметка нравится.", filmService.getFilmById(id));
    }

    @GetMapping  // получение всех фильмов
    public Collection<Film> getAllFilms() {
        return filmService.findAllFilms();
    }

    @GetMapping("/popular")  // получение 10 популярных фильмов
    public Collection<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) int count) {
        return filmService.findPopularFilms(count);
    }

    @GetMapping("/{id}") // получение фильма по id
    public Film getFilmById(@PathVariable Long id) throws FilmNotFoundException {
        Optional<Film> film = filmService.getFilmById(id);
        if (film.isEmpty()) {
            log.debug("Попытка получить фильм с несуществующим идентификатором: {}.", id);
            throw new FilmNotFoundException("В Filmorate отсутствует фильм с идентификатором № " + id);
        }
        return film.get();
    }

    @GetMapping("/mpa")  // получение списка mpa-рейтинга фильмов
    public Collection<Mpa> getMpa() {
        return filmService.getAllMpa();
    }

    @GetMapping("/mpa/{id}")  // получение mpa-рейтинга фильмов
    public Mpa getMpaById(@PathVariable int id) {
        return filmService.getMpaById(id);
    }

    @GetMapping("/genre")  // получение списка жанров фильмов
    public Collection<Genre> getGenre() {
        return filmService.getAllGenres();
    }

    @GetMapping("/genre/{id}")  // получение жанра фильмов
    public Genre getGenreById(@PathVariable int id) {
        return filmService.getGenreById(id);
    }
}
