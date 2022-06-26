package ru.yandex.practicum.filmorate.storage.Dao;

import java.util.List;

public interface LinkDaoStorage<T> {

    void create(Long id1, Long id2);

    void delete(Long id1, Long id2);

    void merge(Long id, List<T> object);
}
