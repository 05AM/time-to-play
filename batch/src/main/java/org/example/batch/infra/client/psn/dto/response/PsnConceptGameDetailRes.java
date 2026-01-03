package org.example.batch.infra.client.psn.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PsnConceptGameDetailRes(
    @JsonProperty("conceptRetrieve") Concept conceptRetrieve
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Concept(
        String id,
        String name,
        String invariantName,
        List<Media> media,
        List<Product> products
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Product(
        String id,
        String name,
        String invariantName,
        String npTitleId,
        String topCategory,
        List<String> platforms,
        List<Media> media,
        List<LocalizedGenre> localizedGenres,
        List<Sku> skus,
        List<GameCta> webctas,
        ProductEdition edition
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Media(
        String role,
        String type,
        String url
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LocalizedGenre(String value) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sku(String id, String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ProductEdition(
        String name,
        String type,
        Integer ordering,
        List<String> features
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GameCta(
        String type,
        Action action,
        Price price
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Action(
        String type,
        List<ActionParam> param
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ActionParam(
        String name,
        String value
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Price(
        String applicability,
        String currencyCode,
        Integer basePriceValue,
        Integer discountedValue,
        String discountText,
        String endTime,
        Boolean isFree
    ) {}
}
