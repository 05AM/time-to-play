package org.example.batch.infra.client.psn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.batch.infra.client.psn.dto.request.GraphQlReq;
import org.example.batch.infra.client.psn.exception.PsnClientException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PsnGraphQlClient {

    private final PsnHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final PsnProperties props;

    public JsonNode execute(String opKey, Object variables) {
        var op = props.operations().get(opKey);
        if (op == null) {
            throw new IllegalArgumentException("Unknown PSN opKey: " + opKey);
        }

        var req = new GraphQlReq(
            op.operationName(),
            variables,
            new GraphQlReq.Extensions(
                new GraphQlReq.PersistedQuery(1, op.sha256())
            )
        );

        try {
            String json = httpClient.postJson(props.graphqlPath(), props.headers(), req);
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new PsnClientException("PSN GraphQL call failed. opKey=" + opKey, e);
        }
    }
}