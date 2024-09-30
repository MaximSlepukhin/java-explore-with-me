package ru.practicum.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

@Builder
@Data
public class EventShortDto {

    private Long id;

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    private String eventDate;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;
}
