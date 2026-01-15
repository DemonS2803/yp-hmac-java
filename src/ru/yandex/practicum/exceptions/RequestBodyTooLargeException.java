package ru.yandex.practicum.exceptions;

public class RequestBodyTooLargeException extends RuntimeException {
    public RequestBodyTooLargeException(String message) {
        super(message);
    }
}
