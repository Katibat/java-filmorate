package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mpa {
    private int id;
    @NotNull
    private String name;

    public Mpa(int id) {
        this.id = id;
    }
}
