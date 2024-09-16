package ru.practicum.mapper;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.util.DateFormatter;

import java.time.LocalDateTime;

public class StatsMapper {

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHit.builder()
                .id(endpointHitDto.getId())
                .app(endpointHitDto.getApp())
                .ip(endpointHitDto.getIp())
                .uri(endpointHitDto.getUri())
                .timestamp(LocalDateTime.parse(endpointHitDto.getTimestamp(),DateFormatter.DATE_TIME_FORMATTER))
                .build();
        return endpointHit;
    }

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        ViewStatsDto viewStatsDto = ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
        return viewStatsDto;
    }
}
