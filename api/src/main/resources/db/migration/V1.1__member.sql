-- member
CREATE TABLE member
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role  VARCHAR(50)  NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ,

    CONSTRAINT member_email_unique
        UNIQUE (email)
);

-- member_oauth_account
CREATE TABLE member_oauth_account
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id        BIGINT       NOT NULL,
    provider         VARCHAR(20)  NOT NULL,
    provider_user_id VARCHAR(100) NOT NULL,
    email            VARCHAR(255) NOT NULL,
    refresh_token    VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_member_oauth_account_member
        FOREIGN KEY (member_id) REFERENCES member (id)
            ON DELETE CASCADE,

    CONSTRAINT uk_member_oauth_provider_user
        UNIQUE (provider, provider_user_id),

    CONSTRAINT uk_member_oauth_member_provider
        UNIQUE (member_id, provider)
);

CREATE INDEX idx_member_oauth_account_member_id
    ON member_oauth_account (member_id);

CREATE INDEX idx_member_oauth_account_email
    ON member_oauth_account (email);


-- wishlist
CREATE TABLE wishlist
(
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    member_id            BIGINT   NOT NULL,
    product_game_id      BIGINT   NOT NULL,
    notify_discount_rate SMALLINT NOT NULL,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_wishlist_member
        FOREIGN KEY (member_id) REFERENCES member (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_wishlist_product_game
        FOREIGN KEY (product_game_id) REFERENCES product_game (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_wishlist_member_product
        UNIQUE (member_id, product_game_id),

    CONSTRAINT ck_wishlist_notify_discount_rate
        CHECK (notify_discount_rate >= 0 AND notify_discount_rate <= 100)
);

CREATE INDEX idx_wishlist_member_id ON wishlist (member_id);
CREATE INDEX idx_wishlist_product_game_id ON wishlist (product_game_id);

