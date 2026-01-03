package org.example.core.domain.wishlist;

import jakarta.persistence.*;

import org.example.core.domain.common.entity.BaseCreatedAtEntity;
import org.example.core.domain.game.product.ProductGame;
import org.example.core.domain.member.Member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "wishlist")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Wishlist extends BaseCreatedAtEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_game_id", nullable = false)
    private ProductGame productGame;

    @Column(name = "notify_discount_rate", nullable = false)
    private short notifyDiscountRate;

    public Wishlist(Member member, ProductGame productGame, short notifyDiscountRate) {
        this.member = member;
        this.productGame = productGame;
        this.notifyDiscountRate = notifyDiscountRate;
    }
}
