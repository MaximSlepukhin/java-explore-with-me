package ru.practicum.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.compilation.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {

    List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable, Integer from, Integer size);

    CompilationDto getCompilation(Long compId);
}
