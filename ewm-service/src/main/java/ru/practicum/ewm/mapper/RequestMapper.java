package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.Request;
import ru.practicum.ewm.entity.RequestStatus;
import ru.practicum.ewm.entity.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public abstract class RequestMapper {

    @Mapping(target = "event", expression = "java(request.getEvent().getId())")
    @Mapping(target = "requester", expression = "java(request.getRequester().getId())")
    public abstract ParticipationRequestDto toParticipationRequestDto(Request request);

    @Mapping(target = "createOn", expression = "java(getCurrentLocalDatetime)")
    @Mapping(target = "status", expression = "java(getPendingEventState)")
    public abstract Request toRequest (User user, Event event);

    @Named("getCurrentLocalDatetime")
    LocalDateTime getCurrentLocalDatetime() {
        return LocalDateTime.now();
    }

    @Named("getPendingEventState")
    RequestStatus getPendingRequestState() {
        return RequestStatus.PENDING;
    }

}
