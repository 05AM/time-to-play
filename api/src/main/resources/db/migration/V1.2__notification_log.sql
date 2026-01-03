CREATE TABLE IF NOT EXISTS email_notification_log
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id        BIGINT NOT NULL,
    price_history_id BIGINT NOT NULL,
    status           TEXT   NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    sent_at TIMESTAMPTZ,

    CONSTRAINT fk_email_log_member
        FOREIGN KEY (member_id) REFERENCES member (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_email_log_price_history
        FOREIGN KEY (price_history_id) REFERENCES product_price_history (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_email_log_member_history
        UNIQUE (member_id, price_history_id),

    CONSTRAINT ck_email_log_status
        CHECK (status IN ('PENDING', 'SENT'))
);

CREATE INDEX IF NOT EXISTS idx_email_log_member_status
    ON email_notification_log (member_id, status);
