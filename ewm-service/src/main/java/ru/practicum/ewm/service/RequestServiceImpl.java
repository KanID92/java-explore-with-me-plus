package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.entity.*;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public ParticipationRequestDto create(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));
        if (userId == event.getInitiator().getId()) {
            throw new ConflictException("Initiator can't request in own event");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event is not published");
        }
        if (event.getParticipantLimit() == event.getConfirmedRequests()) {
            throw new ConflictException("There is no empty place for this event");
        }

        Request creatingRequest = requestMapper.toRequest(user, event);

        if (!event.isRequestModeration()) {
            creatingRequest.setStatus(RequestStatus.CONFIRMED);
        }

        Request receivedRequest = requestRepository.save(creatingRequest);

        return requestMapper.toParticipationRequestDto(receivedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getAllOwnRequests(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        List<Request> receivedRequests = requestRepository.getAllByRequesterId(userId);

        return receivedRequests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto cancel(long userId, long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found"));
        request.setStatus(RequestStatus.CANCELLED);
        Request cancelledRequest = requestRepository.save(request);

        return requestMapper.toParticipationRequestDto(cancelledRequest);
    }
}
