package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id; // целочисленный идентификатор
    private final String name; // название
    private final String description; // описание
    private final LocalDate releaseDate; // дата релиза
    private final Duration duration; // продолжительность фильма
}
