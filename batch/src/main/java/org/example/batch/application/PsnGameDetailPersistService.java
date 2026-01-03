package org.example.batch.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.example.batch.infra.client.psn.dto.response.PsnConceptGameDetailRes;
import org.example.batch.infra.persistence.GameDao;
import org.example.batch.infra.persistence.PlatformGameDao;
import org.example.batch.infra.persistence.ProductGameDao;
import org.example.batch.infra.persistence.ProductPriceHistoryDao;
import org.example.core.domain.game.common.GamePlatform;
import org.example.core.domain.game.common.Genre;
import org.example.core.domain.game.common.MediaType;
import org.example.core.domain.game.common.PlatformDevice;
import org.example.core.domain.game.common.PriceStatus;
import org.example.core.domain.game.common.ReleaseStatus;
import org.example.core.domain.game.platform.PlatformIDType;
import org.example.core.domain.game.product.ProductContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class PsnGameDetailPersistService {

    private static final Comparator<PsnConceptGameDetailRes.Media> VIDEO_FIRST =
        Comparator.comparingInt(m -> MediaType.VIDEO.name().equals(m.type()) ? 0 : 1);

    private final PlatformGameDao platformGameDao;
    private final ProductGameDao productGameDao;
    private final GameDao gameDao;
    private final ProductPriceHistoryDao historyDao;

    @Value("${external.psn.product-base-url}")
    private String storeBaseUrl;

    public void persist(
        PlatformGameDao.PlatformGameRow platformGame,
        PsnConceptGameDetailRes.Concept concept
    ) {
        List<PsnConceptGameDetailRes.Product> products =
            concept.products() == null ? List.of() : concept.products();

        if (!products.isEmpty()) {
            long gameId = platformGame.gameId();

            Set<String> genres = products.stream()
                .map(PsnConceptGameDetailRes.Product::localizedGenres)
                .flatMap(List::stream)
                .map(name -> Genre.fromDisplayName(name.value()).name())
                .collect(Collectors.toSet());

            gameDao.replaceGameGenre(gameId, genres);

            platformGameDao.replacePlatformGameMedia(
                platformGame.id(),
                orderedPlatformMedia(concept)
            );

            productGameDao.batchUpsert(
                products.stream()
                    .map(p -> toUpsert(platformGame.id(), p))
                    .toList()
            );

            Map<String, Long> idMap = productGameDao.mapProductGameIdByPlatformId(
                platformGame.id(),
                PlatformIDType.PRODUCT.name(),
                products.stream().map(PsnConceptGameDetailRes.Product::id).toList()
            );

            Instant snapshotAt = Instant.now();
            List<ProductPriceHistoryDao.HistoryRow> historyRows = new ArrayList<>();

            for (PsnConceptGameDetailRes.Product p : products) {
                Long productGameId = idMap.get(p.id());
                if (productGameId == null)
                    continue;

                productGameDao.replaceProductGameMedia(
                    productGameId,
                    orderedProductMedia(p)
                );

                productGameDao.replaceProductGameDevice(
                    productGameId,
                    p.platforms().stream()
                        .map(platform -> PlatformDevice.from(GamePlatform.PSN, platform).name())
                        .toList()
                );

                PsnConceptGameDetailRes.Price price = firstPrice(p);
                if (price != null) {
                    Integer original = price.basePriceValue();
                    Integer current = price.discountedValue();
                    Short discountRate = parseDiscountRate(price);

                    historyRows.add(new ProductPriceHistoryDao.HistoryRow(
                        productGameId,
                        original,
                        current,
                        discountRate,
                        original != null && current != null ? PriceStatus.PRICED.name() : PriceStatus.CREATED.name()
                    ));
                }
            }

            if (!historyRows.isEmpty()) {
                historyDao.batchInsert(historyRows, snapshotAt);
            }
        }

        platformGameDao.markDetailFetched(platformGame.id());
    }

    private List<PlatformGameDao.MediaRow> orderedPlatformMedia(PsnConceptGameDetailRes.Concept concept) {
        List<PsnConceptGameDetailRes.Media> medias =
            concept.media() == null ? List.of() : concept.media();

        List<PsnConceptGameDetailRes.Media> filtered = medias.stream()
            .filter(this::isKeepMedia)
            .sorted(VIDEO_FIRST)
            .toList();

        return IntStream.range(0, filtered.size())
            .mapToObj(i -> toPlatformMediaRow(filtered.get(i), i))
            .toList();
    }

    private List<ProductGameDao.MediaRow> orderedProductMedia(PsnConceptGameDetailRes.Product p) {
        List<PsnConceptGameDetailRes.Media> medias =
            p.media() == null ? List.of() : p.media();

        List<PsnConceptGameDetailRes.Media> filtered = medias.stream()
            .filter(this::isKeepMedia)
            .sorted(VIDEO_FIRST)
            .toList();

        return IntStream.range(0, filtered.size())
            .mapToObj(i -> toProductMediaRow(filtered.get(i), i))
            .toList();
    }

    private boolean isKeepMedia(PsnConceptGameDetailRes.Media m) {
        if (m == null)
            return false;

        if (MediaType.VIDEO.name().equals(m.type()))
            return true;

        return "SCREENSHOT".equals(m.role()) && "IMAGE".equals(m.type());
    }

    private PlatformGameDao.MediaRow toPlatformMediaRow(PsnConceptGameDetailRes.Media m, int order) {
        String mediaType = MediaType.VIDEO.name().equals(m.type())
            ? MediaType.VIDEO.name()
            : MediaType.IMAGE.name();

        return new PlatformGameDao.MediaRow(
            mediaType,
            m.url(),
            order
        );
    }

    private ProductGameDao.MediaRow toProductMediaRow(PsnConceptGameDetailRes.Media m, int order) {
        String mediaType = MediaType.VIDEO.name().equals(m.type())
            ? MediaType.VIDEO.name()
            : MediaType.IMAGE.name();

        return new ProductGameDao.MediaRow(
            mediaType,
            m.url(),
            order
        );
    }

    private ProductGameDao.ProductUpsert toUpsert(long platformGameId, PsnConceptGameDetailRes.Product p) {
        String editionType = (p.edition() == null) ? "empty" : p.edition().type();
        PsnConceptGameDetailRes.Price price = firstPrice(p);

        Integer original = (price == null) ? null : price.basePriceValue();
        Integer current = (price == null) ? null : price.discountedValue();
        Short discountRate = parseDiscountRate(price);

        String releaseStatus = (price == null) ? ReleaseStatus.ANNOUNCED.name() : ReleaseStatus.UNKNOWN.name();
        String priceStatus = (price == null) ? PriceStatus.UNAVAILABLE.name() : PriceStatus.PRICED.name();

        String features = parseFeatures(p);
        String mainImageUrl = findMasterImageUrl(p);

        return new ProductGameDao.ProductUpsert(
            platformGameId,
            PlatformIDType.PRODUCT.name(),
            p.id(),
            ProductContentType.resolvePSNProduct(p.name(), editionType, p.id()).name(),
            p.name(),
            p.invariantName(),
            features,
            releaseStatus,
            original,
            current,
            discountRate,
            buildStoreUrl(p.id()),
            mainImageUrl,
            priceStatus,
            false
        );
    }

    private static String parseFeatures(PsnConceptGameDetailRes.Product p) {
        List<String> featuresList = p.edition() == null ? null : p.edition().features();

        return featuresList == null || featuresList.isEmpty()
            ? null
            : String.join(";", featuresList);
    }

    private String findMasterImageUrl(PsnConceptGameDetailRes.Product p) {
        if (p.media() == null || p.media().isEmpty())
            return null;

        return p.media().stream()
            .filter(m -> m != null
                && "MASTER".equals(m.role())
                && "IMAGE".equals(m.type())
                && m.url() != null
                && !m.url().isBlank()
            )
            .map(PsnConceptGameDetailRes.Media::url)
            .findFirst()
            .orElse(null);
    }

    private PsnConceptGameDetailRes.Price firstPrice(PsnConceptGameDetailRes.Product p) {
        if (p.webctas() == null || p.webctas().isEmpty())
            return null;
        return p.webctas().getFirst().price();
    }

    private Short parseDiscountRate(PsnConceptGameDetailRes.Price price) {
        if (price == null || price.discountText() == null)
            return null;
        String s = price.discountText().trim();
        if (!s.startsWith("-") || !s.endsWith("%"))
            return null;
        try {
            return Short.parseShort(s.substring(1, s.length() - 1));
        } catch (Exception e) {
            return null;
        }
    }

    private String buildStoreUrl(String productId) {
        return storeBaseUrl + productId;
    }
}
