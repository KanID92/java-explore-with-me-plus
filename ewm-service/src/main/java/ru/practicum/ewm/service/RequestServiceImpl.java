package ru.practicum.ewm.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.controller.priv.PrivateUpdateRequestParams;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.entity.*;
import ru.practicum.ewm.exception.AccessException;
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

    private static final Logger log = LoggerFactory.getLogger(RequestServiceImpl.class);
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

        Long confirmedRequests = requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, eventId);

        if (userId == event.getInitiator().getId()) {
            throw new ConflictException("Initiator can't request in own event");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event is not published");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() == confirmedRequests) {
            throw new ConflictException("There is no empty place for this event");
        }

        Request creatingRequest = requestMapper.toRequest(user, event);

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
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
        request.setStatus(RequestStatus.CANCELED);
        Request canceledRequest = requestRepository.save(request);

        return requestMapper.toParticipationRequestDto(canceledRequest);
    }

    @Override
    public List<ParticipationRequestDto> getAllForOwnEvent(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        if (event.getInitiator().getId() != user.getId()) {
            throw new AccessException("User with id " + userId + " is not own event");
        }

        List<Request> receivedEventsList = requestRepository.getAllByEventId(eventId);

        return receivedEventsList.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatus(PrivateUpdateRequestParams params) {
        User user = userRepository.findById(params.userId())
                .orElseThrow(() -> new NotFoundException("User with id " + params.userId() + " not found"));
        Event event = eventRepository.findById(params.eventId())
                .orElseThrow(() -> new NotFoundException("Event with id " + params.eventId() + " not found"));


        if (event.getInitiator().getId() != user.getId()) {
            throw new AccessException("User with id " + params.userId() + " is not own event");
        }

        long confirmedRequestsCount =
                requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, params.eventId());

        List<Request> requestListOfEvent =
                requestRepository.getAllByIdInAndEventId(
                        params.eventRequestStatusUpdateRequest().requestIds(), params.eventId());


        for (Request request : requestListOfEvent) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Request status is not PENDING");
            }

            if (confirmedRequestsCount >= event.getParticipantLimit()) {
                throw new ConflictException("Participant limit exceeded");
            }

            if (event.isRequestModeration()) {
                requestRepository.updateStatus(
                        params.eventRequestStatusUpdateRequest().status().toString(), request.getId());
                if (params.eventRequestStatusUpdateRequest().status() == RequestStatus.CONFIRMED) {
                    confirmedRequestsCount++;
                }
                if (confirmedRequestsCount >= event.getParticipantLimit()) {
                    requestRepository.cancelNewRequestsStatus(event.getId());
                }
            }
        }

        List<ParticipationRequestDto> confirmedRequestsDtoList =
                requestRepository.findAllByStatus(RequestStatus.CONFIRMED)
                        .stream()
                        .map(requestMapper::toParticipationRequestDto)
                        .toList();
        List<Request> rejectedRequests = requestRepository.findAllByStatus(RequestStatus.REJECTED);
        for (Request request : rejectedRequests) {
            System.out.println(request.getId() + "id, status: " + request.getStatus());
        }

        List<ParticipationRequestDto> rejectedRequestsDtoList =
                rejectedRequests
                        .stream()
                        .map(requestMapper::toParticipationRequestDto)
                        .toList();

        return new EventRequestStatusUpdateResult(confirmedRequestsDtoList, rejectedRequestsDtoList);

    }


}
