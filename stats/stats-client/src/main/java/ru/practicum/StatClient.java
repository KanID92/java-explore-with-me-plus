package ru.practicum;

import java.util.List;

public interface StatClient {

    HitDto saveHit(HitDto hitDto);

    List<HitStatDto> getStats(final String startTime,
                              final String endTime,
                              final List<String> uris,
                              final Boolean unique);
}
