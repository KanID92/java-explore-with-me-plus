package ru.practicum.ewm.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HitDto;
import ru.practicum.HitStatDto;
import ru.practicum.client.StatClient;
import ru.practicum.ewm.controller.params.EventGetByIdParams;
import ru.practicum.ewm.controller.params.EventUpdateParams;
import ru.practicum.ewm.controller.params.search.EventSearchParams;
import ru.practicum.ewm.controller.params.search.PublicSearchParams;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.entity.*;
import ru.practicum.ewm.exception.AccessException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.IncorrectValueException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.ewm.entity.QEvent.event;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    private final StatClient statClient;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EventFullDto create(long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Category category = categoryRepository.findById(newEventDto.category())
                .orElseThrow(() -> new NotFoundException("Category with id " + newEventDto.category() + " not found"));
        Location location = locationRepository.save(newEventDto.location());
        Event event = eventMapper.newEventDtoToEvent(newEventDto, initiator, category, location, LocalDateTime.now());
        Event savedEvent = eventRepository.save(event);
        return eventMapper.eventToEventFullDto(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByInitiatorOrPublic(EventSearchParams searchParams, HitDto hitDto) {

        List<Event> eventListBySearch = new ArrayList<>();

        if (searchParams.getPrivateSearchParams() != null) { //private
            long initiatorId = searchParams.getPrivateSearchParams().getInitiatorId();
            User user = userRepository.findById(initiatorId)
                    .orElseThrow(() -> new NotFoundException("User with id " + initiatorId + " not found"));
            Pageable page = PageRequest.of(searchParams.getFrom(), searchParams.getSize());
            eventListBySearch = eventRepository.findAllByInitiatorId(initiatorId, page);
        }

        if (searchParams.getPublicSearchParams() != null) { //public
            Pageable page = PageRequest.of(searchParams.getFrom(), searchParams.getSize());

            BooleanExpression booleanExpression = event.isNotNull();

            PublicSearchParams publicSearchParams = searchParams.getPublicSearchParams();

            if (!publicSearchParams.getText().isBlank()) { //наличие поиска по тексту
                booleanExpression = booleanExpression.andAnyOf(
                        event.annotation.likeIgnoreCase(publicSearchParams.getText()),
                        event.description.likeIgnoreCase(publicSearchParams.getText())
                );
            }

            if (publicSearchParams.getCategories() != null) { // наличие поиска по категориям
                booleanExpression = booleanExpression.and(
                        event.paid.eq(publicSearchParams.getPaid()));
            }

            LocalDateTime rangeStart = publicSearchParams.getRangeStart();
            LocalDateTime rangeEnd = publicSearchParams.getRangeEnd();

            if (rangeStart != null && rangeEnd != null) { // наличие поиска дате события
                booleanExpression = booleanExpression.and(
                        event.eventDate.between(rangeStart, rangeEnd)
                );
            } else if (rangeStart != null) {
                booleanExpression = booleanExpression.and(
                        event.eventDate.after(rangeStart)
                );
                rangeEnd = rangeStart.plusYears(100);
            } else if (publicSearchParams.getRangeEnd() != null) {
                booleanExpression = booleanExpression.and(
                        event.eventDate.before(rangeEnd)
                );
                rangeStart = LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter), dateTimeFormatter);
            }

            if (rangeEnd == null && rangeStart == null) {
                booleanExpression = booleanExpression.and(
                        event.eventDate.after(LocalDateTime.now())
                );
                rangeStart = LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter), dateTimeFormatter);
                rangeEnd = rangeStart.plusYears(100);
            }

