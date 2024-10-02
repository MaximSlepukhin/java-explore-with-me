package ru.practicum.comment.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.comment.dto.CommentDtoOut;

import java.util.List;

public interface PublicCommentService {

    List<CommentDtoOut> getCommentsOfEvent(Long eventId, Pageable pageable);
}
