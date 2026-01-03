package org.example.batch.infra.persistence;

import java.time.OffsetDateTime;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmailNotificationLogDao {

    private final JdbcTemplate jdbcTemplate;

    public int insertPendingIfAbsent(long memberId, long priceHistoryId) {
        String sql = """
            insert into email_notification_log (member_id, price_history_id, status)
            values (?, ?, 'PENDING')
            on conflict (member_id, price_history_id) do nothing
            """;
        return jdbcTemplate.update(sql, memberId, priceHistoryId);
    }

    public String getStatus(long memberId, long priceHistoryId) {
        String sql = """
            select status
            from email_notification_log
            where member_id = ? and price_history_id = ?
            """;
        return jdbcTemplate.queryForObject(sql, String.class, memberId, priceHistoryId);
    }

    public int markSent(long memberId, long priceHistoryId, OffsetDateTime sentAt) {
        String sql = """
            update email_notification_log
            set status = 'SENT',
                sent_at = ?
            where member_id = ?
              and price_history_id = ?
              and status = 'PENDING'
            """;
        return jdbcTemplate.update(sql, sentAt, memberId, priceHistoryId);
    }
}
