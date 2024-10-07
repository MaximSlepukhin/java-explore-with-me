package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentDtoOut;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

public class CommentMapper {
    public static Comment toComment(NewCommentDto newCommentDto, User user, Event event) {
        return  Comment.builder()
                .text(newCommentDto.getText())
                .event(event)
                .user(user)
                .build();
    }

    public static CommentDtoOut toCommentDtoOut(Comment comment) {
        return  CommentDtoOut.builder()
                .id(comment.getId())
                .authorName(comment.getUser().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }
}
