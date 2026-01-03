package org.example.batch.infra.persistence;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.batch.infra.persistence.ProductPriceHistoryDao.HistoryRow;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductGameDao {

    private final JdbcClient jdbcClient;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public void batchUpsert(List<ProductUpsert> rows) {
        if (rows == null || rows.isEmpty())
            return;

        String sql = """
            insert into product_game (
              platform_game_id,
              platform_id_type,
              platform_id,
              content_type,
              name,
              invariant_name,
              features, 
              release_status,
              price_original,
              price_current,
              discount_rate,
              store_url,
              main_image_url,
              price_status,
              is_delisted,
              last_seen_at,
              last_price_updated_at,
              updated_at
            )
            values (
              :platformGameId,
              :platformIdType,
              :platformId,
              :contentType,
              :name,
              :invariantName,
              :features, 
              :releaseStatus,
              :priceOriginal,
              :priceCurrent,
              :discountRate,
              :storeUrl,
              :mainImageUrl, 
              :priceStatus,
              :isDelisted,
              now(),
              now(),
              now()
            )
            on conflict (platform_game_id, platform_id_type, platform_id)
            do update set
              content_type = excluded.content_type,
              name = excluded.name,
              invariant_name = excluded.invariant_name,
              features = excluded.features, 
              release_status = excluded.release_status,
              price_original = excluded.price_original,
              price_current = excluded.price_current,
              discount_rate = excluded.discount_rate,
              store_url = excluded.store_url,
              main_image_url = excluded.main_image_url,
              price_status = excluded.price_status,
              is_delisted = excluded.is_delisted,
              last_seen_at = now(),
              last_price_updated_at = now(),
              updated_at = now()
            """;

        SqlParameterSource[] batch = rows.stream()
            .map(r -> new MapSqlParameterSource()
                .addValue("platformGameId", r.platformGameId())
                .addValue("platformIdType", r.platformIdType())
                .addValue("platformId", r.platformId())
                .addValue("contentType", r.contentType())
                .addValue("name", r.name())
                .addValue("invariantName", r.invariantName())
                .addValue("features", r.features())
                .addValue("releaseStatus", r.releaseStatus())
                .addValue("priceOriginal", r.priceOriginal())
                .addValue("priceCurrent", r.priceCurrent())
                .addValue("discountRate", r.discountRate())
                .addValue("storeUrl", r.storeUrl())
                .addValue("mainImageUrl", r.mainImageUrl())
                .addValue("priceStatus", r.priceStatus())
                .addValue("isDelisted", r.isDelisted())
            )
            .toArray(SqlParameterSource[]::new);

        namedJdbcTemplate.batchUpdate(sql, batch);
    }

    public Map<String, Long> mapProductGameIdByPlatformId(
        long platformGameId, String platformIDType, List<String> platformIds
    ) {
        if (platformIds == null || platformIds.isEmpty())
            return Map.of();

        String sql = """
            select platform_id, id
            from product_game
            where platform_game_id = :platformGameId
              and platform_id_type = :platformIdType
              and platform_id in (:platformIds)
            """;

        return jdbcClient.sql(sql)
            .param("platformGameId", platformGameId)
            .param("platformIdType", platformIDType)
            .param("platformIds", platformIds)
            .query(rs -> {
                Map<String, Long> map = new HashMap<>();
                while (rs.next()) {
                    map.put(rs.getString("platform_id"), rs.getLong("id"));
                }
                return map;
            });
    }

    public void replaceProductGameMedia(long productGameId, List<MediaRow> medias) {
        jdbcClient.sql("delete from product_game_media where product_game_id = :pgid")
            .param("pgid", productGameId)
            .update();

        if (medias == null || medias.isEmpty())
            return;

        String sql = """
            insert into product_game_media (
              product_game_id, media_type, url, sort_order
            )
            values (
              :productGameId, :mediaType, :url, :sortOrder
            )
            """;

        SqlParameterSource[] batch = medias.stream()
            .map(m -> new MapSqlParameterSource()
                .addValue("productGameId", productGameId)
                .addValue("mediaType", m.mediaType())
                .addValue("url", m.url())
                .addValue("sortOrder", m.sortOrder())
            )
            .toArray(SqlParameterSource[]::new);

        namedJdbcTemplate.batchUpdate(sql, batch);
    }

    public void replaceProductGameDevice(long productGameId, List<String> devices) {
        jdbcClient.sql("delete from product_game_device where product_game_id = :pid")
            .param("pid", productGameId)
            .update();

        if (devices == null || devices.isEmpty())
            return;

        String sql = """
            insert into product_game_device (
              product_game_id, device
            )
            values (
              :productGameId, :device
            )
            """;

        SqlParameterSource[] batch = devices.stream()
            .map(d -> new MapSqlParameterSource()
                .addValue("productGameId", productGameId)
                .addValue("device", d)
            )
            .toArray(SqlParameterSource[]::new);

        namedJdbcTemplate.batchUpdate(sql, batch);
    }

    public Map<String, PriceSnapshot> findPriceSnapshotsByPlatformIds(String platformIdType, List<String> platformIds) {
        if (platformIds == null || platformIds.isEmpty())
            return Map.of();

        String sql = """
            select
              platform_id,
              id as product_game_id,
              price_original,
              price_current,
              discount_rate,
              price_status
            from product_game
            where platform_id_type = :platformIdType
              and platform_id in (:platformIds)
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("platformIdType", platformIdType)
            .addValue("platformIds", platformIds);

        Map<String, PriceSnapshot> map = new HashMap<>();
        namedJdbcTemplate.query(sql, params, rs -> {
            String platformId = rs.getString("platform_id");
            long productGameId = rs.getLong("product_game_id");

            Integer priceOriginal = (Integer)rs.getObject("price_original");
            Integer priceCurrent = (Integer)rs.getObject("price_current");
            Short discountRate = (Short)rs.getObject("discount_rate");
            String priceStatus = rs.getString("price_status");

            map.put(platformId, new PriceSnapshot(
                productGameId,
                priceOriginal,
                priceCurrent,
                discountRate,
                priceStatus
            ));
        });

        return map;
    }

    public void batchUpdatePricing(List<PricingUpdateRow> rows) {
        if (rows == null || rows.isEmpty())
            return;

        String sql = """
            update product_game
            set
              price_original = :priceOriginal,
              price_current = :priceCurrent,
              discount_rate = :discountRate,
              price_status = :priceStatus,
              last_price_updated_at = :now,
              updated_at = :now
            where id = :productGameId
            """;

        SqlParameterSource[] batch = rows.stream()
            .map(r -> new MapSqlParameterSource()
                .addValue("productGameId", r.productGameId())
                .addValue("priceOriginal", r.priceOriginal())
                .addValue("priceCurrent", r.priceCurrent())
                .addValue("discountRate", r.discountRate())
                .addValue("priceStatus", r.priceStatus())
                .addValue("now", r.now())
            )
            .toArray(SqlParameterSource[]::new);

        namedJdbcTemplate.batchUpdate(sql, batch);
    }

    public record ProductUpsert(
        long platformGameId,
        String platformIdType,
        String platformId,
        String contentType,
        String name,
        String invariantName,
        String features,
        String releaseStatus,
        Integer priceOriginal,
        Integer priceCurrent,
        Short discountRate,
        String storeUrl,
        String mainImageUrl,
        String priceStatus,
        boolean isDelisted
    ) {
    }

    public record MediaRow(String mediaType, String url, int sortOrder) {
    }

    public record PriceSnapshot(
        long productGameId,
        Integer priceOriginal,
        Integer priceCurrent,
        Short discountRate,
        String priceStatus
    ) {
    }

    public record PricingUpdateRow(
        long productGameId,
        Integer priceOriginal,
        Integer priceCurrent,
        Short discountRate,
        String priceStatus,
        Instant now
    ) {
    }
}
