package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.location.LocationDto;
import ru.practicum.ewm.entity.Location;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.ewm.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final LocationMapper locationMapper;

    @Override
    public LocationDto addLike(long userId, long locationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location with id " + locationId + " not found"));
        locationRepository.addLike(userId, locationId);
        return locationMapper.locationToLocationDto(locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location with id " + locationId + " not found")));
    }

    @Override
    public void deleteLike(long userId, long locationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location with id " + locationId + " not found"));
        if (locationRepository.checkLikeExisting(userId, locationId)) {
            locationRepository.deleteLike(userId, locationId);
        } else {
            throw new NotFoundException("Like for Location: " + locationId + " by user: " + user.getId() + " not exist");
        }

    }

}
