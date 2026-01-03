package org.example.batch.util;

import java.text.Normalizer;
import java.util.Locale;

public final class GameTitleNormalizer {

    private GameTitleNormalizer() {}

    public static String normalizeDisplay(String s) {
        if (s == null) return null;

        String x = s;

        x = x.replace('\u00A0', ' ');
        x = Normalizer.normalize(x, Normalizer.Form.NFKC);
        x = x.replaceAll("[\\t\\n\\r\\f]+", " ");
        x = x.replaceAll("\\s+", " ").trim();

        return x;
    }

    public static String normalizeKey(String s) {
        String x = normalizeDisplay(s);

        if (x == null || x.isBlank()) return x;

        x = x.replace("®", "")
            .replace("™", "")
            .replace("©", "");
        x = x.replaceAll("[\\p{Punct}]+", " ");
        x = x.replaceAll("\\s+", " ").trim().toLowerCase(Locale.ROOT);

        return x;
    }
}
