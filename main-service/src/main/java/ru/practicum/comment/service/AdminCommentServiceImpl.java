package ru.practicum.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDtoOut;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {

    private final EventRepository eventRepository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;
    @Override
    public void deleteCommentById(Long commentId) {
        Comment comment = findCommentById(commentId);
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDtoOut> getCommentsOfUser(Long userId, Pageable pageable) {
        User user = findUserById(userId);
        List<Comment> comments = commentRepository.findByUser(user, pageable);
        return comments.stream().
                map(CommentMapper::toCommentDtoOut)
                .collect(Collectors.toList());
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + eventId + " не найдено."));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id:" + commentId + " не найден."));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден."));
    }
}
