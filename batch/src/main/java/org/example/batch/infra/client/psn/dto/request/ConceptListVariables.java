package org.example.batch.infra.client.psn.dto.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConceptListVariables(
    String id,
    PageArgs pageArgs,
    SortBy sortBy,
    List<Object> filterBy,
    List<String> facetOptions,
    Integer maxResults
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PageArgs(int size, int offset) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SortBy(String name, boolean isAscending) {}
}
