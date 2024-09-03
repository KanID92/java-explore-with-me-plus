package ru.practicum.ewm.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.Location;
import ru.practicum.ewm.entity.User;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    Event newEventDtoToEvent(
            NewEventDto newEventDto, User initiator, Category category, Location location);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateEventUserRequestToEvent(@MappingTarget Event event, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto eventToEventFullDto(Event event);

    EventShortDto eventToEventShortDto(Event event);


}
