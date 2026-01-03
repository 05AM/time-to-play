package org.example.core.domain.game.media;

import jakarta.persistence.*;

import org.example.core.domain.game.common.MediaType;
import org.example.core.domain.game.product.ProductGame;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product_game_media")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductGameMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_game_id", nullable = false)
    private ProductGame productGame;

    @Column(name = "media_type", nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
