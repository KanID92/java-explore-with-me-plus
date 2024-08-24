package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.HitStatDto;
import ru.practicum.stats.service.StatsService;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public HitDto saveHit(@RequestBody HitDto hitDto) {

        return statsService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<HitStatDto> getHits(@RequestParam String start,
                             @RequestParam String end,
                             @RequestParam(required = false) List<String> uris,
                             @RequestParam (required = false, defaultValue = "false") Boolean unique) {
        return statsService.getHits(start, end, uris, unique);
    }
}
