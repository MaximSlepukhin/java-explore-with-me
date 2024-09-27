package ru.practicum.enums;

import lombok.ToString;

@ToString
public enum RequestState {
    CONFIRMED,
    PENDING,
    CANCELED,
    REJECTED
}
