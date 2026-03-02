package functional_trims.config;

public final class FTConfig {

    public static boolean isTrimEnabled(String material) {
        var cfg = ConfigManager.get();

        if (!cfg.enableAll) return false;

        return switch (material) {
            case "amethyst" -> cfg.amethystEnabled;
            case "iron" -> cfg.ironEnabled;
            case "gold" -> cfg.goldEnabled;
            case "diamond" -> cfg.diamondEnabled;
            case "netherite" -> cfg.netheriteEnabled;
            case "emerald" -> cfg.emeraldEnabled;
            case "lapis" -> cfg.lapisEnabled;
            case "copper" -> cfg.copperEnabled;
            case "quartz" -> cfg.quartzEnabled;
            default -> true;
        };
    }
}
