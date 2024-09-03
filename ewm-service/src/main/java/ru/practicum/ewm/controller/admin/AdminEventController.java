package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.controller.params.EventUpdateParams;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @PatchMapping("{eventId}")
    public EventFullDto update(
            @PathVariable long eventId,
            @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("==> PATCH /admin/events/{}; Update event by admin: {}" , eventId, updateEventAdminRequest);
        EventFullDto updatedEvent = eventService.update(
                eventId, new EventUpdateParams(null, null, updateEventAdminRequest));
        log.info("<== PATCH /admin/events/{}; Updated event by admin: {}", eventId, updatedEvent);
        return updatedEvent;
    }

    @GetMapping
    public List<EventFullDto> getAll(
            @RequestParam(required = false) Long[] users,
            @RequestParam(required = false) String[] states,
            @RequestParam(required = false) Long[] categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return null; //TODO
    }

}
