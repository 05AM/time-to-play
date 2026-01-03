package org.example.batch.infra.client.psn;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PsnHttpClient {

    private final RestClient psnRestClient;

    // TODO: 재시도 로직
    public String postJson(String path, Map<String, String> headers, Object body) {
        var req = psnRestClient.post()
            .uri(path)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

        if (headers != null) {
            headers.forEach(req::header);
        }

        return req.body(body)
            .retrieve()
            .body(String.class);
    }
}