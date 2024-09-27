package ru.practicum.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.QCategory;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.enums.EventState;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.location.LocationRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.statistics.StatisticService;
import ru.practicum.exception.IncorrectDataException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.PatchEventException;
import ru.practicum.exception.UpdateStatusException;
import ru.practicum.util.DateFormatter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    public static final Integer HOUR_LIMIT = 1;
    private final EventRepository eventRepository;
    private final StatisticService statisticService;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EntityManager entityManager;

    @Override
    public List<EventFullDto> getEventsForAdmin(PageRequest pageRequest, Integer from, Integer size, List<Long> users,
                                                List<String> states, List<Integer> categories, LocalDateTime rangeEnd,
                                                LocalDateTime rangeStart) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new IncorrectDataException("Время старта позже времени окончания.");
            }
        }
        QEvent event = QEvent.event;
        QCategory category = QCategory.category;
        JPAQuery<Event> query = new JPAQuery<>(entityManager);
        query.from(event)
                .join(event.category, category).fetchJoin();

        BooleanExpression predicate = event.eventDate.goe(rangeStart);

        if (rangeStart != null) {
            predicate = predicate.and(event.eventDate.goe(rangeStart));
        }
        if (rangeEnd != null) {
            predicate = predicate.and(event.eventDate.loe(rangeEnd));
        }

        if (categories != null && !categories.isEmpty()) {
            predicate = predicate.and(event.category.id.in(categories));
        }
        if (users != null && !users.isEmpty()) {
            predicate = predicate.and(event.initiator.id.in(users));
        }

        query.where(predicate);
        query.offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize());

        List<Event> events = query.fetch();
        Map<Long, Long> eventAndViews = statisticService.getViews(events);
        events.forEach(e -> {
            e.setViews(eventAndViews.getOrDefault(e.getId(), 0L));
        });
        Integer fromDefault = 0;
        Integer sizeDefault = 0;
        if (from == null) {
            fromDefault = 0;
        } else {
            fromDefault = from;
        }
        if (size == null) {
            sizeDefault = 10;
        } else {
            sizeDefault = size;
        }
        events.stream().filter(even -> states.contains(even.getState()))
                .collect(Collectors.toList());
        events.stream().skip(fromDefault)
                .limit(sizeDefault);
        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + eventId + " не найдено."));
        if (updateEventAdminRequest.getEventDate() != null) {
            LocalDateTime newEventDate;
            try {
                newEventDate = LocalDateTime.parse(updateEventAdminRequest.getEventDate(), DateFormatter.DATE_TIME_FORMATTER);
            } catch (Exception e) {
                throw new IncorrectDataException("Некорректная дата начала события.");
            }
            if (newEventDate.isBefore(LocalDateTime.now())) {
                throw new IncorrectDataException("Новая дата события предшествует текущей дате.");
            }
        }
        if (event.getPublishedOn() != null && LocalDateTime.now().plusHours(HOUR_LIMIT).isBefore(event.getPublishedOn())) {
            throw new PatchEventException("Дата изменяемого события должна быть не ранее чем за час от даты публикации.");
        }
        if (event.getState().equals(EventState.PUBLISHED) || event.getState().equals(EventState.CANCELED)) {
            throw new UpdateStatusException("Событие можно опубликовать, только если оно в состоянии ожидания публикации.");
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Категория с id:" + updateEventAdminRequest.getCategoryId()
                            + " не найдена."));
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateEventAdminRequest.getEventDate(), DateFormatter.DATE_TIME_FORMATTER));
        }
        if (updateEventAdminRequest.getLocation() != null) {
            locationRepository.save(updateEventAdminRequest.getLocation());
            event.setLocation(updateEventAdminRequest.getLocation());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT -> {
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> {
                    event.setState(EventState.CANCELED);
                    event.setPublishedOn(null);
                }
            }
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        event.setViews(0L);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    private void addViews(List<Event> listOfEvents) {
        Map<Long, Long> eventAndViews = statisticService.getViews(listOfEvents);
        listOfEvents.forEach(e -> {
            e.setViews(eventAndViews.getOrDefault(e.getId(), 0L));
        });
    }
}