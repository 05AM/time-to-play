package org.example.core.domain.game.media;

import jakarta.persistence.*;

import org.example.core.domain.game.common.MediaType;
import org.example.core.domain.game.platform.PlatformGame;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "platform_game_media")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlatformGameMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "platform_game_id", nullable = false)
    private PlatformGame platformGame;

    @Column(name = "media_type", nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;
}
