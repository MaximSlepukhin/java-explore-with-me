package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.location.Location;
import ru.practicum.event.model.Event;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.util.DateFormatter;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {
    public static Event toEvent(NewEventDto newEventDto, Category category, User initiator, Location location) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .description(newEventDto.getDescription())
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), DateFormatter.DATE_TIME_FORMATTER))
                .location(location)
                .initiator(initiator)
                .requestModeration(newEventDto.getRequestModeration())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .title(newEventDto.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(DateFormatter.format(event.getCreatedOn()))
                .description(event.getDescription())
                .eventDate(DateFormatter.format(event.getEventDate()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(DateFormatter.format(event.getEventDate()))
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(DateFormatter.format(event.getEventDate()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}
