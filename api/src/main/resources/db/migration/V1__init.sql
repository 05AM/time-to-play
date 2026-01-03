-- game
CREATE TABLE IF NOT EXISTS game
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    canonical_title VARCHAR(200),
    canonical_slug  VARCHAR(200) NOT NULL,
    developer       VARCHAR(200),
    release_date    DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_game_canonical_slug
        UNIQUE (canonical_slug)
);


-- platform_game
CREATE TABLE IF NOT EXISTS platform_game
(
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    game_id               BIGINT,
    platform              TEXT         NOT NULL,
    platform_root_id_type TEXT         NOT NULL,
    platform_root_id      VARCHAR(80)  NOT NULL,
    name                  VARCHAR(200) NOT NULL,
    display_name          VARCHAR(200) NOT NULL,
    search_name           VARCHAR(200) NOT NULL,
    invariant_name        VARCHAR(200),
    store_url             VARCHAR(500),
    main_image_url        VARCHAR(500),
    publisher             VARCHAR(200),
    release_at TIMESTAMPTZ,
    release_status        TEXT         NOT NULL,
    collect_status        TEXT         NOT NULL,
    last_synced_at TIMESTAMPTZ,
    last_error            VARCHAR(500),
    last_error_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_platform_game_game
        FOREIGN KEY (game_id) REFERENCES game (id)
            ON DELETE SET NULL,

    CONSTRAINT ck_platform_game_release_status
        CHECK (release_status IN ('ANNOUNCED', 'PREORDER', 'RELEASED', 'CANCELED', 'UNKNOWN')),

    CONSTRAINT ck_platform_game_collect_status
        CHECK (collect_status IN ('CREATED', 'DETAILS_FETCHED', 'FAILED')),

    CONSTRAINT uq_platform_game_root
        UNIQUE (platform, platform_root_id_type, platform_root_id)
);

CREATE INDEX IF NOT EXISTS idx_platform_game_game_id
    ON platform_game (game_id);


-- product_game
CREATE TABLE IF NOT EXISTS product_game
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    platform_game_id BIGINT       NOT NULL,
    platform_id_type TEXT         NOT NULL,
    platform_id      VARCHAR(120) NOT NULL,
    content_type     TEXT         NOT NULL,
    name             VARCHAR(250) NOT NULL,
    invariant_name   VARCHAR(200),
    features         TEXT,
    release_status   TEXT         NOT NULL,
    price_original   INTEGER,
    price_current    INTEGER,
    discount_rate    SMALLINT,
    is_delisted      BOOLEAN      NOT NULL DEFAULT FALSE,
    store_url        VARCHAR(500),
    main_image_url   VARCHAR(500),
    price_status     TEXT         NOT NULL,
    last_seen_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_price_updated_at TIMESTAMPTZ,
    last_price_changed_at TIMESTAMPTZ,
    last_error       VARCHAR(500),
    last_error_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_product_game_platform_game
        FOREIGN KEY (platform_game_id) REFERENCES platform_game (id)
            ON DELETE CASCADE,

    CONSTRAINT ck_product_game_content_type
        CHECK (content_type IN ('BASE_GAME', 'BUNDLE', 'DLC', 'OTHER')),

    CONSTRAINT ck_product_game_release_status
        CHECK (release_status IN ('ANNOUNCED', 'PREORDER', 'RELEASED', 'CANCELED', 'UNKNOWN')),

    CONSTRAINT ck_product_game_price_status
        CHECK (price_status IN ('CREATED', 'PRICED', 'UNAVAILABLE', 'FAILED')),

    CONSTRAINT ck_product_game_prices_non_negative
        CHECK (
            (price_original IS NULL OR price_original >= 0)
                AND (price_current IS NULL OR price_current >= 0)
            ),

    CONSTRAINT ck_product_game_discount_rate_range
        CHECK (
            discount_rate IS NULL
                OR (discount_rate >= 0 AND discount_rate <= 100)
            ),

    CONSTRAINT uq_product_game_platform_offer
        UNIQUE (platform_game_id, platform_id_type, platform_id)
);


-- product_price_history
CREATE TABLE IF NOT EXISTS product_price_history
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_game_id BIGINT NOT NULL,
    price_original  INTEGER,
    price_current   INTEGER,
    discount_rate   SMALLINT,
    price_status    TEXT   NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT fk_price_history_product_game
        FOREIGN KEY (product_game_id) REFERENCES product_game (id)
            ON DELETE CASCADE,

    CONSTRAINT ck_price_history_price_status
        CHECK (price_status IN ('PRICED', 'UNAVAILABLE')),

    CONSTRAINT ck_price_history_prices_non_negative
        CHECK (
            (price_original IS NULL OR price_original >= 0)
                AND (price_current IS NULL OR price_current >= 0)
            ),

    CONSTRAINT ck_price_history_discount_rate_range
        CHECK (
            discount_rate IS NULL
                OR (discount_rate >= 0 AND discount_rate <= 100)
            ),

    CONSTRAINT uq_price_history_created
        UNIQUE (product_game_id, created_at)
);


-- platform_game_media
CREATE TABLE IF NOT EXISTS platform_game_media
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    platform_game_id BIGINT NOT NULL,
    media_type       TEXT   NOT NULL,
    url              TEXT   NOT NULL,
    sort_order       INT    NOT NULL,

    CONSTRAINT uq_platform_game_media_order
        UNIQUE (platform_game_id, sort_order)
);

CREATE INDEX IF NOT EXISTS idx_platform_game_media_game
    ON platform_game_media (platform_game_id);


-- product_game_media
CREATE TABLE IF NOT EXISTS product_game_media
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_game_id BIGINT NOT NULL,
    media_type      TEXT   NOT NULL,
    url             TEXT   NOT NULL,
    sort_order      INT    NOT NULL,

    CONSTRAINT uq_product_game_media_order
        UNIQUE (product_game_id, sort_order)
);

CREATE INDEX IF NOT EXISTS idx_product_game_media_product
    ON product_game_media (product_game_id);

-- genre
CREATE TABLE IF NOT EXISTS game_genre
(
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    game_id BIGINT NOT NULL,
    genre   TEXT   NOT NULL,

    CONSTRAINT fk_game_genre_game
        FOREIGN KEY (game_id) REFERENCES game (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_game_genre
        UNIQUE (game_id, genre)
);

CREATE INDEX IF NOT EXISTS idx_game_genre_genre
    ON game_genre (genre);


-- product_game_device
CREATE TABLE IF NOT EXISTS product_game_device
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_game_id BIGINT NOT NULL,
    device          TEXT   NOT NULL,

    CONSTRAINT fk_product_game_device_product_game
        FOREIGN KEY (product_game_id) REFERENCES product_game (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_product_game_device
        UNIQUE (product_game_id, device)
);

CREATE INDEX IF NOT EXISTS idx_product_game_device_device
    ON product_game_device (device);
