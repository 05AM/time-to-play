package org.example.batch.infra.client.psn.dto.response;

import java.util.List;

public record GraphQlRes<T>(
    T data,
    String message,
    List<GraphQlError> errors
) {
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
}
