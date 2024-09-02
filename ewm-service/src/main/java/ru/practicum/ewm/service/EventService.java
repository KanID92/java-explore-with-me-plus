package ru.practicum.ewm.service;

import ru.practicum.ewm.controller.priv.PrivateEventGetAllParams;
import ru.practicum.ewm.controller.priv.PrivateEventUpdateParams;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;

import java.util.List;

public interface EventService {

    EventFullDto create(long userId, NewEventDto newEventDto);

    List<EventShortDto> getAll(long userId, PrivateEventGetAllParams params);

    EventFullDto getById(long userId, long eventId);

    EventFullDto update(long userId, PrivateEventUpdateParams updateParams);

}
