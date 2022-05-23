package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

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

    public User getUserById(Long id) { // найти пользователя по идентификатору
        return userStorage.getById(id);
    }

    public void addFriend(Long userId, Long friendId) throws UserNotFoundException { // добавить друга
        if (!findAllUsers().contains(userId)) {
            throw new FilmNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + userId);
        }
        if (!findAllUsers().contains(friendId)) {
            throw new UserNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + friendId);
        }
        Set<Long> userFriends = friendsMap.getOrDefault(userId, new HashSet<>());
        userFriends.add(friendId);
        friendsMap.put(userId, userFriends);
        System.out.format("В список друзей пользователя %s добавлено %s пользователей.",
                getUserById(userId), getSumFriendsForUser(userId));
    }

    public void deleteFriend(Long userId, Long friendId) throws UserNotFoundException { // удалить друга
        if (!findAllUsers().contains(userId)) {
            throw new FilmNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + userId);
        }
        if (!findAllUsers().contains(friendId)) {
            throw new UserNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + friendId);
        }
        Set<Long> userFriends = friendsMap.get(userId);
        if (userFriends.size() == 0) {
            throw new NullPointerException("Список друзей пользователя пуст.");
        } else if (userFriends.size() == 1) {
            userFriends.remove(friendId);
            friendsMap.remove(userId);
        } else {
            userFriends.remove(friendId);
            friendsMap.put(userId, userFriends);
        }
    }

    public Collection<User> getFriendsForUser(Long userId) { // получить список друзей пользователя
        if (!findAllUsers().contains(userId)) {
            throw new FilmNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + userId);
        }
        return friendsMap.getOrDefault(userId, new HashSet<>()).stream()
                .map(userStorage::getById)
                .collect(Collectors.toList()
                );
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) throws UserNotFoundException {
        if (!findAllUsers().contains(userId)) {
            throw new FilmNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + userId);
        }
        if (!findAllUsers().contains(friendId)) {
            throw new UserNotFoundException("В Filmorate отсутствует пользователь с идентификатором № " + friendId);
        }
        Set<Long> commonFriends = new TreeSet<>();
        commonFriends.addAll(friendsMap.get(userId));
        commonFriends.addAll(friendsMap.get(friendId));
        return commonFriends.stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    private Long getSumFriendsForUser(Long userId) {
        Set<Long> sumFriends = friendsMap.get(userId);
        return (long) sumFriends.size();
    }
}

