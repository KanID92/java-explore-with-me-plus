package ru.practicum.stats.mapper;

import ru.practicum.HitDto;
import ru.practicum.stats.entity.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class HitDtoMapper {
    private HitDtoMapper() {
    }

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static HitDto toHitDto(Hit hit) {
        String dateTime = hit.getTimestamp().format(formatter);

        return new HitDto(
                hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                dateTime
        );
    }

    public static Hit dtoToHit(HitDto hitDto) {

        LocalDateTime localDateTime = LocalDateTime.parse(hitDto.getTimestamp(), formatter);
        Hit hit = new Hit();
        hit.setId(hitDto.getId());
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimestamp(localDateTime);
        return hit;
    }
}

