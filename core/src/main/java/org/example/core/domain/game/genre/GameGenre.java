package org.example.core.domain.game.genre;

import jakarta.persistence.*;

import org.example.core.domain.game.common.Genre;
import org.example.core.domain.game.concept.Game;
import org.example.core.domain.game.platform.PlatformGame;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "game_genre")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GameGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Enumerated(EnumType.STRING)
    private Genre genre;
}
