package ru.practicum.event.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestState;
import ru.practicum.event.dto.*;
import ru.practicum.event.location.Location;
import ru.practicum.event.location.LocationRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.NotValidException;
import ru.practicum.exception.PatchEventException;
import ru.practicum.exception.UpdateStatusException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.request.request.Request;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.CollectionUtils;
import ru.practicum.util.DateFormatter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;

    private final UserRepository userRepository;

    private final RequestRepository requestRepository;

    private final static Long MIN_TIME = 2L;

    @Override
    public List<EventShortDto> getEventsOfUser(Long userId, Pageable pageable, Integer offset, Integer size) {
        User user = findUserById(userId);
        List<Event> events = eventRepository.findByInitiator(user, pageable);
        List<Event> resultList = CollectionUtils.getSublistByOffset(events, offset, size);
        return resultList.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        if (LocalDateTime.parse(newEventDto.getEventDate(), DateFormatter.DATE_TIME_FORMATTER).
                isBefore(LocalDateTime.now().plusHours(MIN_TIME))) {
            throw new NotValidException("Дата и время на которые намечено событие не может быть раньше, чем через два" +
                    " часа от текущего момента.");
        }
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с id:"
                        + newEventDto.getCategory() + " не найдена."));
        User initiator = findUserById(userId);
        Location location = locationRepository.save(newEventDto.getLocation());
        Event event = EventMapper.toEvent(newEventDto, category, initiator, location);
        event.setConfirmedRequests(0L);
        event.setViews(0L);
        if (newEventDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0L);
        } else {
            event.setParticipantLimit(newEventDto.getParticipantLimit());
        }
        if (newEventDto.getPaid() == null) {
            event.setPaid(false);
        }
        if (newEventDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        event.setState(EventState.PENDING);
        event = eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getFullEventOfUser(Long userId, Long eventId) {
        User user = findUserById(userId);
        Event event = findEventById(eventId);
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new NotFoundException("Пользователь не является инициатором события.");
        }
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        findUserById(userId);
        Event event = findEventById(eventId);
        if ((event.getState().equals(EventState.PUBLISHED))) {
            throw new UpdateStatusException("Изменить можно только отмененные события илли события в состоянии ожидания модерации.");
        }
        if (updateEventUserRequest.getEventDate() != null && LocalDateTime.parse(updateEventUserRequest.getEventDate(), DateFormatter.DATE_TIME_FORMATTER).
                isBefore(LocalDateTime.now().plusHours(MIN_TIME))) {
            throw new PatchEventException("Дата и время на которые намечено событие не может быть раньше, чем через два" +
                    " часа от текущего момента.");
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            Long catId = updateEventUserRequest.getCategory();
            Category category = categoryRepository.findById(catId)
                    .orElseThrow(() -> new NotFoundException("Категория с id:" + catId + " не найдена."));
            event.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateEventUserRequest.getEventDate(), DateFormatter.DATE_TIME_FORMATTER));
        }
        if (updateEventUserRequest.getLocation() != null) {
            locationRepository.save(updateEventUserRequest.getLocation());
            event.setLocation(updateEventUserRequest.getLocation());
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            }
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getInfoAboutRequestsOfEvent(Long userId, Long eventId) {
        User user = findUserById(userId);
        Event event = findEventById(eventId);
        if (event.getInitiator().equals(user.getId())) {
            throw new RuntimeException();
        }
        List<Request> requestList = requestRepository.findByEvent(event);

        return requestList.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        findUserById(userId);
        Event event = findEventById(eventId);
        List<Request> requestList = requestRepository.findRequestByIdIn(eventRequestStatusUpdateRequest.getRequestIds());
        if (eventRequestStatusUpdateRequest.getStatus().equals("REJECTED")) {
            requestList.stream()
                    .peek(request -> request.setStatus(RequestState.CANCELED))
                    .map(requestRepository::save)
                    .collect(Collectors.toList());
        }
        boolean verified = requestList.stream()
                .allMatch(request -> request.getEvent().getId().longValue() == eventId);
        if (!verified) {
            throw new RuntimeException();
        }
        boolean listWithVerifiedStatus = requestList.stream()
                .allMatch(request -> request.getStatus().equals(RequestState.PENDING));
        if (!listWithVerifiedStatus) {
            throw new UpdateStatusException("Статус можно изменить только у заявок, находящихся в состоянии ожидания.");
        }
        if (event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new RuntimeException();
        }
        long numberForConfirm = event.getParticipantLimit() - event.getConfirmedRequests();
        List<Request> rejectedList = requestList.stream()
                .skip(numberForConfirm)
                .peek(request -> request.setStatus(RequestState.REJECTED))
                .map(requestRepository::save).toList();
        List<Request> confirmedList = requestList.stream()
                .limit(numberForConfirm)
                .peek(request -> request.setStatus(RequestState.CONFIRMED))
                .map(requestRepository::save).toList();
        List<ParticipationRequestDto> confirmedRequests = confirmedList.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        List<ParticipationRequestDto> rejectedRequests = rejectedList.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult =
                new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        return eventRequestStatusUpdateResult;
    }


    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден."));
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + eventId + " не найдено."));
    }
}
