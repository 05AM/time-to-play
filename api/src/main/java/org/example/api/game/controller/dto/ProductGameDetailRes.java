package org.example.api.game.controller.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.example.core.domain.game.common.PriceStatus;
import org.example.core.domain.game.common.ReleaseStatus;
import org.example.core.domain.game.concept.Game;
import org.example.core.domain.game.platform.PlatformGame;
import org.example.core.domain.game.platform.PlatformIDType;
import org.example.core.domain.game.product.ProductContentType;
import org.example.core.domain.game.product.ProductGame;

public record ProductGameDetailRes(
    Long id,
    Long platformGameId,
    PlatformIDType platformIdType,
    String platformId,
    ProductContentType contentType,
    String name,
    String invariantName,
    List<String> features,
    String developer,
    LocalDateTime releaseDate,
    ReleaseStatus releaseStatus,
    Integer priceOriginal,
    Integer priceCurrent,
    Short discountRate,
    PriceStatus priceStatus,
    String storeUrl,
    String mainImageUrl,
    LocalDateTime lastSeenAt,
    LocalDateTime lastPriceUpdatedAt,
    LocalDateTime lastPriceChangedAt,
    List<String> supportPlatformDevices,
    List<GameMediaRes> medias,
    List<String> genres,
    boolean isWished,
    boolean delisted
) {

    public static ProductGameDetailRes toDto(ProductGame productGame, boolean isWished) {
        PlatformGame platformGame = productGame.getPlatformGame();
        Game game = platformGame.getGame();

        return new ProductGameDetailRes(
            productGame.getId(),
            productGame.getPlatformGame().getId(),
            productGame.getPlatformIdType(),
            productGame.getPlatformId(),
            productGame.getContentType(),
            productGame.getName(),
            productGame.getInvariantName(),
            productGame.getFeatures(),
            game.getDeveloper(),
            platformGame.getReleaseAt(),
            productGame.getReleaseStatus(),
            productGame.getPriceOriginal(),
            productGame.getPriceCurrent(),
            productGame.getDiscountRate(),
            productGame.getPriceStatus(),
            productGame.getStoreUrl(),
            productGame.getMainImageUrl(),
            productGame.getLastSeenAt(),
            productGame.getLastPriceUpdatedAt(),
            productGame.getLastPriceChangedAt(),
            productGame.getDevices().stream()
                .map(d -> d.getDevice().name())
                .toList(),
            productGame.getMedias().stream()
                .map(media -> new GameMediaRes(
                    media.getMediaType(),
                    media.getUrl(),
                    media.getSortOrder()
                ))
                .toList(),
            game.getGenres().stream()
                .map(gameGenre -> gameGenre.getGenre().getDisplayName())
                .toList(),
            isWished,
            productGame.isDelisted()
        );
    }
}
