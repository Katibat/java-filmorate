package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Long id; // целочисленный идентификатор
    @NotBlank
    private String name; // название
    @Size(max = 200)
    @NotBlank
    private String description; // описание
    @PastOrPresent
    private LocalDate releaseDate; // дата релиза
    @Positive
    private Integer duration; // продолжительность фильма
    private Mpa mpa; // рейтинг фильма
    private Set<Genre> genre; // список жанров фильма

    public Film(String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }
}
