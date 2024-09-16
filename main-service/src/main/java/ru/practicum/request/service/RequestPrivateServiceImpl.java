package ru.practicum.request.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestState;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.AddRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.request.request.Request;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestPrivateServiceImpl implements RequestPrivateService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        findUserById(userId);
        List<Request> request = requestRepository.findRequestByRequesterId(userId);
        List<ParticipationRequestDto> participationRequestDtos = request.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        return participationRequestDtos;
    }

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        Event event = findEventById(eventId);
        User user = findUserById(userId);
        if (user.getId().equals(event.getInitiator().getId())) {
            throw new AddRequestException("Инициатор события не может добавить запрос на участие в своем событии.");
        }
        Request request = requestRepository.findByRequesterAndEvent(user, event);
        if (request != null) {
            throw new AddRequestException("Нельзя добавить повторный запрос.");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new AddRequestException("Нельзя участвовать в неопубликованном событии.");
        }
        if (event.getParticipantLimit().equals(event.getConfirmedRequests()) && event.getParticipantLimit() != 0) {
            throw new AddRequestException("У события достигнут лимит запросов на участие.");
        }
        Request newRequest = new Request();
        newRequest.setRequester(user);
        newRequest.setEvent(event);
        newRequest.setStatus(RequestState.PENDING);
        newRequest.setCreated(LocalDateTime.now());
        if (!event.getRequestModeration()) {
            newRequest.setStatus(RequestState.CONFIRMED);
        }
        requestRepository.save(newRequest);
        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto updateRequest(Long userId, Long requestId) {
        findUserById(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id:" + requestId + " не найден."));
        if (!userId.equals(request.getRequester().getId())) {
            throw new RuntimeException();
        } else {
            request.setStatus(RequestState.CANCELED);
        }
        requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(request);
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
