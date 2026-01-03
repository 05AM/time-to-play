package org.example.batch.job.notification.reader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import org.example.batch.model.WishlistSaleRow;
import org.example.core.domain.game.common.GamePlatform;
import org.springframework.jdbc.core.RowMapper;

public class WishlistSaleRowMapper implements RowMapper<WishlistSaleRow> {

    @Override
    public WishlistSaleRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new WishlistSaleRow(
            rs.getLong("member_id"),
            rs.getString("member_email"),
            rs.getString("member_name"),
            rs.getLong("price_history_id"),
            GamePlatform.valueOf(rs.getString("platform")),
            rs.getString("game_name"),
            rs.getString("main_image_url"),
            rs.getInt("price_original"),
            rs.getInt("price_current"),
            rs.getShort("discount_rate"),
            rs.getObject("price_changed_at", OffsetDateTime.class)
        );
    }
}
