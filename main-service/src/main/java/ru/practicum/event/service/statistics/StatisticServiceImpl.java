package ru.practicum.event.service.statistics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.model.Event;
import ru.practicum.util.DateFormatter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StatsClient statsClient;

    private final ObjectMapper objectMapper;

    @Value("${app.name}")
    private String appName;


    @Override
    public void saveViews(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();
        String timestamp = DateFormatter.format(LocalDateTime.now());
        EndpointHitDto endpointHitDto = EndpointHitDto
                .builder()
                .ip(ip)
                .app(appName)
                .uri(path)
                .timestamp(timestamp)
                .build();
        statsClient.add(endpointHitDto);
    }

    @Override
    public Map<Long, Long> getViews(List<Event> events) {
        Map<Long, Long> views = new HashMap<>();
        List<Event> publishedEvents = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .toList();
        if (publishedEvents.isEmpty()) {
            return views;
        }
        LocalDateTime minDate = publishedEvents.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .get();
        String[] uris = events.stream()
                .map(Event::getId)
                .map(id -> "/events/" + id)
                .toArray(String[]::new);
        LocalDateTime maxDate = LocalDateTime.now();
        List<ViewStatsDto> viewStatsDtos = getStats(minDate, maxDate, uris, true);
        views = viewStatsDtos.stream()
                .collect(Collectors.toMap(
                        dto -> Long.valueOf(dto.getUri().substring(dto.getUri().lastIndexOf("/") + 1)),
                        dto -> dto.getHits().longValue()));
        return views;
    }

    private List<ViewStatsDto> getStats(LocalDateTime minDate,
                                        LocalDateTime maxDate,
                                        String[] uris,
                                        Boolean unique) {
        String start = DateFormatter.format(minDate);
        String end = DateFormatter.format(maxDate);

        ResponseEntity<Object> viewStats = statsClient.get(start, end, uris, unique);
        //if (viewStats.getStatusCode().is2xxSuccessful() && viewStats.getBody() != null) {
        try {
            List<ViewStatsDto> newList = new ArrayList<>();
            String string = objectMapper.writeValueAsString(viewStats.getBody());
            System.out.println(string);
            newList = Arrays.asList(objectMapper.readValue(objectMapper.writeValueAsString(viewStats.getBody()), ViewStatsDto[].class));
            //if (newList.size() == 0) {
            //    throw new RuntimeException();
            //}
            return newList;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при обработке JSON: " + e.getMessage(), e);
        }
        //} else {
        //    throw new RuntimeException("Не удалось получить данные: " + viewStats.getStatusCode());
    }
}
