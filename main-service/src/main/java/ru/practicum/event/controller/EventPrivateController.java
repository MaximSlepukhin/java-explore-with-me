package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.PrivateEventService;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class EventPrivateController {

    private final PrivateEventService privateEventService;

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsOfUser(@PathVariable Long userId,
                                               @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                               @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        int page = from / size;
        int offset = from % size;
        Pageable pageable = PageRequest.of(page, size);
        log.info("GET запрос на получение событий, добавленных пользователем с id:" + userId);
        return privateEventService.getEventsOfUser(userId, pageable, offset, size);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @Valid @RequestBody NewEventDto newEventDto) {
        log.info("POST запрос на добавление нового события.");
        return privateEventService.addEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getFullEventOfUser(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("GET запрос на получение полной информации о событии c id:" + eventId + "," +
                " добавленном пользователем с id:" + userId);
        return privateEventService.getFullEventOfUser(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("PATCH запрос на изменение события с id:" + eventId + ", добавленного пользователем с id:" + userId);
        return privateEventService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getInfoAboutRequestsOfEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("GET запрос на получение информации о запросах на участие в событии с id:"
                + eventId + " пользователя с id:" + userId);
        return privateEventService.getInfoAboutRequestsOfEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)

    public EventRequestStatusUpdateResult updateStatus(@PathVariable Long userId, @PathVariable Long eventId,
                                                       @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("PATCH запрос на изменение статуса заявок на участие в событии текущего пользователя.");
        return privateEventService.updateStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
