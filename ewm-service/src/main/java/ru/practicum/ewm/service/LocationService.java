package ru.practicum.ewm.service;

public interface LocationService {

    void addLike(long userId, long locationId);

    void deleteLike(long userId, long locationId);
}
