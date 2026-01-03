package org.example.core.domain.game.common;

import lombok.Getter;

@Getter
public enum GamePlatform {

    PSN("PlayStation"),
    STEAM("Steam"),
    NINTENDO("Nintendo"),
    XBOX("Xbox"),
    EPIC_STORE("Epic Store")
    ;

    private String displayName;

    GamePlatform(String displayName) {
        this.displayName = displayName;
    }
}
