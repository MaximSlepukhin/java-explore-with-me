package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.model.ViewStats(ep.app, ep.uri, count(ep.ip)) " +
            "from EndpointHit as ep " +
            "where ep.timestamp between ?1 and ?2 " +
            "group by ep.app, ep.uri " +
            "order by count(ep.ip) DESC")
    List<ViewStats> getViewStats(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.model.ViewStats(ep.app, ep.uri, count(DISTINCT (ep.ip))) " +
            "from EndpointHit as ep " +
            "where ep.timestamp between ?1 and ?2 " +
            "group by ep.app, ep.uri " +
            "order by count(ep.ip) DESC")
    List<ViewStats> getViewStatsUnique(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.model.ViewStats(ep.app, ep.uri, count(ep.ip)) " +
            "from EndpointHit as ep " +
            "where ep.timestamp between ?1 and ?2 and ep.uri in ?3 " +
            "group by ep.app, ep.uri " +
            "order by count(ep.ip) DESC")
    List<ViewStats> getViewStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.model.ViewStats(ep.app, ep.uri, count(DISTINCT (ep.ip))) " +
            "from EndpointHit as ep " +
            "where ep.timestamp between ?1 and ?2 and ep.uri in ?3 " +
            "group by ep.app, ep.uri " +
            "order by count(ep.ip) DESC")
    List<ViewStats> getViewStatsUniqueByUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
