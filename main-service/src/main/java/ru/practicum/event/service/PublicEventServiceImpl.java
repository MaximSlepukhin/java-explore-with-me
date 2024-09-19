package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.exception.IncorrectDataException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.PatchEventException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;

    private final StatisticService statisticService;

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, SortEnum sort,
                                         PageRequest pageRequest, HttpServletRequest request) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new IncorrectDataException("Некорректная дата старта выборки.");
            }
        }
        if (sort != null) {
            if (sort.equals(SortEnum.EVENT_DATE)) {
                pageRequest.withSort(Sort.by(Sort.Direction.ASC, "eventDate"));
            } else {
                pageRequest.withSort(Sort.by(Sort.Direction.DESC, "views"));
            }
        }
        List<Event> listOfEvents = eventRepository.getEvents(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, pageRequest);
        if (listOfEvents.isEmpty()) {
            return Collections.emptyList();
        }
        addViews(listOfEvents);
        statisticService.saveViews(request);
        return listOfEvents.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEvent(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + id + " не найдено."));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие с id:" + id + " имеет статус " + event.getState().toString());
        }
        addViews(List.of(event));
        statisticService.saveViews(request);
        return EventMapper.toEventFullDto(event);
    }

    private void addViews(List<Event> listOfEvents) {
        Map<Long, Long> eventAndViews = statisticService.getViews(listOfEvents);
        listOfEvents.forEach(e -> {
            e.setViews(eventAndViews.getOrDefault(e.getId(),0L));
        });
    }
}


