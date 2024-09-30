package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.request.request.Request;
import ru.practicum.user.model.User;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findRequestByRequesterId(Long userId);

    List<Request> findRequestByIdIn(List<Long> ids);

    Request findByRequesterAndEvent(User user, Event event);

    List<Request> findByEvent(Event event);
}
