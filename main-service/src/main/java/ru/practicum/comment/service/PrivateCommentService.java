package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDtoOut;
import ru.practicum.comment.dto.NewCommentDto;

public interface PrivateCommentService {
    CommentDtoOut addComment(NewCommentDto newCommentDto, Long eventId, Long userId);

    void deleteComment(Long eventId, Long commentId, Long uerId);
}