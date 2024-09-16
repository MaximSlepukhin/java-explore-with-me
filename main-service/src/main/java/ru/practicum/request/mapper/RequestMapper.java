package ru.practicum.request.mapper;

import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.request.Request;
import ru.practicum.util.DateFormatter;


public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(DateFormatter.format(request.getCreated()))
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().toString())
                .build();
    }
}
