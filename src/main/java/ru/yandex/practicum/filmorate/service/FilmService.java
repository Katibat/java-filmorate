package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;

@Service
public interface FilmService { // отвечает за операции с фильмами: добавление/удаление лайков, получение списков фильмов

    Film create(Film film); // добавить фильм

    Film put(Film film); // обновить фильм

    Collection<Film> findAllFilms(); // найти все фильмы

    Film getFilmById(Long id); // найти фильм по идентификатору

    Collection<Film> findPopularFilms(int count); // найти популярные фильмы

    void addLike(Long filmId, Long userId); // добавить отметку нравится фильму

    void deleteLike(Long filmId, Long userId); // удалить отметку нравится фильму

    Mpa getMpaById(int id); // найти рейтинг MPA по идентификатору

    Collection<Mpa> getAllMpa(); // найти все рейтинги MPA

    Genre getGenreById(int id); // найти жанр фильма по идентификатору

    List<Genre> getAllGenres(); // найти все жанры фильма
}
