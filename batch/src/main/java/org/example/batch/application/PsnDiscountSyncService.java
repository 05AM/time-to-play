package org.example.batch.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.example.batch.infra.client.psn.dto.response.PsnDiscountProductListRes;
import org.example.batch.infra.persistence.ProductGameDao;
import org.example.batch.infra.persistence.ProductPriceHistoryDao;
import org.example.core.domain.game.common.PriceStatus;
import org.example.core.domain.game.platform.PlatformIDType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PsnDiscountSyncService {

    private final ProductGameDao productGameDao;
    private final ProductPriceHistoryDao historyDao;

    public void syncDiscounts(List<PsnDiscountProductListRes.Product> products, Instant snapshotAt) {
        if (products == null || products.isEmpty())
            return;

        List<String> platformIds = collectPlatformIds(products);
        if (platformIds.isEmpty())
            return;

        Map<String, ProductGameDao.PriceSnapshot> currentPricesByPlatformId =
            productGameDao.findPriceSnapshotsByPlatformIds(PlatformIDType.PRODUCT.name(), platformIds);

        List<ProductPriceHistoryDao.HistoryRow> historyRows = new ArrayList<>();
        List<ProductGameDao.PricingUpdateRow> updateRows = new ArrayList<>();
        Instant now = Instant.now();

        for (var product : products) {
            if (!isValidProduct(product))
                continue;

            ProductGameDao.PriceSnapshot current = currentPricesByPlatformId.get(product.id());
            if (current == null)
                continue;

            ParsedPrice newPrice = parsePrice(product);

            if (!hasPriceChanged(current, newPrice))
                continue;

            historyRows.add(new ProductPriceHistoryDao.HistoryRow(
                current.productGameId(),
                newPrice.priceOriginal,
                newPrice.priceCurrent,
                newPrice.discountRate,
                newPrice.priceStatus.name()
            ));

            updateRows.add(new ProductGameDao.PricingUpdateRow(
                current.productGameId(),
                newPrice.priceOriginal,
                newPrice.priceCurrent,
                newPrice.discountRate,
                newPrice.priceStatus.name(),
                now
            ));
        }

        if (!historyRows.isEmpty()) {
            historyDao.batchInsert(historyRows, snapshotAt);
        }
        if (!updateRows.isEmpty()) {
            productGameDao.batchUpdatePricing(updateRows);
        }
    }

    private static List<String> collectPlatformIds(List<PsnDiscountProductListRes.Product> products) {
        Set<String> unique = new LinkedHashSet<>();
        for (var p : products) {
            if (p == null)
                continue;
            String id = p.id();
            if (id == null || id.isBlank())
                continue;
            unique.add(id);
        }
        return new ArrayList<>(unique);
    }

    private static boolean isValidProduct(PsnDiscountProductListRes.Product p) {
        return p != null && p.id() != null && !p.id().isBlank();
    }

    private static boolean hasPriceChanged(ProductGameDao.PriceSnapshot current, ParsedPrice next) {
        return !Objects.equals(current.priceOriginal(), next.priceOriginal)
            || !Objects.equals(current.priceCurrent(), next.priceCurrent)
            || !Objects.equals(current.discountRate(), next.discountRate)
            || !Objects.equals(current.priceStatus(), next.priceStatus.name());
    }

    private static final class ParsedPrice {
        final Integer priceOriginal;
        final Integer priceCurrent;
        final Short discountRate;
        final PriceStatus priceStatus;

        ParsedPrice(Integer priceOriginal, Integer priceCurrent, Short discountRate, PriceStatus priceStatus) {
            this.priceOriginal = priceOriginal;
            this.priceCurrent = priceCurrent;
            this.discountRate = discountRate;
            this.priceStatus = priceStatus;
        }
    }

    private static ParsedPrice parsePrice(PsnDiscountProductListRes.Product product) {
        var price = product.price();

        Integer original = null;
        Integer current = null;
        Short discountRate = null;

        if (price != null) {
            original = parseKrw(price.basePrice());
            current = parseKrw(price.discountedPrice());
            if (current == null)
                current = original;
            discountRate = parsePercent(price.discountText());
        }

        PriceStatus status = (current == null) ? PriceStatus.UNAVAILABLE : PriceStatus.PRICED;
        return new ParsedPrice(original, current, discountRate, status);
    }

    private static Integer parseKrw(String s) {
        if (s == null || s.isBlank())
            return null;

        int len = s.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9')
                sb.append(c);
        }
        if (sb.length() == 0)
            return null;

        try {
            return Integer.parseInt(sb.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Short parsePercent(String s) {
        if (s == null || s.isBlank())
            return null;

        int len = s.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9')
                sb.append(c);
        }
        if (sb.isEmpty())
            return null;

        try {
            int v = Integer.parseInt(sb.toString());
            if (v < 0)
                v = 0;
            if (v > 100)
                v = 100;
            return (short)v;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
