package ru.yandex.practicum.filmorate.resource;

public class IdGeneratorFilm {
    private static Long id = 1L;

    public static Long generateId() {
        return id++;
    }
}
