package org.example.batch.infra.client.psn.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GraphQlReq(
    String operationName,
    Object variables,
    Extensions extensions
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Extensions(PersistedQuery persistedQuery) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PersistedQuery(int version, String sha256Hash) {}
}