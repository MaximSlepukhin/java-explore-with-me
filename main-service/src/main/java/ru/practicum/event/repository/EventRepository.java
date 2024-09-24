package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {


    List<Event> findByIdIn(List<Long> eventIds);

    @Query("SELECT e FROM Event e JOIN FETCH e.category AS c JOIN FETCH e.initiator AS i " +
            "WHERE (:users is null or e.initiator.id in :users ) " +
            "and (:categories is null or e.category.id in :categories ) " +
            "and (:states is null or e.state in :states )" +
            "and (:start is null or e.eventDate >=:start ) " +
            "and (:end is null or e.eventDate <= :end)")
    List<Event> getEventsForAdmin(@Param("users") List<Long> usersIds,
                                  @Param("states") List<String> states,
                                  @Param("categories") List<Long> categories,
                                  @Param("start") LocalDateTime rangeStart,
                                  @Param("end") LocalDateTime rangeEnd, Pageable pageable);

    List<Event> findByInitiator(User initiator, Pageable pageable);
}
