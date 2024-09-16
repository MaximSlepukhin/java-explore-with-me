package ru.practicum.request.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestPrivateService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class RequestPrivateController {

    private final RequestPrivateService requestPrivateService;

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId) {
        log.info("GET запрос на получение информации о заявках пользователя с id:"
                + userId + " на участие в чужих событиях");
        return requestPrivateService.getRequests(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable Long userId, @RequestParam Long eventId, HttpServletRequest httpServletRequest) {
        log.info("POST запрос на добавление запроса от пользователя с id:" + userId + " на участие в событии.");
        log.info("" + httpServletRequest.getQueryString());
        return requestPrivateService.addRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        log.info("PATCH запрос на отмену запроса на участие в событии от пользователя с id:" + userId);
        return requestPrivateService.updateRequest(userId, requestId);
    }
}
