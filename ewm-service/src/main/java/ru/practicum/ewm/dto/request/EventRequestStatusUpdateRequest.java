package ru.practicum.ewm.dto.request;

import ru.practicum.ewm.entity.RequestStatus;

import java.util.List;

public record EventRequestStatusUpdateRequest(

        List<Long> requestIds,

        RequestStatus requestStatus

) {
}
