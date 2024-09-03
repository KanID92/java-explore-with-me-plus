package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.controller.priv.PrivateEventGetAllParams;
import ru.practicum.ewm.controller.priv.PrivateEventUpdateParams;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.EventState;
import ru.practicum.ewm.entity.Location;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.AccessException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final LocationRepository locationRepository;

    @Override
    public EventFullDto create(long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        //TODO Category category = categoryRepository.
        Location location = locationRepository.save(newEventDto.location());
        Event event = eventMapper.newEventDtoToEvent(newEventDto, initiator, category, location);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.eventToEventFullDto(savedEvent);
    }

    @Override
    public List<EventShortDto> getAll(long initiatorId, PrivateEventGetAllParams params) {
        User user = userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException("User with id " + initiatorId + " not found"));

        Pageable page = PageRequest.of(params.from(), params.size());
        //TODO
        //TODO Statistic?
        return  eventRepository.findAllByInitiatorId(initiatorId, page).stream()
                .map(eventMapper::eventToEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto getById(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Event receivedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));
        //TODO Statistic?
        return eventMapper.eventToEventFullDto(receivedEvent);
    }

    @Override
    public EventFullDto update(long userId, PrivateEventUpdateParams updateParams) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Event event = eventRepository.findById(updateParams.eventId())
                .orElseThrow(() -> new NotFoundException("Event with id " + updateParams.eventId() + " not found"));

        if (userId != event.getInitiator().getId()) {
            throw new AccessException("User with id = " + userId + " do not initiate this event");
        }

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConflictException("Cannot update event: only pending or canceled events can be changed");
        }

        if (updateParams.updateEventUserRequest().eventDate().isBefore(LocalDateTime.now().plusHours(2))) { //TODO ? check in Controller?
            throw new ConflictException("Cannot update event: event date must be not earlier then after 2 hours ");
        }

        eventMapper.updateEventUserRequestToEvent(event, updateParams.updateEventUserRequest());

        Event updatedEvent = eventRepository.save(event);

        return eventMapper.eventToEventFullDto(updatedEvent);

    }
}
