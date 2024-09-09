package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.location.LocationDto;

public interface LocationService {

    LocationDto addLike(long userId, long locationId);

    void deleteLike(long userId, long locationId);
}
