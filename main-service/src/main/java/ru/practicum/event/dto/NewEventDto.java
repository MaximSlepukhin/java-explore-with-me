package ru.practicum.event.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.practicum.event.location.Location;

@Builder
@Data
public class NewEventDto {

    @Size(min = 20, max = 2000)
    @NotBlank
    private String annotation;

    @NotNull
    private Long category;

    @Size(min = 20, max = 7000)
    @NotBlank
    private String description;

    @NotNull
    private String eventDate;

    @NotNull
    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Long participantLimit;

    private Boolean requestModeration;

    @Size(min = 3, max = 120)
    private String title;
}
