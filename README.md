# java-filmorate
Template repository for Filmorate project.

# Database structure:
[db ER-diagram](https://github.com/Katibat/java-filmorate/blob/add-database/ER-diagram.png)

Примеры SQL запросов:

Получение всех фильмов:
SELECT * 
FROM films;

Получение фильма по идентификтору:
SELECT * 
FROM films 
WHERE id=?;

Получение списка популярных фильмов:
SELECT f.film_id,
f.name,
f.description,
f.release_date,
f.duration,
f.mpa_id,
COUNT (l.user_id)
FROM films f
LEFT JOIN likes AS l ON f.film_id = l.film_id
GROUP BY f.film_id
ORDER BY COUNT (l.user_id) DESC
LIMIT ?;

Получение всех пользователей:
SELECT * 
FROM users;

Получение пользователя по идентификтору:
SELECT * 
FROM users 
WHERE id = ?;

Получение списка друзей пользователя по идентификтору:
SELECT *
FROM users u
JOIN friendship f ON u.user_id = f.friend_id
WHERE f.user_id = ?;