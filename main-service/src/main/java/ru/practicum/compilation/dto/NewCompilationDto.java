package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NewCompilationDto {

    private List<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50)
    @NotBlank
    private String title;
}
