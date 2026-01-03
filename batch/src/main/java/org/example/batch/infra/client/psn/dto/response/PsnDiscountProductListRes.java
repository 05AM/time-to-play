package org.example.batch.infra.client.psn.dto.response;

import java.util.List;

public record PsnDiscountProductListRes(
    CategoryGrid categoryGridRetrieve
) {

    public record CategoryGrid(
        String __typename,
        String id,
        String localizedName,
        PageInfo pageInfo,
        List<Product> products
    ) { }

    public record PageInfo(
        String __typename,
        boolean isLast,
        int offset,
        int size,
        int totalCount
    ) { }

    public record Product(
        String __typename,
        String id,
        String name,
        String npTitleId,
        String storeDisplayClassification,
        String localizedStoreDisplayClassification,
        List<String> platforms,
        SkuPrice price
    ) { }

    public record SkuPrice(
        String __typename,
        String basePrice,
        String discountedPrice,
        String discountText,
        Boolean isFree,
        Boolean isTiedToSubscription
    ) { }
}