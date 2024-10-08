package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatService;
import ru.practicum.util.DateFormatter;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatsController {
    private final StatService statService;

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto add(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("POST запрос на сохранение информации о том, что к эндпоинту был запрос.");
        return statService.add(endpointHitDto);
    }

    @GetMapping(path = "/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> get(@RequestParam
                                  @DateTimeFormat(pattern = DateFormatter.DATE_TIME_PATTERN) LocalDateTime start,
                                  @RequestParam
                                  @DateTimeFormat(pattern = DateFormatter.DATE_TIME_PATTERN) LocalDateTime end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("StatsController", "GET запрос на получение статистики по посещениям.");
        List<ViewStatsDto> viewStatsDtoList = statService.get(start, end, uris, unique);
        return viewStatsDtoList;
    }
}
