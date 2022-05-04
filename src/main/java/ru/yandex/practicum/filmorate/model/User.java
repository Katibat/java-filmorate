package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id; // целочисленный идентификатор
    @Email
    @NotNull()
    @NotBlank
    private String email; // электронная почта
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[A-Za-z]+$",
            message = "должно содержать только латинские буквы без пробелов")
    private String login; // логин пользователя
    @NotNull
    @NotBlank
    private String name; // имя для отображения
    @Past
    @NotNull
    private LocalDate birthday; // дата рождения
}
