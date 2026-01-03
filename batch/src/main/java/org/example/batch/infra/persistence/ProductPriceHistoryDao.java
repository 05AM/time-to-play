package org.example.batch.infra.persistence;

import java.sql.Types;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductPriceHistoryDao {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public void batchInsert(List<HistoryRow> rows, Instant createdAt) {
        if (rows == null || rows.isEmpty())
            return;

        String sql = """
            insert into product_price_history (
              product_game_id,
              price_original,
              price_current,
              discount_rate,
              price_status,
              created_at
            )
            values (
              :productGameId,
              :priceOriginal,
              :priceCurrent,
              :discountRate,
              :priceStatus,
              :createdAt
            )
            on conflict (product_game_id, created_at)
            do nothing
            """;

        OffsetDateTime createdAtOdt = createdAt.atOffset(ZoneOffset.UTC);
        SqlParameterSource[] batch = rows.stream()
            .map(r -> new MapSqlParameterSource()
                .addValue("productGameId", r.productGameId())
                .addValue("priceOriginal", r.priceOriginal())
                .addValue("priceCurrent", r.priceCurrent())
                .addValue("discountRate", r.discountRate())
                .addValue("priceStatus", r.priceStatus())
                .addValue("createdAt", createdAtOdt, Types.TIMESTAMP_WITH_TIMEZONE)
            )
            .toArray(SqlParameterSource[]::new);

        namedJdbcTemplate.batchUpdate(sql, batch);
    }

    public record HistoryRow(
        long productGameId,
        Integer priceOriginal,
        Integer priceCurrent,
        Short discountRate,
        String priceStatus
    ) {
    }
}
