package org.example.batch.infra.client.psn.dto.response;

import java.util.List;
import java.util.Map;

public record GraphQlError(
    String message,
    List<String> path,
    Map<String, Object> extensions
) {}