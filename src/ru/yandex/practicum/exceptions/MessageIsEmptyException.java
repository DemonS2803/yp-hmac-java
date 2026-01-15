package ru.yandex.practicum.exceptions;

public class MessageIsEmptyException extends RuntimeException {
    public MessageIsEmptyException(String message) {
        super(message);
    }
}
