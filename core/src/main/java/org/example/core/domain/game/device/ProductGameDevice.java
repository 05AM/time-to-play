package org.example.core.domain.game.device;

import jakarta.persistence.*;

import org.example.core.domain.game.common.PlatformDevice;
import org.example.core.domain.game.platform.PlatformGame;
import org.example.core.domain.game.product.ProductGame;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product_game_device")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductGameDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_game_id", nullable = false)
    private ProductGame productGame;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private PlatformDevice device;
}
