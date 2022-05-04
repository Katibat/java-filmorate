package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.*;

import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id; // целочисленный идентификатор
    @NotNull
    @NotEmpty
    private final String name; // название
    @NotNull
    @Size(max = 200)
    private final String description; // описание
    @NotNull
    private final LocalDate releaseDate; // дата релиза
    @Positive
    @NotNull
    private final Duration duration; // продолжительность фильма
}
