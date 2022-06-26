package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService { // отвечает за добавление/удаление друзей, вывод списка общих друзей
    private final UserStorage userStorage;
    private final UserDbStorage userDbStorage;


    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage, UserDbStorage userDbStorage) {
        this.userStorage = userStorage;
        this.userDbStorage = userDbStorage;
    }

    public User create(User user) { // добавить пользователя
        return userStorage.create(user);
    }

    public User put(User user) { // обновить данные пользователя
        return userStorage.put(user);
    }

    public Collection<User> findAllUsers() { // найти всех пользователей
        return userStorage.findAll();
    }

    public Optional<User> getUserById(Long id) { // найти пользователя по идентификатору
        return userStorage.getById(id);
    }

    public void addFriend(Long userId, Long friendId) { // добавить друга
        userDbStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) { // удалить друга
        userDbStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> getFriendsForUser(Long userId) { // получить список друзей пользователя
        return userDbStorage.getUserFriends(userId);
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) { // получить список общих друзей
        return userDbStorage.getCommonFriends(userId, friendId);
    }
}

