package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.controller.priv.PrivateEventGetAllParams;
import ru.practicum.ewm.controller.priv.PrivateEventUpdateParams;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public EventFullDto create(long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));


        return null;
    }

    @Override
    public List<EventShortDto> getAll(long userId, PrivateEventGetAllParams params) {
        return List.of();
    }

    @Override
    public EventFullDto getById(long userId, long eventId) {
        return null;
    }

    @Override
    public EventFullDto update(long userId, PrivateEventUpdateParams updateParams) {
        return null;
    }
}
