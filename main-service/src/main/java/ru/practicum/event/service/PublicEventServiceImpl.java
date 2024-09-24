package ru.practicum.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.QCategory;
import ru.practicum.enums.EventState;
import ru.practicum.enums.SortEnum;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.statistics.StatisticService;
import ru.practicum.exception.IncorrectDataException;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;

    private final StatisticService statisticService;
    private final EntityManager entityManager;

    @Override
    public List<EventShortDto> getEvents(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, SortEnum sort,
                                         PageRequest pageRequest, HttpServletRequest request) {

        List<Long> longCategories = categories.stream()
                .map(Integer::longValue) // Преобразуем каждый Integer в Long
                .collect(Collectors.toList());

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new IncorrectDataException("Некорректная дата старта выборки.");
            }
        }
        QEvent event = QEvent.event;
        QCategory category = QCategory.category;
        JPAQuery<Event> query = new JPAQuery<>(entityManager);
        query.from(event)
                .join(event.category, category).fetchJoin();
        BooleanExpression predicate = event.state.eq(EventState.PUBLISHED);
        if (rangeStart != null) {
            predicate = predicate.and(event.eventDate.goe(rangeStart));
        }
        if (text != null && !text.isEmpty()) {
            predicate = predicate.and(event.annotation.toLowerCase().like("%" + text.toLowerCase() + "%")
                    .or(event.description.toLowerCase().like("%" + text.toLowerCase() + "%")));
        }
        if (rangeEnd != null) {
            predicate = predicate.and(event.eventDate.loe(rangeEnd));
        }
        if (categories != null && !categories.isEmpty()) {
            predicate = predicate.and(event.category.id.in(longCategories));
        }
        if (paid != null) {
            predicate = predicate.and(event.paid.eq(paid));
        }
        if (onlyAvailable != null) {
            predicate = predicate.and(onlyAvailable ? event.participantLimit.gt(event.confirmedRequests) : null);
        }
        query.where(predicate);
        query.offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize());
        List<Event> events = query.fetch();
        Map<Long, Long> eventAndViews = statisticService.getViews(events);
        events.forEach(e -> {
            e.setViews(eventAndViews.getOrDefault(e.getId(), 0L));
        });
        if (sort != null) {
            switch (sort) {
                case EVENT_DATE:
                    events.sort(Comparator.comparing(Event::getEventDate));
                    break;
                case VIEWS:
                    events.sort(Comparator.comparing(Event::getViews).reversed());
                    break;
            }
        }
        statisticService.saveViews(request);
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
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


