package org.example.batch.infra.client.psn.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConceptDetailVariables(
    String conceptId
) {}
