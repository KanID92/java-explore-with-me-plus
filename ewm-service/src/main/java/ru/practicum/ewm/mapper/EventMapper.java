package ru.practicum.ewm.mapper;

import jakarta.validation.ValidationException;
import org.mapstruct.*;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.entity.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "createIn", expression = "java(getLocalDatetime())")
    @Mapping(target = "state", expression = "java(getPendingEventState())")
    Event newEventDtoToEvent(
            NewEventDto newEventDto, User initiator, Category category, Location location);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateEventUserRequestToEvent(@MappingTarget Event event, UpdateEventUserRequest updateEventUserRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "state", expression = "java(getAdminEventState(UpdateEventAdminRequest updateEventAdminRequest))")
    void updateEventAdminRequestToEvent(@MappingTarget Event event, UpdateEventAdminRequest updateEventAdminRequest);

    EventFullDto eventToEventFullDto(Event event);

    EventShortDto eventToEventShortDto(Event event);


    private LocalDateTime getLocalDatetime() {
        return LocalDateTime.now();
    }

    private EventState getPendingEventState() {
        return EventState.PENDING;
    }

    private EventState getAdminEventState(UpdateEventAdminRequest updateEventAdminRequest) {
        switch (updateEventAdminRequest.stateAction()) {
            case StateAction.PUBLISH_EVENT -> {
                return EventState.PUBLISHED;
            }
            case StateAction.REJECT_EVENT -> {
                return EventState.CANCELED;
            }
            default -> throw new ValidationException("EventMapper: Invalid state action");
        }
    }

}
