package ru.practicum.exception;

import org.springframework.validation.ObjectError;

import java.util.List;

public record ApiError(String status, String reason, String message, String timestamp, List<ObjectError> errors) {
}
