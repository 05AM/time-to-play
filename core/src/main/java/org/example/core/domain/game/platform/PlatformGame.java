package org.example.core.domain.game.platform;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.core.domain.common.entity.BaseTimeEntity;
import org.example.core.domain.game.concept.Game;
import org.example.core.domain.game.common.GamePlatform;
import org.example.core.domain.game.common.ReleaseStatus;
import org.example.core.domain.game.media.PlatformGameMedia;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "platform_game")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlatformGame extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "platformGame", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<PlatformGameMedia> medias = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private GamePlatform platform;

    @Column(name = "platform_root_id_type", nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private PlatformRootIDType platformRootIdType;

    @Column(name = "platform_root_id", nullable = false, length = 80)
    private String platformRootId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "display_name", nullable = false, length = 200)
    private String displayName;

    @Column(name = "search_name", nullable = false, length = 200)
    private String searchName;

    @Column(name = "invariant_name", length = 200)
    private String invariantName;

    @Column(name = "store_url", length = 500)
    private String storeUrl;

    @Column(name = "main_image_url", length = 500)
    private String mainImageUrl;

    @Column(length = 200)
    private String publisher;

    @Column(name = "release_at")
    private LocalDateTime releaseAt;

    @Column(name = "release_status", nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private ReleaseStatus releaseStatus;

    @Column(name = "collect_status", nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private CollectStatus collectStatus;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @Column(name = "last_error", length = 500)
    private String lastError;

    @Column(name = "last_error_at")
    private LocalDateTime lastErrorAt;
}
