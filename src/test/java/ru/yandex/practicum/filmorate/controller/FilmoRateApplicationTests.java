package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Dao.LikesDaoLink;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final LikesDaoLink likesStorage;
    private static User user1 = User.builder()
            .id(15L)
            .email("email@ya.ru")
            .login("login")
            .name("user")
            .birthday(LocalDate.parse("2000-11-11"))
            .build();
    private static User friend = User.builder()
            .id(25L)
            .email("emailFriend@ya.ru")
            .login("loginFriend")
            .name("friend")
            .birthday(LocalDate.parse("2005-09-01"))
            .build();
    private static User commonFriend = User.builder()
            .id(35L)
            .email("emailCommonFriend@ya.ru")
            .login("loginCommonFriend")
            .name("commonFriend")
            .birthday(LocalDate.parse("2010-07-09"))
            .build();
    private static final Film film1 = Film.builder()
            .name("nameFilm1")
            .description("filmDescription1")
            .releaseDate(LocalDate.parse("1986-10-11"))
            .duration(100)
            .mpa(new Mpa(4, "R"))
            .build();
    private static final Film film2 = Film.builder()
            .name("nameFilm2")
            .description("filmDescription1")
            .releaseDate(LocalDate.parse("2004-12-11"))
            .duration(120)
            .mpa(new Mpa(1, "G"))
            .build();

    @BeforeEach
    public void beforeEach() {
        userDbStorage.findAll().clear();
    }

    @Test
    public void testFindUserById() {
        Optional<User> userOptional = userDbStorage.getById(2L);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2L)
                );
    }

    @Test
    public void testFindAllUsers() {
        Collection<User> result = userDbStorage.findAll();
        assertEquals(8, result.size());
    }

    @Test
    public void testCreateUser() {
        User user2 = User.builder()
                .id(45L)
                .email("emailNew@ya.ru")
                .login("loginNew")
                .name("userNew")
                .birthday(LocalDate.parse("2006-01-01"))
                .build();
        Optional<User> result = Optional.ofNullable(userDbStorage.create(user2));
        assertThat(result).isPresent();
        Optional<User> actual = userDbStorage.getById(result.get().getId());
        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", user.getEmail()))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", user.getLogin()));
    }

    @Test
    public void testUpdateUser() {
        user1.setEmail("emailUp@ya.ru");
        user1.setLogin("loginUp@ya.ru");
        userDbStorage.put(user1);
        assertEquals("emailUp@ya.ru", user1.getEmail());
        assertEquals("loginUp@ya.ru", user1.getLogin());
    }

    @Test
    public void testAddFriend() {
        userDbStorage.addFriend(user1.getId(), friend.getId());
        Collection<User> userList = userDbStorage.getUserFriends(user1.getId());
        assertEquals(1, userList.size());
    }

    @Test
    public void testGetUserFriends() {
        Collection<User> result = userDbStorage.getUserFriends(user1.getId());
        assertEquals(1, result.size());
    }

    @Test
    public void testDeleteFriend() {
        userDbStorage.deleteFriend(user1.getId(), friend.getId());
        Collection<User> userList = userDbStorage.getUserFriends(user1.getId());
        assertEquals(0, userList.size());
    }

    @Test
    public void testGetCommonFriends() {
        User userCommon = new User(85L, "emailCom@ya.ru", "loginCom",
                "userCom",LocalDate.parse("2009-01-01"));
        User userCommon2 = new User(95L,"emailCom2@ya.ru","loginCom2",
                "userCom2",LocalDate.parse("2005-01-01"));
        User userCommon3 = new User(105L,"emailCom3@ya.ru","loginCom3",
                "userCom3",LocalDate.parse("1999-08-01"));
        userDbStorage.create(userCommon);
        userDbStorage.create(userCommon2);
        userDbStorage.create(userCommon3);
        userDbStorage.addFriend(userCommon.getId(), userCommon3.getId());
        userDbStorage.addFriend(userCommon2.getId(), userCommon3.getId());
        assertEquals(1, userDbStorage.getCommonFriends(userCommon.getId(), userCommon2.getId()).size());
    }

    @Test
    public void testFindFilmById() {
        Optional<Film> filmOptional = filmDbStorage.getById(film1.getId());
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", film1.getId())
                );
    }

    @Test
    public void testFindAllFilms() {
        Collection<Film> result = filmDbStorage.findAll();
        assertEquals(4, result.size());
    }

    @Test
    public void testCreateFilm() {
        Film filmNew = Film.builder()
                .id(56L)
                .name("filmNew")
                .description("descriptionNew")
                .releaseDate(LocalDate.parse("1989-09-03"))
                .duration(100)
                .mpa(new Mpa(4, "G"))
                .build();
        Optional<Film> result = Optional.ofNullable(filmDbStorage.create(filmNew));
        assertThat(result).isPresent();
        Optional<Film> actual = filmDbStorage.getById(result.get().getId());
        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", film.getName()))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", film.getDescription()));
    }

    @Test
    public void testUpdateFilm() {
        Film filmUp = Film.builder()
                .id(1L)
                .name("film1Up")
                .description("film1DescriptionUp")
                .releaseDate(LocalDate.parse("1986-10-01"))
                .duration(130)
                .mpa(new Mpa(4, "R"))
                .build();
        filmDbStorage.put(filmUp);
        Optional<Film> filmOptional = filmDbStorage.getById(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "film1Up"))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", "film1DescriptionUp")
                );
    }

    @Test
    public void testAddLike() {
        filmDbStorage.addLike(film1.getId(), user1.getId());
        List<Long> likesList = likesStorage.getAllLikesForFilm(film1.getId());
        assertEquals(1, likesList.size());
    }

    @Test
    public void testDeleteLike() {
        filmDbStorage.deleteLike(film1.getId(), user1.getId());
        List<Long> likesList = likesStorage.getAllLikesForFilm(film1.getId());
        assertEquals(0, likesList.size());
    }

    @Test
    public void testGetPopularFilms() {
        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        userDbStorage.create(user1);
        userDbStorage.create(friend);
        filmDbStorage.addLike(film1.getId(), user1.getId());
        filmDbStorage.addLike(film2.getId(), user1.getId());
        filmDbStorage.addLike(film2.getId(), friend.getId());
        Collection<Film> films = filmDbStorage.getPopular(10);
        assertEquals(4, films.size());
    }
}
