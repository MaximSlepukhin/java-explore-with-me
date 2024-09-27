package ru.practicum.event.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {

    List<EventFullDto> getEventsForAdmin(PageRequest pageRequest, Integer from, Integer size, List<Long> usersIds,
                                         List<String> states, List<Integer> categoriesIds, LocalDateTime rangeEnd, LocalDateTime rangeStart);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
