package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService { // отвечает за операции с фильмами: добавление/удаление лайков, 10 популярных фильмов
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> likesMap = new HashMap<>(); // id фильма и id пользователей, поставивших лайк

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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

    public Film getFilmById(Long id) { // найти фильм по идентификатору
        return filmStorage.getById(id);
    }

    public Collection<Film> findPopularFilms(int count) { // найти 10 самых популярных фильмов
        List<Film> all = new ArrayList<>(filmStorage.findAll());
        List<Film> liked = new ArrayList<>(sortedPopularFilms(count));
        List<Film> sorted = new ArrayList<>(liked);
        for (Film film : all) {
            if (!(liked.contains(film.getId()))) {
                sorted.add(film);
            }
        }
        return sorted.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    private Collection<Film> sortedPopularFilms(int count) { // сортировать фильмы по популярности
        return likesMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(x -> 1 - x.size())))
                .limit(count)
                .map(x -> filmStorage.getById(x.getKey()))
                .collect(Collectors.toList()
                );
    }

    public void addLike(Long filmId, Long userId) throws FilmNotFoundException, UserNotFoundException {
        if (!findAllFilms().contains(filmId)) {
            throw new FilmNotFoundException("В Filmorate отсутствует фильм с идентификатором № " + filmId);
        }
        if (!userStorage.findAll().contains(userId)) {
            throw new UserNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + userId);
        }
        Set<Long> filmLikes = likesMap.getOrDefault(filmId, new HashSet<>());
        filmLikes.add(userId);
        likesMap.put(filmId, filmLikes);
        System.out.format("Общая сумма отметок нравится для фильма %s составляет %s.",
                getFilmById(filmId), getRatingFilm(filmId));
    }

    public void deleteLike(Long filmId, Long userId) throws FilmNotFoundException, UserNotFoundException {
        if (!findAllFilms().contains(filmId)) {
            throw new FilmNotFoundException("В Filmorate отсутствует фильм с идентификатором № " + filmId);
        }
        if (!userStorage.findAll().contains(userId)) {
            throw new UserNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + userId);
        }
        Set<Long> filmLikes = likesMap.get(filmId);
        if (filmLikes.size() == 0) {
            throw new NullPointerException("Список отметок нравится фильма пуст.");
        } else if (filmLikes.size() == 1) {
            filmLikes.remove(userId);
            likesMap.remove(filmId);
        } else {
            filmLikes.remove(userId);
            likesMap.put(filmId, filmLikes);
        }
    }

    private Long getRatingFilm(Long filmId) {
        Set<Long> filmRating = likesMap.get(filmId);
        return (long) filmRating.size();
    }
}
