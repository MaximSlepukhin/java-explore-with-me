package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentDtoOut {

    private Long id;

    @NotNull
    private String text;

    @NotNull
    private String authorName;

    @NotNull
    private LocalDateTime created;
}
