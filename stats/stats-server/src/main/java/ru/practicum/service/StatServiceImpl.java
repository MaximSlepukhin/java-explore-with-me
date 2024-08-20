package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;
import ru.practicum.model.mapper.EndpointHitMapper;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;

    @Override
    public EndpointHitDto add(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHitMapper.toEndpointHit(endpointHitDto);
        endpointHit = statRepository.save(endpointHit);
        endpointHitDto.setId(endpointHit.getId());
        //добавить
        log.debug("");
        return endpointHitDto;
    }

    @Override
    public List<ViewStats> get(LocalDateTime start, LocalDateTime end, List<String> uri, Boolean unique) {
        if (start.isAfter(end)) {
            //добавить исключение
            throw new RuntimeException();
        }
        if (uri == null) {
            if (unique == true) {
                return statRepository.getViewStatsUnique(start, end);
            } else {
                return statRepository.getViewStats(start, end);
            }
        } else {
            if (unique == true) {
                return statRepository.getViewStatsUniqueByUris(start, end, uri);
            } else {
                return statRepository.getViewStatsByUris(start, end, uri);
            }
        }
    }
}