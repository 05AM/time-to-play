package org.example.batch.infra.client.psn;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.psn")
public record PsnProperties(
    String baseUrl,
    String graphqlPath,
    String locale,
    Map<String, String> headers,
    Map<String, Operation> operations
) {
    public record Operation(String operationName, String sha256) {
    }
}
