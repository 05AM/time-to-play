package org.example.batch.infra.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.core.domain.game.common.GamePlatform;
import org.example.core.domain.game.common.ReleaseStatus;
import org.example.core.domain.game.platform.CollectStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PlatformGameDao {

    private final JdbcClient jdbcClient;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public void batchUpsert(List<PlatformGameUpsert> rows) {
        if (rows == null || rows.isEmpty())
            return;

        String sql = """
            insert into platform_game (
              game_id,
              platform,
              platform_root_id_type,
              platform_root_id,
              name,
              display_name,
              search_name,
              store_url,
              release_status,
              collect_status,
              created_at,
              updated_at
            )
            values (
              :gameId,
              :platform,
              :rootIdType,
              :rootId,
              :name,
              :displayName,
              :searchName,
              :storeUrl,
              :releaseStatus,
              :collectStatus,
              CURRENT_TIMESTAMP,
              CURRENT_TIMESTAMP
            )
            on conflict (platform, platform_root_id_type, platform_root_id)
            do update set
              game_id = excluded.game_id,
              name = excluded.name,
              updated_at = CURRENT_TIMESTAMP
            """;

        SqlParameterSource[] batch = rows.stream()
            .map(r -> new MapSqlParameterSource()
                .addValue("gameId", r.gameId())
                .addValue("platform", r.platform())
                .addValue("rootIdType", r.rootIdType())
                .addValue("rootId", r.rootId())
                .addValue("name", r.name())
                .addValue("displayName", r.displayName())
                .addValue("searchName", r.searchName())
                .addValue("storeUrl", r.storeUrl())
                .addValue("releaseStatus", ReleaseStatus.UNKNOWN.name())
                .addValue("collectStatus", CollectStatus.CREATED.name())
            )
            .toArray(SqlParameterSource[]::new);

        namedJdbcTemplate.batchUpdate(sql, batch);
    }

    public Optional<PlatformGameRow> findNextCreatedForUpdate() {
        String sql = """
            select
                id,
                game_id,
                platform,
                platform_root_id_type,
                platform_root_id
            from platform_game
            where collect_status = :status
              and platform = :platform
            order by created_at asc
            limit 1
            for update skip locked
            """;

        return jdbcClient.sql(sql)
            .param("status", CollectStatus.CREATED.name())
            .param("platform", GamePlatform.PSN.name())
            .query(PlatformGameRow.class)
            .optional();
    }

    public void markDetailFetched(long platformGameId) {
        String sql = """
            update platform_game
            set collect_status = :status,
                last_synced_at = now(),
                updated_at = now()
            where id = :id
            """;

        jdbcClient.sql(sql)
            .param("status", CollectStatus.DETAILS_FETCHED.name())
            .param("id", platformGameId)
            .update();
    }

    public Map<String, Long> mapIdsByRootIds(
        String platform,
        String rootIdType,
        List<String> rootIds
    ) {
        if (rootIds == null || rootIds.isEmpty()) {
            return Map.of();
        }

        String sql = """
            select
                platform_root_id as root_id,
                id
            from platform_game
            where platform = :platform
              and platform_root_id_type = :rootIdType
              and platform_root_id in (:rootIds)
            """;

        return namedJdbcTemplate.query(
            sql,
            new MapSqlParameterSource()
                .addValue("platform", platform)
                .addValue("rootIdType", rootIdType)
                .addValue("rootIds", rootIds),
            (rs, rowNum) -> Map.entry(
                rs.getString("root_id"),
                rs.getLong("id")
            )
        ).stream().collect(
            Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (a, b) -> a
            )
        );
    }

    public void replacePlatformGameMedia(long platformGameId, List<MediaRow> media) {
        jdbcClient.sql("delete from platform_game_media where platform_game_id = :pgId")
            .param("pgId", platformGameId)
            .update();

        if (media == null || media.isEmpty())
            return;

        String sql = """
            insert into platform_game_media (
              platform_game_id, media_type, url, sort_order
            )
            values (
              :platformGameId, :mediaType, :url, :sortOrder
            )
            """;

        SqlParameterSource[] batch = media.stream()
            .map(m -> new MapSqlParameterSource()
                .addValue("platformGameId", platformGameId)
                .addValue("mediaType", m.mediaType())
                .addValue("url", m.url())
                .addValue("sortOrder", m.sortOrder())
            )
            .toArray(SqlParameterSource[]::new);

        namedJdbcTemplate.batchUpdate(sql, batch);
    }

    public record PlatformGameUpsert(
        Long gameId,
        String platform,
        String rootIdType,
        String rootId,
        String name,
        String displayName,
        String searchName,
        String storeUrl,
        String mainImageUrl
    ) {
    }

    public record PlatformGameRow(
        long id,
        long gameId,
        String platform,
        String platformRootIdType,
        String platformRootId
    ) {
    }

    public record MediaRow(
        String mediaType,
        String url,
        int sortOrder
    ) {
    }
}
