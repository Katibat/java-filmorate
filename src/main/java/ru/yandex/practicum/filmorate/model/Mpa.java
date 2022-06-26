package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mpa {
    @NotNull
    private int id;
    private String name;

    public Mpa(int id) {
        this.id = id;
    }
}
