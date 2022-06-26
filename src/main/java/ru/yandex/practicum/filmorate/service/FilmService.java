package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Dao.GenreDaoEntity;
import ru.yandex.practicum.filmorate.storage.Dao.MpaDaoEntity;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Service
public class FilmService { // отвечает за операции с фильмами: добавление/удаление лайков, получение списков фильмов
    private FilmStorage filmStorage;
    private final GenreDaoEntity genreStorage;
    private final MpaDaoEntity mpaStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, MpaDaoEntity mpaStorage, GenreDaoEntity genreStorage) {
        this.filmStorage = filmStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Film create(Film film) { // добавить фильм
        return filmStorage.create(film);
    }

    public Film put(Film film) { // обновить фильм
        return filmStorage.put(film);
    }

    public Collection<Film> findAllFilms() { // найти все фильмы
        return filmStorage.findAll();
    }

    public Optional<Film> getFilmById(Long id) { // найти фильм по идентификатору
        return filmStorage.getById(id);
    }

    public Collection<Film> findPopularFilms(int count) { // найти популярные фильмы
        return filmStorage.getPopular(count);
    }

    public void addLike(Long filmId, Long userId) { // добавить отметку нравится фильму
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) { // удалить отметку нравится фильму
        filmStorage.deleteLike(filmId, userId);
    }

    public Mpa getMpaById(int id) {
        return mpaStorage.getById(id);
    }

    public Collection<Mpa> getAllMpa() {
        return mpaStorage.getAll();
    }

    public Genre getGenreById(int id) {
        return genreStorage.getById(id);
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAll();
    }
}
