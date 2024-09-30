package ru.practicum.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.event.dto.*;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsOfUser(Long userId, Pageable pageable, Integer from, Integer size);

    EventFullDto getFullEventOfUser(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getInfoAboutRequestsOfEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId,
                                                EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
