package ru.yandex.practicum.exceptions;

public class MessageTooLargeException extends RuntimeException {
    public MessageTooLargeException(String message) {
        super(message);
    }
}
