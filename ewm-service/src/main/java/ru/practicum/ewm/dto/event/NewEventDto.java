package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ru.practicum.ewm.dto.event.annotation.FutureAfterTwoHours;
import ru.practicum.ewm.entity.Location;

import java.time.LocalDateTime;

public record NewEventDto(

        @NotBlank @Size(min = 20, max = 2000)
        String annotation,

        Long category,

        @NotBlank @Size(min = 20, max = 7000)
        String description,

        @NotNull @FutureAfterTwoHours @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,

        @NotNull
        Location location,

        boolean paid,

        @NotNull @PositiveOrZero
        Integer participantLimit,

        boolean requestModeration,

        @NotNull @Size(min = 3, max = 120)
        String title

) {
}
