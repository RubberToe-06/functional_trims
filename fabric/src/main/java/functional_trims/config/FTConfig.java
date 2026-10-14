package functional_trims.config;

import java.util.Locale;

public final class FTConfig {
    private FTConfig() {
    }

    public static FunctionalTrimsConfig cfg() {
        return ConfigManager.get();
    }

    public static boolean isTrimEnabled(String material) {
        FunctionalTrimsConfig cfg = cfg();

        if (!cfg.modEnabled) {
            return false;
        }

        return switch (normalize(material)) {
            case "iron" -> cfg.iron.enabled;
            case "gold" -> cfg.gold.enabled;
            case "diamond" -> cfg.diamond.enabled;
            case "netherite" -> cfg.netherite.enabled;
            case "redstone" -> cfg.redstone.enabled;
            case "emerald" -> cfg.emerald.enabled;
            case "lapis" -> cfg.lapis.enabled;
            case "quartz" -> cfg.quartz.enabled;
            case "amethyst" -> cfg.amethyst.enabled;
            case "copper" -> cfg.copper.enabled;
            case "resin" -> cfg.resin.enabled;
            default -> true;
        };
    }

    private static String normalize(String material) {
        return material == null ? "" : material.toLowerCase(Locale.ROOT);
    }
}