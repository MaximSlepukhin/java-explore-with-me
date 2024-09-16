package ru.practicum.exception;

public class UpdateStatusException extends RuntimeException{
    public UpdateStatusException(String message) {
        super(message);
    }
}