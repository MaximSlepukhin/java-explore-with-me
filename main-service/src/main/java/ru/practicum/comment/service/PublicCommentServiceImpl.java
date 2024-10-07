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

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PublicCommentServiceImpl implements PublicCommentService {
    private final CommentRepository commentRepository;

    private final EventRepository eventRepository;

    @Override
    public List<CommentDtoOut> getCommentsOfEvent(Long eventId, Pageable pageable) {
        Event event = findEventById(eventId);
        List<Comment> comments = commentRepository.findByEvent(event, pageable);
        return comments.stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(Collectors.toList());
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + eventId + " не найдено."));
    }
}
