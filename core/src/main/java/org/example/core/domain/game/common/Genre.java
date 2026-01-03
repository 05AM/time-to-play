package org.example.core.domain.game.common;

import lombok.Getter;

@Getter
public enum Genre {

    ACTION("ACTION", "액션"),
    ADVENTURE("ADVENTURE", "어드벤처"),
    PUZZLE("PUZZLE", "퍼즐"),
    ARCADE("ARCADE", "아케이드"),
    CASUAL("CASUAL", "캐주얼"),
    SIMULATION("SIMULATION", "시뮬레이션"),
    ROLE_PLAYING_GAMES("ROLE_PLAYING_GAMES", "롤플레잉 게임"),
    SHOOTER("SHOOTER", "슈팅"),
    STRATEGY("STRATEGY", "전략"),
    RACING("RACING", "드라이빙/레이싱"),
    HORROR("HORROR", "공포"),
    FAMILY("FAMILY", "가족"),
    SIMULATOR("SIMULATOR", "시뮬레이터"),
    UNIQUE("UNIQUE", "유니크"),
    SPORTS("SPORTS", "스포츠"),
    FIGHTING("FIGHTING", "격투"),
    BRAIN_TRAINING("BRAIN_TRAINING", "두뇌개발"),
    PARTY("PARTY", "파티"),
    MUSIC_RHYTHM("MUSIC/RHYTHM", "음악/리듬"),
    QUIZ("QUIZ", "퀴즈"),
    EDUCATIONAL("EDUCATIONAL", "교육"),
    ADULT("ADULT", "성인"),
    FITNESS("FITNESS", "피트니스");

    private final String code;
    private final String displayName;

    Genre(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static Genre fromCode(String code) {
        for (Genre g : values()) {
            if (g.code.equals(code)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Unknown genre code: " + code);
    }

    public static Genre fromDisplayName(String displayName) {
        for (Genre g : values()) {
            if (g.displayName.equals(displayName)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Unknown genre display name: " + displayName);
    }
}
