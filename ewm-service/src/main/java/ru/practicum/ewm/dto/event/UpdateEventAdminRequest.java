package ru.practicum.ewm.dto.event;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.ewm.entity.Location;
import ru.practicum.ewm.entity.StateAction;

import java.time.LocalDateTime;

public record UpdateEventAdminRequest(

        @Min(20) @Max(2000)
        String annotation,

        Integer category,

        @Min(20) @Max(7000)
        String description,

        LocalDateTime eventDate, //TODO Validation, DateTimePattern, customAnnotation?

        Location location, //TODO Validation, customAnnotation?

        Boolean paid,

        @PositiveOrZero
        Integer participantLimit,

        Boolean requestModeration,

        StateAction stateAction,

        @Min(3) @Max(120)
        String title

) {
}
