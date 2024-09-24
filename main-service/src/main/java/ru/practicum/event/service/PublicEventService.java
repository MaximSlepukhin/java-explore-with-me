package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.enums.SortEnum;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {

    List<EventShortDto> getEvents(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                  Boolean onlyAvailable, SortEnum sort, PageRequest pageRequest, HttpServletRequest request);

    EventFullDto getEvent(Long id, HttpServletRequest request);
}
