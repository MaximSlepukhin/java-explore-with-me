package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.AdminEventService;
import ru.practicum.util.DateFormatter;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
@Validated
public class EventAdminController {

    private final AdminEventService adminEventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEventsForAdmin(@RequestParam(required = false) List<Long> users,
                                                @RequestParam(required = false) List<String> states,
                                                @RequestParam(required = false) List<Integer> categories,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = DateFormatter.DATE_TIME_PATTERN) LocalDateTime rangeStart,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = DateFormatter.DATE_TIME_PATTERN) LocalDateTime rangeEnd,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("GET запрос на поиск событий.");
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return adminEventService.getEventsForAdmin(pageRequest, from, size, users, states,
                categories, rangeEnd, rangeStart);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("PATCH запрос на редкатирование данных события и его статуса (отклонение/публикация)");
        return adminEventService.updateEvent(eventId, updateEventAdminRequest);
    }
}
