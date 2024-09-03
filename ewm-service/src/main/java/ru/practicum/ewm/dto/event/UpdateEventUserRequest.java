package ru.practicum.ewm.dto.event;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.ewm.dto.event.annotation.FutureAfterTwoHours;
import ru.practicum.ewm.entity.EventState;
import ru.practicum.ewm.entity.Location;

import java.time.LocalDateTime;

public record UpdateEventUserRequest(

        @Min(20) @Max(2000)
        String annotation,

        Integer category,

        @Min(20) @Max(7000)
        String description,

        @FutureAfterTwoHours
        LocalDateTime eventDate,

        Location location,

        Boolean paid,

        @PositiveOrZero
        Integer participantLimit,

        String publishedOn,

        Boolean requestModeration,

        EventState stateAction,

        @Min(3) @Max(120)
        String title

) {
}
