package ru.practicum.event.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.enums.PrivateEventState;
import ru.practicum.event.location.Location;

@Data
@Builder
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Integer category;

    @Size(min = 20, max = 7000)
    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    @Positive
    private Long participantLimit;

    private Boolean requestModeration;

    private PrivateEventState stateAction;

    @Size(min = 3, max = 120)
    private String title;
}
