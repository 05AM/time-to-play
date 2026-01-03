package org.example.core.domain.game.concept;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.example.core.domain.common.entity.BaseTimeEntity;
import org.example.core.domain.game.genre.GameGenre;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "game")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Game extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameGenre> genres = new ArrayList<>();

    @Column(name = "canonical_title", length = 200)
    private String canonicalTitle;

    @Column(name = "canonical_slug", nullable = false, length = 200)
    private String canonicalSlug;

    @Column(length = 200)
    private String developer;

    @Column(name = "release_date")
    private LocalDate releaseDate;
}
