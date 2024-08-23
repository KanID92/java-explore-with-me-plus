import java.util.List;

public interface StatClient {

    Object getStats(Object hitStatsDto);

    List<Object> createEndpointHit(final String startTime,
                                          final String endTime,
                                          final List<String> uris,
                                          final Boolean unique);
}
