package org.example.api.game.controller.dto;

import java.util.List;

import org.example.core.domain.game.common.GamePlatform;
import org.example.core.domain.game.common.PriceStatus;
import org.example.core.domain.game.common.ReleaseStatus;
import org.example.core.domain.game.concept.Game;
import org.example.core.domain.game.platform.PlatformGame;
import org.example.core.domain.game.product.ProductContentType;
import org.example.core.domain.game.product.ProductGame;
import org.example.core.domain.wishlist.Wishlist;

public record WishListRes(
    Long wishlistId,
    GamePlatform gamePlatform,
    short notifyDiscountRate,
    Long productGameId,
    Long platformGameId,
    ProductContentType contentType,
    String name,
    Integer priceOriginal,
    Integer priceCurrent,
    Short discountRate,
    PriceStatus priceStatus,
    String storeUrl,
    String mainImageUrl,
    ReleaseStatus releaseStatus,
    List<String> genres,
    boolean delisted
) {
    public static WishListRes toDto(Wishlist wishlist) {
        ProductGame productGame = wishlist.getProductGame();
        PlatformGame platformGame = productGame.getPlatformGame();
        Game game = platformGame.getGame();

        return new WishListRes(
            wishlist.getId(),
            platformGame.getPlatform(),
            wishlist.getNotifyDiscountRate(),
            productGame.getId(),
            productGame.getPlatformGame().getId(),
            productGame.getContentType(),
            productGame.getName(),
            productGame.getPriceOriginal(),
            productGame.getPriceCurrent(),
            productGame.getDiscountRate(),
            productGame.getPriceStatus(),
            productGame.getStoreUrl(),
            productGame.getMainImageUrl(),
            productGame.getReleaseStatus(),
            game.getGenres().stream()
                .map(gameGenre -> gameGenre.getGenre().getDisplayName())
                .toList(),
            productGame.isDelisted()
        );
    }
}
