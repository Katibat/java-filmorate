package ru.yandex.practicum.filmorate.resource;

public class IdGeneratorFilm {
    private static int id = 1;

    public static int generateId() {
        return id++;
    }
}
