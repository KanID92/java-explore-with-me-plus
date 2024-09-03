package ru.practicum.ewm.controller.priv;

import ru.practicum.ewm.dto.event.UpdateEventUserRequest;

public record PrivateEventUpdateParams(
        long eventId,
        UpdateEventUserRequest updateEventUserRequest
) {
}
