package ru.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    LocalDateTime now = LocalDateTime.now();
    String formattedTimestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ApiError(HttpStatus.BAD_REQUEST.toString(),
                "Некорректные данные в запросе.",
                "При валидации объекта было обнаружено ошибок: " + e.getErrorCount(),
                formattedTimestamp,
                e.getAllErrors());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolationException(final ConstraintViolationException e) {
        return new ApiError(HttpStatus.CONFLICT.toString(),
                "Некорректные данные в запросе.",
                e.getMessage(),
                formattedTimestamp,
                null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleMethodArgumentNotValidException(final NotFoundException e) {
        return new ApiError(HttpStatus.NOT_FOUND.toString(),
                "Нужный объект не найден.",
                e.getMessage(),
                formattedTimestamp,
                null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        return new ApiError(HttpStatus.BAD_REQUEST.toString(),
                "Некорректные данные в запросе.",
                e.getMessage(),
                formattedTimestamp,
                null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNotValidException(final NotValidException e) {
        return new ApiError(HttpStatus.BAD_REQUEST.toString(),
                "Некорректные данные в запросе.",
                e.getMessage(),
                formattedTimestamp,
                null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlePatchEventException(final PatchEventException e) {
        return new ApiError(HttpStatus.BAD_REQUEST.toString(),
                "Не проходит по условиям обновления.",
                e.getMessage(),
                formattedTimestamp,
                null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectDataException(final IncorrectDataException e) {
        return new ApiError(HttpStatus.BAD_REQUEST.toString(),
                "Не проходит по условиям обновления.",
                e.getMessage(),
                formattedTimestamp,
                null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleAddEventException(final AddEventException e) {
        return new ApiError(HttpStatus.FORBIDDEN.toString(),
                "Не проходит по условиям добавления.",
                e.getMessage(),
                formattedTimestamp,
                null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUpdateStatusException(final UpdateStatusException e) {
        return new ApiError(HttpStatus.CONFLICT.toString(),
                "Не проходит по условиям обновления.",
                e.getMessage(),
                formattedTimestamp,
                null);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUpdateEventException(final UpdateEventException e) {
        return new ApiError(HttpStatus.CONFLICT.toString(),
                "Не проходит по условиям обновления.",
                e.getMessage(),
                formattedTimestamp,
                null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        return new ApiError(HttpStatus.CONFLICT.toString(),
                "Нарушение целостности данных.",
                e.getMessage(),
                formattedTimestamp,
                null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleAddRequestException(final AddRequestException e) {
        return new ApiError(HttpStatus.FORBIDDEN.toString(),
                "Не проходит по условиям добавления.",
                e.getMessage(),
                formattedTimestamp,
                null);
    }
}
