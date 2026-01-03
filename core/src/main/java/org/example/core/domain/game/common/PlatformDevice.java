package org.example.core.domain.game.common;

import lombok.Getter;

@Getter
public enum PlatformDevice {

    PS5(GamePlatform.PSN, "PS5", "PS5"),
    PS4(GamePlatform.PSN, "PS4", "PS4"),

    STEAM_PC(GamePlatform.STEAM, "PC", "PC"),
    STEAM_DECK(GamePlatform.STEAM, "STEAM DECK", "STEAM DECK"),

    SWITCH(GamePlatform.NINTENDO, "SWITCH", "Nintendo Switch")
    ;
    
    private final GamePlatform gamePlatform;
    private final String code;
    private final String displayName;

    PlatformDevice(GamePlatform gamePlatform, String code, String displayName) {
        this.gamePlatform = gamePlatform;
        this.code = code;
        this.displayName = displayName;
    }

    public static PlatformDevice from(GamePlatform gamePlatform, String code) {
        for (PlatformDevice d : values()) {
            if (d.gamePlatform == gamePlatform && d.code.equals(code)) {
                return d;
            }
        }
        throw new IllegalArgumentException(
            "Unknown GamePlatform device: GamePlatform=" + gamePlatform + ", code=" + code
        );
    }
}
