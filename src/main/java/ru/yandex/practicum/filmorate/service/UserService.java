package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService { // отвечает за добавление / удаление друзей, вывод списка общих друзей
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> friendsMap = new HashMap<>(); // id пользователя и id друзей

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
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

    public void addFriend(Long userId, Long friendId) throws UserNotFoundException { // добавить друга
        if (userStorage.getById(userId).isEmpty()) {
            throw new FilmNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + userId);
        }
        if (userStorage.getById(friendId).isEmpty()) {
            throw new UserNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + friendId);
        }

        Set<Long> user = friendsMap.getOrDefault(userId, new HashSet<>());
        user.add(friendId);
        friendsMap.put(userId, user);
        log.info("В список друзей пользователя {} добавлен пользователь {}.",
                getUserById(userId), getUserById(friendId));

        Set<Long> friend = friendsMap.getOrDefault(friendId, new HashSet<>());
        friend.add(userId);
        friendsMap.put(friendId, friend);
        log.info("В список друзей пользователя {} добавлен пользователь {}.",
                getUserById(friendId), getUserById(userId));
    }

    public void deleteFriend(Long userId, Long friendId) throws UserNotFoundException { // удалить друга
        if (userStorage.getById(userId).isEmpty()) {
            throw new FilmNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + userId);
        }
        if (userStorage.getById(friendId).isEmpty()) {
            throw new UserNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + friendId);
        }

        Set<Long> user = friendsMap.get(userId);
        if (user == null) {
            throw new NullPointerException("Список друзей пользователя пуст.");
        } else {
            user.remove(friendId);
            friendsMap.put(userId, user);
            log.info("Из списка друзей пользователя {} удален пользователь {}.",
                    getUserById(userId), getUserById(friendId));
        }

        Set<Long> friend = friendsMap.get(friendId);
        if (friend == null) {
            throw new NullPointerException("Список друзей пользователя пуст.");
        } else {
            friend.remove(userId);
            friendsMap.put(friendId, friend);
            log.info("Из списка друзей пользователя {} удален пользователь {}.",
                    getUserById(friendId), getUserById(userId));
        }
    }

    public Collection<User> getFriendsForUser(Long userId) { // получить список друзей пользователя
        if (userStorage.getById(userId).isEmpty()) {
            throw new FilmNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + userId);
        }
        return friendsMap.getOrDefault(userId, new HashSet<>()).stream()
                .map(u->userStorage.getById(u).get())
                .collect(Collectors.toList()
                );
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) throws UserNotFoundException {
        if (userStorage.getById(userId).isEmpty()) {
            throw new FilmNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + userId);
        }
        if (userStorage.getById(friendId).isEmpty()) {
            throw new UserNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + friendId);
        }
        Collection<User> user = getFriendsForUser(userId);
        Collection<User> friend = getFriendsForUser(friendId);
        HashSet<User> commonFriends = new HashSet<>();
        for (User u : user) {
            for (User f : friend) {
                if (u.equals(f)) {
                    commonFriends.add(u);
                }
            }
        }
        return commonFriends;
    }
}

