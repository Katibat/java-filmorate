package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class Genre {
    private int id;
    @NotNull
    private String name;

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
