package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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

    public Optional<Film> getFilmById(Long id) { // найти фильм по идентификатору
        return filmStorage.getById(id);
    }

    public Collection<Film> findPopularFilms(int count) { // найти 10 самых популярных фильмов
        List<Film> all = new ArrayList<>(filmStorage.findAll());
        List<Film> liked = new ArrayList<>(sortedFilmsByPopularity(count));
        List<Film> sorted = new ArrayList<>(liked);
        for (Film f : all) {
            if (!(liked.contains(f.getId()))) {
                sorted.add(f);
            }
        }
        return sorted.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    private Collection<Film> sortedFilmsByPopularity(int count) { // сортировать фильмы по популярности
        return likesMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(f -> 1 - f.size())))
                .limit(count)
                .map(f -> filmStorage.getById(f.getKey()).get())
                .collect(Collectors.toList()
                );
    }

    public void addLike(Long filmId, Long userId) throws FilmNotFoundException, UserNotFoundException {
        if (filmStorage.getById(filmId).isEmpty()) {
            throw new FilmNotFoundException("В Filmorate отсутствует фильм с идентификатором № " + filmId);
        }
        if (userStorage.getById(userId).isEmpty()) {
            throw new UserNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + userId);
        }
        Set<Long> filmLikes = likesMap.getOrDefault(filmId, new HashSet<>());
        filmLikes.add(userId);
        likesMap.put(filmId, filmLikes);
        log.info("Добавлена отметка нравится фильму: {}", getFilmById(filmId));
    }

    public void deleteLike(Long filmId, Long userId) throws FilmNotFoundException, UserNotFoundException {
        if (filmStorage.getById(filmId).isEmpty()) {
            throw new FilmNotFoundException("В Filmorate отсутствует фильм с идентификатором № " + filmId);
        }
        if (userStorage.getById(userId).isEmpty()) {
            throw new UserNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + userId);
        }
        Set<Long> filmLikes = likesMap.get(filmId);
        if (filmLikes == null) {
            throw new NullPointerException("Список отметок нравится фильма пуст.");
        } else {
            filmLikes.remove(userId);
            if (filmLikes.size() == 0) {
                likesMap.remove(filmId);
                log.info("Фильм {} удален из списка популярных.", getFilmById(filmId));
            } else {
                likesMap.put(filmId, filmLikes);
                log.info("Удалена отметка нравится фильму: {}", getFilmById(filmId));
            }
        }
    }
}
