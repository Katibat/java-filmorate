package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private final UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

@AfterEach
void afterEachTest() {
    userController.findAll().clear();
}

    @Test //    электронная почта не может быть пустой
    void shouldCreateUserWithoutEmail() {
        User user = User.builder()
                .id(1L)
                .email("")
                .login("loginTest")
                .name("nameTest")
                .birthday(LocalDate.of(2006, 7, 7))
                .build();
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test //    электронная почта должна содержать символ @
    void shouldCreateUserWithoutEmailAt() {
        User user = User.builder()
                .id(1L)
                .email("email.test")
                .login("loginTest")
                .name("nameTest")
                .birthday(LocalDate.of(2006, 7, 7))
                .build();
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test //    создание пользователя с используемыми данными (email & login)
    void shouldCreateUserWithTheSameData() {
        User user1 = User.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("loginTest")
                .name("nameTest")
                .birthday(LocalDate.of(2006, 7, 7))
                .build();
        userController.create(user1);
        // создание пользователя с уже используемой почтой
        User user2 = User.builder()
                .id(6L)
                .email("email@ya.ru")
                .login("loginTest2")
                .name("nameTest2")
                .birthday(LocalDate.of(2009, 1, 9))
                .build();
        assertThrows(UserAlreadyExistException.class, () -> userController.create(user2));
        // создание пользователя с уже используемым логином
        User user3 = User.builder()
                .id(7L)
                .email("email2@ya.ru")
                .login("loginTest")
                .name("nameTest2")
                .birthday(LocalDate.of(2009, 1, 9))
                .build();
        assertThrows(UserAlreadyExistException.class, () -> userController.create(user3));
    }

    @Test //    логин не может быть пустым
    void shouldCreateUserWithoutLogin() {
        User user = User.builder()
                .id(1L)
                .email("email3@ya.ru")
                .login("")
                .name("nameTest3")
                .birthday(LocalDate.of(1990, 2, 26))
                .build();
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test //    логин не может содержать пробелы
    void shouldCreateUserWithSpace() {
        User user = User.builder()
                .id(1L)
                .email("email3@ya.ru")
                .login("login test")
                .name("nameTest3")
                .birthday(LocalDate.of(1990, 2, 26))
                .build();
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test //    имя для отображения может быть пустым — в таком случае будет использован логин
    void shouldCreateUserWithoutName() {
        User user = User.builder()
                .id(1L)
                .email("email4@ya.ru")
                .login("login4")
                .name("")
                .birthday(LocalDate.of(1995, 10, 2))
                .build();
        userController.create(user);
        Assertions.assertEquals(user.getName(), user.getLogin());
    }

    @Test //   дата рождения не может быть в будущем
    void shouldCreateUserWithBirthdayInFuture() {
        User user = User.builder()
                .id(1L)
                .email("email5@ya.ru")
                .login("login5")
                .name("nameTest5")
                .birthday(LocalDate.of(2024, 1, 31))
                .build();
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test //   обновление данных пользователя
    void shouldUpdateUserDate() {
        User user = User.builder()
                .id(1L)
                .email("email@ya.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2006, 7, 7))
                .build();
        userController.create(user);
        User userUpdate = User.builder()
                .id(user.getId())
                .email("newEmail@ya.ru")
                .login("newLogin")
                .name("newName")
                .birthday(LocalDate.of(2006, 7, 7))
                .build();
        userController.put(userUpdate);
        User actual = userController.getUserById(Long.valueOf(1));
        Assertions.assertEquals(userUpdate, actual);
    }
}
