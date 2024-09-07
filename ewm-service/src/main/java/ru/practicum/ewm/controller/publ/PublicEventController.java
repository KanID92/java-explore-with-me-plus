package ru.practicum.ewm.controller.publ;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.ewm.controller.params.EventGetByIdParams;
import ru.practicum.ewm.controller.params.search.EventSearchParams;
import ru.practicum.ewm.controller.params.search.PublicSearchParams;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.entity.EventState;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.service.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @GetMapping
    public List<EventShortDto> getAll(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            HttpServletRequest httpRequest) {
        log.info("==> GET /events Public searching events with params: " +
                        "text {}, categories: {}, paid {}, rangeStart: {}, rangeEnd: {}, available {}, from: {}, size: {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size);

        EventSearchParams eventSearchParams = new EventSearchParams();
        PublicSearchParams publicSearchParams = new PublicSearchParams();
        publicSearchParams.setText(text);
        publicSearchParams.setCategories(categories);
        publicSearchParams.setPaid(paid);

        publicSearchParams.setRangeStart(rangeStart);
        publicSearchParams.setRangeEnd(rangeEnd);

        eventSearchParams.setPublicSearchParams(publicSearchParams);
        eventSearchParams.setFrom(from);
        eventSearchParams.setSize(size);

        HitDto hitDto = new HitDto(
                null, // не нужно во входящем DTO
                "ewm-service",
                httpRequest.getRequestURI(),
                httpRequest.getRemoteAddr(),
                LocalDateTime.now().format(dateTimeFormatter));

        List<EventShortDto> eventShortDtoList = eventService.getAllByInitiatorOrPublic(eventSearchParams, hitDto);
        log.info("<== GET /events Returning public searching events. List size: {}",
                eventShortDtoList.size());
        //TODO Statistic?
        return eventShortDtoList;
    }


    @GetMapping("/{id}")
    @Transactional
    public EventFullDto getById(
            @PathVariable Long id, HttpServletRequest httpRequest
    ) {
        log.info("==> GET /events/{}  Public getById", id);
        HitDto hitDto = new HitDto(
                null, // не нужно во входящем DTO
                "ewm-service",
                httpRequest.getRequestURI(),
                httpRequest.getRemoteAddr(),
                LocalDateTime.now().toString());
        EventFullDto eventFullDto = eventService.getById(new EventGetByIdParams(null, id), hitDto);
        if (eventFullDto.state() != EventState.PUBLISHED) {
            throw new NotFoundException("Нет опубликованных событий с id " + id);
        }
        log.info("<== GET /events/{}  Public getById", id);
        return eventFullDto;
    }

}
