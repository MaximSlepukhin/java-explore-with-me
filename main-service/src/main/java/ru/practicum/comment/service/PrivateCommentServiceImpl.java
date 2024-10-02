package ru.practicum.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDtoOut;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ErrorCommentException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.NotValidException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

@Service
@AllArgsConstructor
public class PrivateCommentServiceImpl implements PrivateCommentService {
    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Override
    public CommentDtoOut addComment(NewCommentDto newCommentDto, Long eventId, Long userId) {
        User user = findUserById(userId);
        Event event = findEventById(eventId);
        if(newCommentDto.getText().isEmpty() || newCommentDto.getText() == null) {
            throw new NotValidException("Отсутствуют данные в поле text.");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotValidException("Оставлять комментарии можно только у опубликованных событиях.");
        }
        Comment comment = CommentMapper.toComment(newCommentDto, user, event);
        commentRepository.save(comment);
        return CommentMapper.toCommentDtoOut(comment);

    }

    @Override
    public void deleteComment(Long eventId, Long commentId, Long userId) {
        findUserById(userId);
        findEventById(eventId);
        Comment comment = findCommentById(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new ErrorCommentException("Удалить комментарий может только его автор.");
        }
        commentRepository.delete(comment);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден."));
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + eventId + " не найдено."));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id:" + commentId + " не найден."));
    }
}