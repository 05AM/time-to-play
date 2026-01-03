package org.example.batch.infra.client.psn.dto.response;

import java.util.List;

public record PsnConceptGameListRes(
    CategoryGrid categoryGridRetrieve
) {
    public record CategoryGrid(
        List<Concept> concepts
    ) {}

    public record Concept(
        String id,
        String name,
        SkuPrice price,
        List<Media> media,
        List<Product> products
    ) {}

    public record Media(
        String role,
        String type,
        String url
    ) {}

    public record SkuPrice(
        String basePrice,
        String discountedPrice,
        String discountText,
        boolean isFree
    ) {}

    public record Product(
        String id
    ) {}
}
