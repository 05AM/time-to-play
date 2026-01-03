package org.example.core.domain.game.product;

public enum ProductContentType {
    BASE_GAME,
    BUNDLE,
    DLC,
    OTHER
    ;

    public static ProductContentType resolvePSNProduct(String name, String type, String productId) {
        String lowerName = safeLower(name);
        String lowerType = safeLower(type);
        String lowerProductId = safeLower(productId);

        if (containsAny(lowerName, lowerType, lowerProductId, "edition", "premium", "에디션", "bundle")) {
            return BUNDLE;
        }

        if (containsAny(lowerName, lowerType, lowerProductId, "dlc")) {
            return DLC;
        }

        if (containsAny(lowerName, lowerType, lowerProductId, "standard")) {
            return BASE_GAME;
        }

        return OTHER;
    }

    private static String safeLower(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private static boolean containsAny(String a, String b, String c, String... keywords) {
        for (String keyword : keywords) {
            if (a.contains(keyword) || b.contains(keyword) || c.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

}
