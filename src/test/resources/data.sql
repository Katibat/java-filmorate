MERGE INTO GENRE KEY (genre_id) VALUES (1, 'Комедия');
MERGE INTO GENRE KEY (genre_id) VALUES (2, 'Драма');
MERGE INTO GENRE KEY (genre_id) VALUES (3, 'Мультфильм');
MERGE INTO GENRE KEY (genre_id) VALUES (4, 'Триллер');
MERGE INTO GENRE KEY (genre_id) VALUES (5, 'Документальный');
MERGE INTO GENRE KEY (genre_id) VALUES (6, 'Боевик');

INSERT INTO USERS (email, login, name, birthday)
VALUES ('user1@ya.ru', 'login1', 'user1', '1995-05-05');
INSERT INTO USERS (email, login, name, birthday)
VALUES ('user2@ya.ru', 'login2', 'user2', '1996-06-06');
INSERT INTO USERS (email, login, name, birthday)
VALUES ('user3@ya.ru', 'login3', 'user3', '1997-07-07');

INSERT INTO FILMS (name, description, release_date, duration, mpa_id)
VALUES ('film1', 'description1', '1989-01-01', 120, 1);
INSERT INTO FILMS (name, description, release_date, duration, mpa_id)
VALUES ('film2', 'description2', '1996-05-05', 105, 2);

INSERT INTO friendship VALUES (1, 3);
INSERT INTO friendship VALUES (2, 3);