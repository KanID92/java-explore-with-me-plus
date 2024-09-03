package ru.practicum.ewm.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.entity.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    Event newEventDtoToEvent(NewEventDto newEventDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateEventUserRequestToEvent(@MappingTarget Event event, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto eventToEventFullDto(Event event);

    EventShortDto eventToEventShortDto(Event event);


}
