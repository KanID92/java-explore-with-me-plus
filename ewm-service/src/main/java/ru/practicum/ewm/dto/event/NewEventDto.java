package ru.practicum.ewm.dto.event;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.ewm.dto.event.annotation.FutureAfterTwoHours;
import ru.practicum.ewm.entity.Location;

import java.time.LocalDateTime;

public record NewEventDto(

        @NotNull @Min(20) @Max(2000)
        String annotation,

        Long category,

        @NotNull @Min(20) @Max(7000)
        String description,

        @NotNull @FutureAfterTwoHours
        LocalDateTime eventDate,

        @NotNull
        Location location,

        boolean paid,

        @NotNull @PositiveOrZero
        Integer participantLimit,

        boolean requestModeration,

        @NotNull @Min(3) @Max(120)
        String title

) {
}
