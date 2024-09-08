package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.LocationService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/locations")
public class PrivateLocationController {

    private final LocationService locationService;

    @PutMapping("/{locationId}/likes")
    public void addLike( //Добавление лайка на локацию
            @PathVariable long userId,
            @PathVariable long locationId) {

        log.info("==> PUT. /users/{userId}/locations/{locationId}/likes" +
                "Adding like for location with id: {} by user with id: {}", locationId, userId);
        locationService.addLike(userId, locationId);
        log.info("<== PUT. /users/{userId}/events/{eventId}/likes" +
                "Like for location with id: {} by user with id: {} added.", locationId, userId);
    }

    @DeleteMapping("/{locationId}/likes")
    public void deleteLike( //удаление лайка на локацию
                            @PathVariable long userId,
                            @PathVariable long locationId
    ) {
        log.info("==> DELETE. /users/{userId}/events/{eventId}/likes" +
                "Deleting like for location with id: {} by user with id: {}", locationId, userId);
        locationService.deleteLike(userId, locationId);
        log.info("<== DELETE. /users/{userId}/events/{eventId}/likes" +
                "Like for location with id: {} by user with id: {} deleted.", locationId, userId);
    }



}



