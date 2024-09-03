package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.StatClientImpl;
import ru.practicum.ewm.controller.params.EventUpdateParams;
import ru.practicum.ewm.controller.priv.PrivateEventGetAllParams;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.entity.*;
import ru.practicum.ewm.exception.AccessException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;

    private final StatClientImpl statClient;

    @Override
    public EventFullDto create(long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Category category = categoryRepository.findById(newEventDto.category())
                .orElseThrow(() -> new NotFoundException("Category with id " + newEventDto.category() + " not found"));
        Location location = locationRepository.save(newEventDto.location());
        Event event = eventMapper.newEventDtoToEvent(newEventDto, initiator, category, location);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.eventToEventFullDto(savedEvent);
    }

    @Override
    public List<EventShortDto> getAll(long initiatorId, PrivateEventGetAllParams params) {
        User user = userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException("User with id " + initiatorId + " not found"));
//        TODO Statistic?

        Pageable page = PageRequest.of(params.from(), params.size());

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
        //statClient.saveHit();
        //TODO Statistic? Reading?
        return eventMapper.eventToEventFullDto(receivedEvent);
    }
    @Override
    public EventFullDto update(long eventId, EventUpdateParams updateParams) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        Event updatedEvent;

        if (updateParams.updateEventUserRequest() != null) {
            User user = userRepository.findById(updateParams.userId())
                .orElseThrow(() -> new NotFoundException("User with id " + updateParams.userId()+ " not found"));

            if (updateParams.userId() != event.getInitiator().getId()) {
                throw new AccessException("User with id = " + updateParams.userId() + " do not initiate this event");
            }

            if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
                throw new ConflictException(
                        "User. Cannot update event: only pending or canceled events can be changed");
            }

            if (updateParams.updateEventUserRequest().eventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException(
                        "User. Cannot update event: event date must be not earlier then after 2 hours ");
            }

            eventMapper.updateEventUserRequestToEvent(event, updateParams.updateEventUserRequest());
        }

        if (updateParams.updateEventAdminRequest() != null) {
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("Admin. Cannot update event: only pending events can be changed");
            }

            if (updateParams.updateEventAdminRequest().eventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ConflictException(
                        "Admin. Cannot update event: event date must be not earlier then after 2 hours ");
            }

            eventMapper.updateEventAdminRequestToEvent(event, updateParams.updateEventAdminRequest());
        }

        updatedEvent = eventRepository.save(event);

        return eventMapper.eventToEventFullDto(updatedEvent);
    }

}
