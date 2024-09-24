package ru.practicum.event.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.enums.AdminEventState;
import ru.practicum.event.location.Location;

@Builder
@Data
public class UpdateEventAdminRequest {

    private Long id;

    @Size(min = 20, max = 2000)
    private String annotation;

    private Integer categoryId;

    @Size(min = 20, max = 7000)
    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    private Long participantLimit;

    private Boolean requestModeration;

    private AdminEventState stateAction;

    @Size(min = 3, max = 120)
    private String title;
}
