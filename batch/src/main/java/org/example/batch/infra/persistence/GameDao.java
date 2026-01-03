package org.example.batch.infra.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GameDao {

    private final JdbcClient jdbcClient;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public void batchInsertIgnore(List<GameInsert> games) {
        if (games == null || games.isEmpty())
            return;

        String sql = """
            insert into game (
              canonical_title,
              canonical_slug,
              created_at,
              updated_at
            )
            values (
              :canonicalTitle,
              :canonicalSlug,
              now(),
              now()
            )
            on conflict (canonical_slug) do nothing
            """;

        SqlParameterSource[] batch = games.stream()
            .map(g -> new MapSqlParameterSource()
                .addValue("canonicalTitle", g.canonicalTitle())
                .addValue("canonicalSlug", g.canonicalSlug())
            )
            .toArray(SqlParameterSource[]::new);

        namedJdbcTemplate.batchUpdate(sql, batch);
    }

    public Map<String, Long> findIdsBySlugs(List<String> slugs) {
        if (slugs == null || slugs.isEmpty())
            return Map.of();

        String sql = """
            select canonical_slug, id
            from game
            where canonical_slug in (:slugs)
            """;

        return jdbcClient.sql(sql)
            .param("slugs", slugs)
            .query(rs -> {
                Map<String, Long> map = new HashMap<>();
                while (rs.next()) {
                    map.put(rs.getString("canonical_slug"), rs.getLong("id"));
                }
                return map;
            });
    }

    public void replaceGameGenre(long gameId, Set<String> genres) {
        jdbcClient.sql("delete from game_genre where game_id = :gameId")
            .param("gameId", gameId)
            .update();

        if (genres == null || genres.isEmpty())
            return;

        String sql = """
            insert into game_genre (game_id, genre)
            values (:gameId, :genre)
            """;

        SqlParameterSource[] batch = genres.stream()
            .map(genre -> new MapSqlParameterSource()
                .addValue("gameId", gameId)
                .addValue("genre", genre)
            )
            .toArray(SqlParameterSource[]::new);

        namedJdbcTemplate.batchUpdate(sql, batch);
    }

    public record GameInsert(
        String canonicalTitle,
        String canonicalSlug
    ) {
    }
}
