package ru.yandex.practicum.filmorate.storage.Dao;

import java.util.List;

public interface EntityDaoStorage<T> {

    T create(T object);

    void delete(int id);

    T update(T object);

    T getById(int id);

    List<T> getAll();
}
