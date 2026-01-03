package org.example.batch.infra.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;

import org.example.batch.infra.client.psn.dto.response.GraphQlRes;
import org.example.batch.infra.client.psn.exception.PsnClientException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PsnGraphQlResponseMapper {

    private final ObjectMapper objectMapper;

    public <T> GraphQlRes<T> toResponse(JsonNode root, Class<T> dataType) {
        try {
            JavaType type = objectMapper.getTypeFactory()
                .constructParametricType(GraphQlRes.class, dataType);

            return objectMapper.treeToValue(root, type);
        } catch (Exception e) {
            throw new PsnClientException("GraphQL response mapping failed. dataType=" + dataType.getSimpleName(), e);
        }
    }
}
