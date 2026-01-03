package org.example.api.game.controller.dto;

import java.util.List;

import org.example.core.domain.game.common.GamePlatform;
import org.example.core.domain.game.common.PriceStatus;
import org.example.core.domain.game.common.ReleaseStatus;
import org.example.core.domain.game.concept.Game;
import org.example.core.domain.game.platform.PlatformGame;
import org.example.core.domain.game.product.ProductContentType;
import org.example.core.domain.game.product.ProductGame;

public record ProductGameItemRes(
    Long id,
    GamePlatform gamePlatform,
    ProductContentType contentType,
    String name,
    ReleaseStatus releaseStatus,
    Integer priceOriginal,
    Integer priceCurrent,
    Short discountRate,
    boolean delisted,
    PriceStatus priceStatus,
    String mainImageUrl,
    List<String> genres,
    boolean isWished
) {

    public static ProductGameItemRes toDto(ProductGame productGame, boolean isWished) {
        PlatformGame platformGame = productGame.getPlatformGame();
        Game game = platformGame.getGame();

        return new ProductGameItemRes(
            productGame.getId(),
            platformGame.getPlatform(),
            productGame.getContentType(),
            productGame.getName(),
            productGame.getReleaseStatus(),
            productGame.getPriceOriginal(),
            productGame.getPriceCurrent(),
            productGame.getDiscountRate(),
            productGame.isDelisted(),
            productGame.getPriceStatus(),
            productGame.getMainImageUrl(),
            game.getGenres().stream()
                .map(gameGenre -> gameGenre.getGenre().getDisplayName())
                .toList(),
            isWished
        );
    }
}
