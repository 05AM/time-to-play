package org.example.core.domain.game.product;

import jakarta.persistence.*;

import org.example.core.domain.common.entity.BaseCreatedAtEntity;
import org.example.core.domain.game.common.PriceStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product_price_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductPriceHistory extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_game_id", nullable = false)
    private ProductGame productGame;

    @Column(name = "price_original")
    private Integer priceOriginal;

    @Column(name = "price_current")
    private Integer priceCurrent;

    @Column(name = "discount_rate")
    private Short discountRate;

    @Column(name = "price_status", nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private PriceStatus priceStatus;
}
