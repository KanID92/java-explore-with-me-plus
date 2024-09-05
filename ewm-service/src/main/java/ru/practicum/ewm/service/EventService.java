package ru.practicum.ewm.service;

import ru.practicum.HitDto;
import ru.practicum.ewm.controller.params.EventGetByIdParams;
import ru.practicum.ewm.controller.params.EventUpdateParams;
import ru.practicum.ewm.controller.params.search.EventSearchParams;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;

import java.util.List;

public interface EventService {

    EventFullDto create(long userId, NewEventDto newEventDto);

    EventFullDto getById(EventGetByIdParams params, HitDto hitDto);

    EventFullDto update(long eventId, EventUpdateParams updateParams);

    List<EventShortDto> getAllByInitiatorOrPublic(EventSearchParams searchParams, HitDto hitDto);

    List<EventFullDto> getAllByAdmin(EventSearchParams searchParams);
}
