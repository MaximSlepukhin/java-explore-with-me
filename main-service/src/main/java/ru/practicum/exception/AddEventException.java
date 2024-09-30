package ru.practicum.exception;

public class AddEventException extends RuntimeException {
    public AddEventException(String message) {
        super(message);
    }
}