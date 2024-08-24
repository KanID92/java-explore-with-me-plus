package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.HitDto;
import ru.practicum.HitStatDto;
import ru.practicum.stats.entity.Hit;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.stats.mapper.HitDtoMapper.dtoToHit;
import static ru.practicum.stats.mapper.HitDtoMapper.toHitDto;

@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public HitDto saveHit(HitDto hitDto) {
        return toHitDto(statsRepository.save(dtoToHit(hitDto)));
    }

    @Override
    public List<HitStatDto> getHits(String start, String end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTimeStart = LocalDateTime.parse(start, formatter);
        LocalDateTime localDateTimeEnd = LocalDateTime.parse(end, formatter);
        List<Hit> data;
        List<HitStatDto> result = new ArrayList<>();
        data = (uris == null || uris.isEmpty()) ? statsRepository.getStat(localDateTimeStart, localDateTimeEnd) :
                statsRepository.getStatByUris(localDateTimeStart, localDateTimeEnd, uris);

        Map<String, Map<String, List<Hit>>> mapByAppAndUri = data.stream()
                .collect(Collectors.groupingBy(Hit::getApp,
                        Collectors.groupingBy(Hit::getUri)));
        mapByAppAndUri.forEach((appKey, mapUriValue) -> mapUriValue.forEach((uriKey, hitsValue) -> {
            HitStatDto hitStat = new HitStatDto();
            hitStat.setApp(appKey);
            hitStat.setUri(uriKey);
            List<String> ips = hitsValue.stream().map(Hit::getIp).toList();
            Integer hits = unique ? ips.stream().distinct().toList().size() : ips.size();
            hitStat.setHits(hits);
            result.add(hitStat);
        }));
        return result.stream().sorted(Comparator.comparingInt(HitStatDto::getHits).reversed()).toList();
    }
}
