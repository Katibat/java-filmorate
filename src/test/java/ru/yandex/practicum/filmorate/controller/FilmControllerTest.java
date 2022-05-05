package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private final FilmController filmController = new FilmController();

    @AfterEach
    void afterEachTest() {
        filmController.findAll().clear();
    }

    @Test //    название не может быть пустым
    void shouldCreateFilmWithoutName() {
        Film film = Film.builder()
                .id(1)
                .name("")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(120))
                .build();
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test //    максимальная длина описания — 200 символов
    void shouldCreateFilmWithDescriptionMore200() {
        Film film = Film.builder()
                .id(1)
                .name("Matrix")
                .description("description111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111" +
                        "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111")
                .releaseDate(LocalDate.of(1999, 3, 31))
                .duration(Duration.ofMinutes(120))
                .build();
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test //    дата релиза — не раньше 28 декабря 1895 года
    void shouldCreateFilmBeforeCinemaBirthday() {
        Film film = Film.builder()
                .id(1)
                .name("Before the cinema time")
                .description("description Before the cinema time")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(Duration.ofMinutes(120))
                .build();
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test //    продолжительность фильма равна 0
    void shouldCreateFilmNullDuration() {
        Film film = Film.builder()
                .id(1)
                .name("Avatar")
                .description("description Avatar")
                .releaseDate(LocalDate.of(2009, 12, 17))
                .duration(Duration.ofMinutes(0))
                .build();
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test //    продолжительность фильма отрицательная
    void shouldCreateFilmNegativeDuration() {
        Film film = Film.builder()
                .id(1)
                .name("Avatar")
                .description("description Avatar")
                .releaseDate(LocalDate.of(2009, 12, 17))
                .duration(Duration.ofMinutes(-120))
                .build();
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test //    название фильма уже используется
    void shouldCreateFilmWithTheSameName() {
        Film film1 = Film.builder()
                .id(1)
                .name("Avatar")
                .description("description Avatar")
                .releaseDate(LocalDate.of(2009, 12, 17))
                .duration(Duration.ofMinutes(120))
                .build();
        filmController.create(film1);
        Film film2 = Film.builder()
                .id(2)
                .name("Avatar")
                .description("other description for Avatar")
                .releaseDate(LocalDate.of(2009, 12, 17))
                .duration(Duration.ofMinutes(120))
                .build();
        assertThrows(ValidationException.class, () -> filmController.create(film2));
    }

    @Test //   обновление данных фильма
    void shouldUpdateFilmDate() {
        Film film = Film.builder()
                .id(1)
                .name("A Dog’s Purpose")
                .description("description A Dog’s Purpose")
                .releaseDate(LocalDate.of(2016, 8, 26))
                .duration(Duration.ofMinutes(180))
                .build();
        filmController.create(film);
        Film filmUpdate = Film.builder()
                .id(1)
                .name("A Dog’s Purpose")
                .description("new description A Dog’s Purpose")
                .releaseDate(LocalDate.of(2017, 1, 27))
                .duration(Duration.ofMinutes(100))
                .build();
        filmController.put(filmUpdate);
        Film actual = filmController.findAll().get(0);
        Assertions.assertEquals(filmUpdate, actual);
    }

    @Test //   обновление данных фильма
    void shouldUpdateFilmWithIncorrectId() {
        Film film = Film.builder()
                .id(1)
                .name("A Dog’s Purpose")
                .description("description A Dog’s Purpose")
                .releaseDate(LocalDate.of(2016, 8, 26))
                .duration(Duration.ofMinutes(180))
                .build();
        filmController.create(film);
        Film filmUpdate = Film.builder()
                .id(7)
                .name("A Dog’s Purpose")
                .description("new description A Dog’s Purpose")
                .releaseDate(LocalDate.of(2017, 1, 27))
                .duration(Duration.ofMinutes(100))
                .build();
        assertThrows(ValidationException.class, () -> filmController.put(filmUpdate));
    }
}