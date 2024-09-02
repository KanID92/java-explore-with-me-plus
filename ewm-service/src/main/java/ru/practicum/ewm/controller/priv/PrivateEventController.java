package ru.practicum.ewm.controller.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;

    @PostMapping
    public EventFullDto create(@PathVariable long userId, @Valid @RequestBody NewEventDto newEventDto) {
        log.info("==> POST. /users/{userId}/events " +
                "Creating new event {} by user with id: {}", newEventDto, userId);
        EventFullDto receivedEventDto = eventService.create(userId, newEventDto);
        log.info("<== POST. /users/{userId}/events " +
                "Returning new event {}: {}", receivedEventDto.id(), receivedEventDto);
        return receivedEventDto;
    }

    @GetMapping
    public List<EventShortDto> getAll(
            @PathVariable Long userId,
            @RequestParam @DefaultValue("0") int from,
            @RequestParam @DefaultValue("10") int size) {
        log.info("==> GET. /users/{userId}/events " +
                "Getting all user id {} event: from {}, size {}", userId, from, size);
        List<EventShortDto> receivedEventsDtoList =
                eventService.getAll(userId, new PrivateEventGetAllParams(from, size));
        //TODO Statistic service
        log.info("<== GET. /users/{userId}/events " +
                "Returning all user id {} event: size {}", userId, receivedEventsDtoList.size());
        return receivedEventsDtoList;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable long userId, @PathVariable long eventId) {
        log.info("==> GET. /users/{userId}/events/{eventId} " +
                "Getting event with id: {}, by user with id: {}", eventId, userId);
        EventFullDto receivedEventDto = eventService.getById(userId, eventId);
        //TODO Statistic service
        log.info("<== GET. /users/{userId}/events/{eventId} " +
                "Returning event with id: {}", receivedEventDto.id());
        return receivedEventDto;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable long userId,
                               @PathVariable long eventId,
                               @Valid @RequestBody UpdateEventUserRequest updateEventDto) {
        log.info("==> PATCH. /users/{userId}/events/{eventId} " +
                "Updating event with id: {}, by user with id: {}. Updating: {}", eventId, userId, updateEventDto);
        EventFullDto receivedEventDto = eventService.update(
                userId, new PrivateEventUpdateParams(eventId, updateEventDto));
        log.info("<== PATCH. /users/{userId}/events/{eventId} " +
                "Returning updated event with id: {}, by user with id: {}. Updating: {}",
                eventId, userId, receivedEventDto);
        return receivedEventDto;

    }


}
