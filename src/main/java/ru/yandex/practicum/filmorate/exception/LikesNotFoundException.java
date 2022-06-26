package ru.yandex.practicum.filmorate.exception;

public class LikesNotFoundException extends RuntimeException {
    public LikesNotFoundException(String message) {
        super(message);
    }
}
