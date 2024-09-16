package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable, Integer offset, Integer size) {
        List<Compilation> compilationList = new ArrayList<>();

        if (pinned == null) {
            pinned = true;
            compilationList = compilationRepository.findCompilationByPinned(pinned, pageable);

        } else {
            pinned = false;
            compilationList = compilationRepository.findCompilationByPinned(pinned, pageable);
        }
        if (compilationList.isEmpty()) {
            return Collections.emptyList();
        }

        List<CompilationDto> compilationDtoList = compilationList.stream()
                .map(compilation -> {
                    List<EventShortDto> eventShortDtos = getEventShortDtos(compilation.getEvents());
                    return CompilationMapper.toCompilationDto(compilation, eventShortDtos);
                })
                .collect(Collectors.toList());
        return compilationDtoList;
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id:" + compId + " не найдена."));
        List<Event> listOfEvents = compilation.getEvents();
        List<EventShortDto> eventShortDtoList = listOfEvents.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        return CompilationMapper.toCompilationDto(compilation, eventShortDtoList);
    }

    private List<EventShortDto> getEventShortDtos(List<Event> events) {
        return events.stream().map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }
}
