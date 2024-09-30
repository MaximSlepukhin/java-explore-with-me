package ru.practicum.event.service.statistics;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Map;

public interface StatisticService {

    void saveViews(HttpServletRequest request);

    Map<Long, Long> getViews(List<Event> events);


}
