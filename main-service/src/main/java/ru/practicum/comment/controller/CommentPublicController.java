package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDtoOut;
import ru.practicum.comment.service.PublicCommentService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "events")
public class CommentPublicController {

    private final PublicCommentService publicCommentService;

    @GetMapping("comments/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDtoOut> getCommentsOfEvent(@PathVariable Long eventId,
                                                  @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("GET запрос на получение комментариев к событию.");
        Pageable pageable = PageRequest.of(from, size);
        return publicCommentService.getCommentsOfEvent(eventId, pageable);
    }
}
