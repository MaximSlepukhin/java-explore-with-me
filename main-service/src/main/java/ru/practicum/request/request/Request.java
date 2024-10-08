package ru.practicum.request.request;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.enums.RequestState;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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

    @Enumerated(EnumType.STRING)
    private RequestState status;
}
