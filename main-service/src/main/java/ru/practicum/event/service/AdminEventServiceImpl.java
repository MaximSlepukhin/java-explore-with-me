package ru.practicum.event.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.enums.EventState;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.location.LocationRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.statistics.StatisticService;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.PatchEventException;
import ru.practicum.exception.UpdateStatusException;
import ru.practicum.util.DateFormatter;

import java.time.LocalDateTime;
import java.util.Collections;
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

    @Override
    public List<EventFullDto> getEventsForAdmin(Pageable pageable, Integer offset, Integer size, List<Long> users,
                                                List<String> states, List<Long> categories, LocalDateTime rangeEnd,
                                                LocalDateTime rangeStart) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new PatchEventException("Время старта позже времени окончания.");
            }
        }
        List<Event> events = eventRepository.getEventsForAdmin(users, states, categories, rangeStart, rangeEnd,
                pageable);
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Long> eventAndViews = statisticService.getViews(events);
        events.forEach(event -> event.setViews(eventAndViews.get(event.getId())));
        List<EventFullDto> eventFullDtos = events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
        if (eventFullDtos.size() > offset) {
            return eventFullDtos.subList(offset, Math.min(offset + size, eventFullDtos.size()));
        } else {
            return List.of();
        }
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + eventId + " не найдено."));
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
}