package org.example.batch.util;

import java.text.Normalizer;
import java.util.Locale;

public class SlugNormalizer {

    public static String toTempSlug(String title) {
        if (title == null) return null;

        String s = Normalizer.normalize(title, Normalizer.Form.NFKC)
            .toLowerCase(Locale.ROOT)
            .replace("®", "")
            .replace("™", "")
            .replace("©", "");

        s = s.replaceAll("[^a-z0-9가-힣\\s]", " ");
        s = s.trim().replaceAll("\\s+", "-");

        return s.isBlank() ? "untitled" : s;
    }

}
