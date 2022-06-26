package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film create(Film film);

    Film put(Film film);

    Collection<Film> findAll();

    Film getById(Long id);

    Collection<Film> getPopular(int count);
}
