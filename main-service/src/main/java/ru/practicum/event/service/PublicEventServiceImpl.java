package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.enums.EventState;
import ru.practicum.enums.SortEnum;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.statistics.StatisticService;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.PatchEventException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;

    private final StatisticService statisticService;

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, SortEnum sort,
                                         PageRequest pageRequest, HttpServletRequest request) {
        if (rangeStart.isAfter(rangeEnd)) {
            throw new PatchEventException("Время старта позже времени окончания.");
        }
        if (sort != null) {
            if (sort.equals(SortEnum.EVENT_DATE)) {
                pageRequest.withSort(Sort.by(Sort.Direction.ASC, "eventDate"));
            } else {
                pageRequest.withSort(Sort.by(Sort.Direction.DESC, "views"));
            }
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        List<Event> listOfEvents = eventRepository.getEvents(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, pageRequest);
        Map<Long, Long> eventAndViews = statisticService.getViews(listOfEvents);
        listOfEvents.stream()
                .forEach(event -> {
                    event.setViews(eventAndViews.get(event.getId()));
                });
        statisticService.saveViews(request);
        if (listOfEvents.isEmpty()) {
            return Collections.emptyList();
        }
        return listOfEvents.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEvent(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + id + " не найдено."));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие с id:" + id + " имеет статус " + event.getState().toString());
        }
        Map<Long, Long> eventAndViews = statisticService.getViews(List.of(event));
        statisticService.saveViews(request);
        Long views = eventAndViews.get(event.getId());
        event.setViews(views);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        return EventMapper.toEventFullDto(event);
    }
}
