package ru.practicum.request.request;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.enums.RequestState;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@Table(name = "requests")
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "requester_id")
    @ManyToOne
    private User requester;

    @JoinColumn(name = "event_id")
    @ManyToOne
    private Event event;

    @Column(name = "created_date")
    private LocalDateTime created;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RequestState status;
}
