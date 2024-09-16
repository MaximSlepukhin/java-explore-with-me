package ru.practicum.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.location.Location;
import ru.practicum.user.dto.UserShortDto;

@Builder
@Data
public class EventFullDto {

    private Long id;

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    private String createdOn;

    private String description;

    private String eventDate;

    private UserShortDto initiator;

    private Location location;

    private Boolean paid;

    private Long participantLimit;

    private String publishedOn;

    private Boolean requestModeration;

    private String state;

    private String title;

    private Long views;
}
