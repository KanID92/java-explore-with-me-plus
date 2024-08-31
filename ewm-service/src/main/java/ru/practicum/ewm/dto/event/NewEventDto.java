package ru.practicum.ewm.dto.event;

import jakarta.validation.constraints.*;
import ru.practicum.ewm.entity.Location;

import java.time.LocalDateTime;

public record NewEventDto(

        @NotNull @Min(20) @Max(2000)
        String annotation,

        Long category,

        @NotNull @Min(20) @Max(7000)
        String description,

        @NotNull @Future
        LocalDateTime eventDate, //TODO Validation, DateTimePattern, customAnnotation

        @NotNull
        Location location, //TODO validation?

        boolean paid,

        @NotNull @PositiveOrZero
        Integer participantLimit,

        boolean requestModeration,

        @NotNull @Min(3) @Max(120) String title

) {
}
