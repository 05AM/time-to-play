package org.example.batch.job.notification.writer;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.example.batch.infra.mail.MailTemplateRenderer;
import org.example.batch.infra.mail.SmtpMailSender;
import org.example.batch.infra.persistence.EmailNotificationLogService;
import org.example.batch.job.notification.WishlistSaleMailProperties;
import org.example.batch.model.WishlistSaleRow;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberGroupingMailWriter implements ItemStreamWriter<WishlistSaleRow> {

    private final WishlistSaleMailProperties props;
    private final EmailNotificationLogService logService;
    private final MailTemplateRenderer renderer;
    private final SmtpMailSender mailSender;

    private Long currentMemberId = null;
    private String currentEmail;
    private String currentName;
    private final List<WishlistSaleRow> buffer = new ArrayList<>();
    private final List<Long> bufferHistoryIds = new ArrayList<>();

    @Override
    public void write(Chunk<? extends WishlistSaleRow> items) {
        for (WishlistSaleRow row : items) {
            boolean include = logService.acquirePending(row.memberId(), row.priceHistoryId());
            if (!include)
                continue;

            if (currentMemberId != null && currentMemberId != row.memberId()) {
                flush();
            }

            if (currentMemberId == null) {
                currentMemberId = row.memberId();
                currentEmail = row.memberEmail();
                currentName = row.memberName();
            }

            buffer.add(row);
            bufferHistoryIds.add(row.priceHistoryId());
        }
    }

    private void flush() {
        if (currentMemberId == null || buffer.isEmpty()) {
            reset();
            return;
        }

        int limit = Math.min(buffer.size(), 30);
        List<WishlistSaleRow> trimmed = new ArrayList<>(buffer.subList(0, limit));

        String subject = String.format(props.subjectPrefix(), trimmed.size());
        String html = renderer.renderWishlistSale(Map.of(
            "memberName", currentName,
            "wishlistUrl", props.wishlistUrl(),
            "items", trimmed
        ));

        mailSender.sendHtml(props.fromEmail(), currentEmail, subject, html);

        OffsetDateTime sentAt = OffsetDateTime.now(ZoneId.of("Asia/Seoul"));
        for (Long historyId : bufferHistoryIds) {
            logService.markSent(currentMemberId, historyId, sentAt);
        }

        reset();
    }

    private void reset() {
        currentMemberId = null;
        currentEmail = null;
        currentName = null;
        buffer.clear();
        bufferHistoryIds.clear();
    }

    @Override
    public void close() throws ItemStreamException {
        flush();
    }
}
