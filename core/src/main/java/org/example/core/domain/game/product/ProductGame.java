package org.example.core.domain.game.product;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.core.domain.common.converter.StringListConverter;
import org.example.core.domain.common.entity.BaseTimeEntity;
import org.example.core.domain.game.common.PriceStatus;
import org.example.core.domain.game.common.ReleaseStatus;
import org.example.core.domain.game.device.ProductGameDevice;
import org.example.core.domain.game.media.ProductGameMedia;
import org.example.core.domain.game.platform.PlatformGame;
import org.example.core.domain.game.platform.PlatformIDType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product_game")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductGame extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "platform_game_id", nullable = false)
    private PlatformGame platformGame;

    @OneToMany(mappedBy = "productGame", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductGameDevice> devices = new ArrayList<>();

    @OneToMany(mappedBy = "productGame", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ProductGameMedia> medias = new ArrayList<>();

    @Column(name = "platform_id_type", nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private PlatformIDType platformIdType;

    @Column(name = "platform_id", nullable = false, length = 120)
    private String platformId;

    @Column(name = "content_type", nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private ProductContentType contentType;

    @Column(nullable = false, length = 250)
    private String name;

    @Column(name = "invariant_name", length = 200)
    private String invariantName;

    @Column(name = "features", length = 1000)
    @Convert(converter = StringListConverter.class)
    private List<String> features = new ArrayList<>();

    @Column(name = "release_status", nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private ReleaseStatus releaseStatus;

    @Column(name = "price_original")
    private Integer priceOriginal;

    @Column(name = "price_current")
    private Integer priceCurrent;

    @Column(name = "discount_rate")
    private Short discountRate;

    @Column(name = "is_delisted", nullable = false)
    private boolean delisted;

    @Column(name = "store_url", length = 500)
    private String storeUrl;

    @Column(name = "main_image_url", length = 500)
    private String mainImageUrl;

    @Column(name = "price_status", nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private PriceStatus priceStatus;

    @Column(name = "last_seen_at", nullable = false, insertable = false)
    private LocalDateTime lastSeenAt;

    @Column(name = "last_price_updated_at")
    private LocalDateTime lastPriceUpdatedAt;

    @Column(name = "last_price_changed_at")
    private LocalDateTime lastPriceChangedAt;

    @Column(name = "last_error", length = 500)
    private String lastError;

    @Column(name = "last_error_at")
    private LocalDateTime lastErrorAt;
}
