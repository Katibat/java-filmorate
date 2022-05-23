package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class User {
    private Long id; // целочисленный идентификатор
    @Email
    private String email; // электронная почта
    @NotBlank
    private String login; // логин пользователя
    private String name; // имя для отображения
    @Past
    private LocalDate birthday; // дата рождения
}
