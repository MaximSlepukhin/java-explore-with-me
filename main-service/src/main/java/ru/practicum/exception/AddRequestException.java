package ru.practicum.exception;

public class AddRequestException extends RuntimeException {
    public AddRequestException(String message) {
        super(message);
    }
}
