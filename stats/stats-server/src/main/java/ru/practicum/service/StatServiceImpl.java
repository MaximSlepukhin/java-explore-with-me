package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.IncorrectDataException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;

    @Override
    public EndpointHitDto add(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = StatsMapper.toEndpointHit(endpointHitDto);
        endpointHit = statRepository.save(endpointHit);
        endpointHitDto.setId(endpointHit.getId());
        return endpointHitDto;
    }

    @Override
    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uri, Boolean unique) {
        List<String> listOfUris = new ArrayList<>();

        if (start.isAfter(end)) {
            throw new IncorrectDataException("Заданы неверные даты начала и конца диапазона времени.");
        }
        if (uri == null) {
            if (unique) {
                List<ViewStats> viewStatsList = statRepository.getViewStatsUnique(start, end);
                return viewStatsList.stream()
                        .map(StatsMapper::toViewStatsDto)
                        .collect(Collectors.toList());
            } else {
                List<ViewStats> viewStatsList = statRepository.getViewStats(start, end);
                return viewStatsList.stream()
                        .map(StatsMapper::toViewStatsDto)
                        .collect(Collectors.toList());
            }
        } else {
            for (String item : uri) {
                String[] parts = item.split("&uris=");
                listOfUris.add(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    listOfUris.add(parts[i]);
                }
            }
            if (unique) {
                List<ViewStats> viewStatsList = statRepository.getViewStatsUniqueByUris(start, end, listOfUris);
                return viewStatsList.stream()
                        .map(StatsMapper::toViewStatsDto)
                        .collect(Collectors.toList());
            } else {
                List<ViewStats> viewStatsList = statRepository.getViewStatsByUris(start, end, listOfUris);
                return viewStatsList.stream()
                        .map(StatsMapper::toViewStatsDto)
                        .collect(Collectors.toList());
            }
        }
    }
}