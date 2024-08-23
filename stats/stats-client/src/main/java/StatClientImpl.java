import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class StatClientImpl implements StatClient {

    private static final  String BASE_URL = "http//localhost:9090";
    private static final String STATS_PATH = "/stats";
    private static final String HIT_PATH = "/hit";

    RestClient restClient;

    public StatClientImpl() {
        restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    public Object getStats(final Object hitStatsDto) {
        return restClient
                .post()
                .uri(HIT_PATH)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(hitStatsDto)
                .retrieve()
                .body(Object.class);
    }

    public List<Object> createEndpointHit(final String startTime,
                                               final String endTime,
                                               final List<String> uris,
                                               final Boolean unique) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(STATS_PATH)
                        .queryParam("start", startTime)
                        .queryParam("end", endTime)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

    }

}
