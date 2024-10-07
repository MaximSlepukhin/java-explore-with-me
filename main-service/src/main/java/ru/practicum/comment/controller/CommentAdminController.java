package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDtoOut;
import ru.practicum.comment.service.AdminCommentService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "admin")
public class CommentAdminController {

    private final AdminCommentService adminCommentService;

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentById(@PathVariable Long commentId) {
        log.info("DELETE запрос на удаление комментария с id=" + commentId + ".");
        adminCommentService.deleteCommentById(commentId);
    }

    @GetMapping("/comments/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDtoOut> getCommentsOfUser(@PathVariable Long userId,
                                                 @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                 @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("GET запрос на получение списка пользователей.");
        Pageable pageable = PageRequest.of(from, size);
        return adminCommentService.getCommentsOfUser(userId, pageable);
    }
}