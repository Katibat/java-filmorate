CREATE TABLE IF NOT EXISTS MPA (
    MPA_ID INT PRIMARY KEY NOT NULL,
    NAME VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS GENRE (
    GENRE_ID INT PRIMARY KEY NOT NULL,
    NAME VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS FILMS (
    FILM_ID LONG NOT NULL AUTO_INCREMENT,
    NAME VARCHAR(200) NOT NULL UNIQUE,
    DESCRIPTION VARCHAR(200) NOT NULL,
    RELEASE_DATE DATE NOT NULL UNIQUE,
    DURATION INT,
    MPA_ID INT REFERENCES MPA (MPA_ID) ON DELETE CASCADE,
    CONSTRAINT PK_FILMS PRIMARY KEY (FILM_ID)
);

CREATE TABLE IF NOT EXISTS FILM_GENRE (
    FILM_ID INT NOT NULL,
    GENRE_ID INT NOT NULL,
    CONSTRAINT PK_FILM_GENRE PRIMARY KEY (FILM_ID, GENRE_ID),
    CONSTRAINT FK_FILM_GENRE_FILM_ID FOREIGN KEY(FILM_ID) REFERENCES FILMS (FILM_ID),
    CONSTRAINT FK_FILM_GENRE_GENRE_ID FOREIGN KEY(GENRE_ID) REFERENCES GENRE (GENRE_ID)
);

CREATE TABLE IF NOT EXISTS USERS (
    USER_ID LONG NOT NULL AUTO_INCREMENT,
    NAME VARCHAR(200) NOT NULL,
    LOGIN VARCHAR(200) NOT NULL UNIQUE,
    EMAIL VARCHAR(200) NOT NULL UNIQUE,
    BIRTHDAY DATE NOT NULL,
    CONSTRAINT PK_USERS PRIMARY KEY (USER_ID)
);

CREATE TABLE IF NOT EXISTS LIKES (
    FILM_ID LONG REFERENCES FILMS (FILM_ID) ON DELETE CASCADE NOT NULL,
    USER_ID LONG REFERENCES USERS (USER_ID) ON DELETE CASCADE NOT NULL,
    CONSTRAINT PK_LIKES PRIMARY KEY (FILM_ID, USER_ID)
);

CREATE TABLE IF NOT EXISTS FRIENDSHIP (
    USER_ID LONG REFERENCES USERS (USER_ID) ON DELETE CASCADE NOT NULL,
    FRIEND_ID LONG REFERENCES USERS (USER_ID) ON DELETE CASCADE NOT NULL,
    CONSTRAINT PK_FRIENDSHIP PRIMARY KEY (USER_ID, FRIEND_ID)
);