//            if (!publicSearchParams.getOnlyAvailable()) { //TODO after Requests
//                requestRepository.countByStatusAndEventId(eve)
//                booleanExpression = booleanExpression.and(reques.loe(event.participantLimit));
//            }

            eventListBySearch = eventRepository.findAll(booleanExpression, page).stream().toList();

            statClient.saveHit(hitDto);


            for (Event event : eventListBySearch) {
                List<HitStatDto> hitStatDtoList = statClient.getStats(
                        rangeStart.format(dateTimeFormatter),
                        rangeEnd.format(dateTimeFormatter),
                        List.of("/event/" + event.getId()),
                        false);
                Long view = 0L;
                for (HitStatDto hitStatDto : hitStatDtoList) {
                    view += hitStatDto.getHits();
                }
                event.setViews(view);
                event.setConfirmedRequests(
                        requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, event.getId()));
            }
        }

        return eventListBySearch.stream()
                .map(eventMapper::eventToEventShortDto)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllByAdmin(EventSearchParams searchParams) {
        Pageable page = PageRequest.of(
                searchParams.getFrom(), searchParams.getSize());

        BooleanExpression booleanExpression = event.isNotNull();

        if (searchParams.getAdminSearchParams().getUsers() != null) {
            booleanExpression = booleanExpression.and(
                    event.initiator.id.in(searchParams.getAdminSearchParams().getUsers()));
        }

        if (searchParams.getAdminSearchParams().getCategories() != null) {
            booleanExpression = booleanExpression.and(
                    event.category.id.in(searchParams.getAdminSearchParams().getCategories()));
        }

        if (searchParams.getAdminSearchParams().getStates() != null) {
            booleanExpression = booleanExpression.and(
                    event.state.in(searchParams.getAdminSearchParams().getStates()));
        }

        LocalDateTime rangeStart = searchParams.getAdminSearchParams().getRangeStart();
        LocalDateTime rangeEnd = searchParams.getAdminSearchParams().getRangeEnd();

        if (rangeStart != null && rangeEnd != null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.between(rangeStart, rangeEnd));
        } else if (rangeStart != null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.after(rangeStart));
        } else if (rangeEnd != null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.before(rangeEnd));
        }



        return eventRepository.findAll(booleanExpression, page)
                .stream()
                .peek(event -> event.setConfirmedRequests(
                        requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, event.getId())))
                .map(eventMapper::eventToEventFullDto)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public EventFullDto getById(EventGetByIdParams params, HitDto hitDto) {
        Event receivedEvent;
        if (params.initiatorId() != null) {
            User user = userRepository.findById(params.initiatorId())
                    .orElseThrow(() -> new NotFoundException("User with id " + params.initiatorId() + " not found"));
            receivedEvent = eventRepository.findByInitiatorIdAndId(params.initiatorId(), params.eventId())
                    .orElseThrow(() -> new NotFoundException(
                            "Event with id " + params.eventId() +
                                    " created by user with id " + params.initiatorId() + " not found"));
        } else {
            receivedEvent = eventRepository.findById(params.eventId())
                    .orElseThrow(() -> new NotFoundException("Event with id " + params.eventId() + " not found"));
            statClient.saveHit(hitDto);

            List<HitStatDto> hitStatDtoList = statClient.getStats(
                        null, null, List.of("/event/" + params.eventId()), false
            );
            Long view = 0L;
            for (HitStatDto hitStatDto : hitStatDtoList) {
                view += hitStatDto.getHits();
            }
            receivedEvent.setViews(view);
            receivedEvent.setConfirmedRequests(
                    requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, receivedEvent.getId()));
        }
        return eventMapper.eventToEventFullDto(receivedEvent);
    }

    @Override
    public EventFullDto update(long eventId, EventUpdateParams updateParams) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        Event updatedEvent;

        if (updateParams.updateEventUserRequest() != null) { // private section
            User user = userRepository.findById(updateParams.userId())
                .orElseThrow(() -> new NotFoundException("User with id " + updateParams.userId() + " not found"));

            if (updateParams.updateEventUserRequest().category() != null) {
                Category category = categoryRepository.findById(updateParams.updateEventUserRequest().category())
                        .orElseThrow(() -> new NotFoundException(
                                "Category with id " + updateParams.updateEventUserRequest().category() + " not found"));
                event.setCategory(category);
            }
            if (!updateParams.userId().equals(event.getInitiator().getId())) {
                throw new AccessException("User with id = " + updateParams.userId() + " do not initiate this event");
            }

            if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
                throw new ConflictException(
                        "User. Cannot update event: only pending or canceled events can be changed");
            }

            LocalDateTime eventDate = updateParams.updateEventUserRequest().eventDate();

            if (eventDate != null &&
                    eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException(
                        "User. Cannot update event: event date must be not earlier then after 2 hours ");
            }

            StateAction stateAction = updateParams.updateEventUserRequest().stateAction();

            if (stateAction != null) {
                switch (stateAction) {
                    case CANCEL_REVIEW -> event.setState(EventState.CANCELED);

                    case SEND_TO_REVIEW -> {
                        event.setState(EventState.PENDING);
                    }
                }
            }

            eventMapper.updateEventUserRequestToEvent(event, updateParams.updateEventUserRequest());

        }

        if (updateParams.updateEventAdminRequest() != null) { // admin section

            if (updateParams.updateEventAdminRequest().category() != null) {
                Category category  = categoryRepository.findById(updateParams.updateEventAdminRequest().category())
                        .orElseThrow(() -> new NotFoundException(
                                "Category with id " + updateParams.updateEventAdminRequest().category() + " not found"));
                event.setCategory(category);
            }

            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("Admin. Cannot update event: only pending events can be changed");
            }

            if (updateParams.updateEventAdminRequest().eventDate() != null &&
                    updateParams.updateEventAdminRequest().eventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new IncorrectValueException(
                        "Admin. Cannot update event: event date must be not earlier then after 2 hours ");
            }

            eventMapper.updateEventAdminRequestToEvent(event, updateParams.updateEventAdminRequest());

        }
        event.setId(eventId);

        updatedEvent = eventRepository.save(event);

        return eventMapper.eventToEventFullDto(updatedEvent);
    }

}
