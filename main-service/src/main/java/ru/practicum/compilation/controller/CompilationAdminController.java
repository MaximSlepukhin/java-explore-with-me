package ru.practicum.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.AdminCompilationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
@Validated
public class CompilationAdminController {

    private final AdminCompilationService adminCompilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("POST запрос на добавление новой подборки.");
        return adminCompilationService.addCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("DELETE запрос на удаление подборки.");
        adminCompilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@Valid @RequestBody UpdateCompilationRequest updateCompilationRequest,
                                            @PathVariable Long compId) {
        log.info("PATCH запрос на обновление подборки.");
        return adminCompilationService.updateCompilation(updateCompilationRequest, compId);
    }
}
