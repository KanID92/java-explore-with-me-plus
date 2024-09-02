package ru.practicum.ewm.dto.event;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.entity.EventState;
import ru.practicum.ewm.entity.Location;

public record EventFullDto(

        String annotation,

        CategoryDto category,

        Long confirmedRequests,

        String createOn,

        String description,

        String eventDate,

        Long id,

        UserShortDto initiator,

        Location location,

        boolean paid,

        Integer participantLimit,

        String publishedOn,

        boolean requestModeration,

        EventState state,

        String title,

        Long views
) {

}
