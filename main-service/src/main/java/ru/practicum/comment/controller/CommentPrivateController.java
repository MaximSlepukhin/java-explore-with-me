package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDtoOut;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.PrivateCommentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "events")
public class CommentPrivateController {

    private final PrivateCommentService commentPrivateService;

    @PostMapping("/{eventId}/comments/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDtoOut addComment(@RequestBody NewCommentDto newCommentDto,
                                    @PathVariable Long eventId,
                                    @PathVariable Long userId) {
        log.info("POST запрос на добавление комментария {}", newCommentDto);
        return commentPrivateService.addComment(newCommentDto, eventId, userId);
    }

    @DeleteMapping("comments/{eventId}/{commentId}/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long eventId, @PathVariable Long commentId,
                              @PathVariable Long userId) {
        log.info("DELETE запрос на удаление комментария с id=" + commentId + ".");
        commentPrivateService.deleteComment(eventId, commentId, userId);
    }
}
