package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByIdIn(List<Long> eventIds);

    @Query("SELECT e  FROM Event e JOIN FETCH e.category AS c " +
            "WHERE (:text is null or lower(e.annotation) like lower(concat('%',:text,'%')) or " +
            ":text is null or lower(e.description) like lower(concat('%',:text,'%'))) " +
            "and (e.eventDate >= :start) " +
            "and (e.state = ru.practicum.enums.EventState.PUBLISHED) " +
            "and (:end is null or e.eventDate <= :end) " +
            "and (:paid is null or e.paid = :paid) " +
            "and (((e.participantLimit > e.confirmedRequests) and :available = true) or :available = false)")
    List<Event> getEvents(@Param("text") String text,
                          @Param("categories") List<Long> categories,
                          @Param("paid") Boolean paid,
                          @Param("start") LocalDateTime rangeStart,
                          @Param("end") LocalDateTime rangeEnd,
                          @Param("available") Boolean onlyAvailable,
                          Pageable pageable);


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
