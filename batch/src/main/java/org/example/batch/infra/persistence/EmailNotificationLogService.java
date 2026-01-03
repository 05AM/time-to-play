package org.example.batch.infra.persistence;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailNotificationLogService {

    private final EmailNotificationLogDao dao;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean acquirePending(long memberId, long priceHistoryId) {
        int inserted = dao.insertPendingIfAbsent(memberId, priceHistoryId);
        if (inserted == 1) return true;

        String status = dao.getStatus(memberId, priceHistoryId);
        return "PENDING".equals(status);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSent(long memberId, long priceHistoryId, OffsetDateTime sentAt) {
        dao.markSent(memberId, priceHistoryId, sentAt);
    }
}
