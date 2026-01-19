package ru.yandex.practicum.exceptions;

public class InvalidConfigFormatException extends RuntimeException {
    public InvalidConfigFormatException(String message) {
        super(message);
    }
}
