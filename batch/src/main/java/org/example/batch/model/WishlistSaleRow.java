package org.example.batch.model;

import java.time.OffsetDateTime;

import org.example.core.domain.game.common.GamePlatform;

public record WishlistSaleRow(
    long memberId,
    String memberEmail,
    String memberName,
    long priceHistoryId,
    GamePlatform platform,
    String gameName,
    String mainImageUrl,
    Integer priceOriginal,
    Integer priceCurrent,
    Short discountRate,
    OffsetDateTime priceChangedAt
) {
}
