package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.PublicCompilationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationPublicController {

    private final PublicCompilationService publicCompilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                                @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        log.info("GET запрос на получение подборок событий.");
        int page = from / size;
        int offset = from % size;
        Pageable pageable = PageRequest.of(page, size);
        return publicCompilationService.getCompilations(pinned, pageable, offset, size);
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilation(@PathVariable Long compId) {
        log.info("GET запрос на получение подборки по id=" + compId);
        return publicCompilationService.getCompilation(compId);
    }
}
