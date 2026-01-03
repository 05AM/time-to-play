package org.example.api.game.controller.dto;

public record AddWishlistReq(
    Long productId,
    Short discountRate
) {
}
