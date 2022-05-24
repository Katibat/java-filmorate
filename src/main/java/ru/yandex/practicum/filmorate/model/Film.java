package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private Long id; // целочисленный идентификатор
    @NotBlank
    private final String name; // название
    @Size(max = 200)
    @NotBlank
    private final String description; // описание
    @PastOrPresent
    private final LocalDate releaseDate; // дата релиза
    @Positive
    private final Integer duration; // продолжительность фильма
}
