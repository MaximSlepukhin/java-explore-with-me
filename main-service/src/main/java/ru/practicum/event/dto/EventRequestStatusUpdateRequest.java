package ru.practicum.event.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    private String status;
}
