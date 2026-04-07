package functional_trims;

import functional_trims.config.ConfigManager;
import functional_trims.criteria.ModCriteria;
import functional_trims.event.ChargedAttackHandler;
import functional_trims.event.RedstoneTrimPowerTicker;
import functional_trims.event.TrimAdvancementHandler;
import functional_trims.trim_effect.*;
import functional_trims.effect.AmethystVisionEffect;
import functional_trims.effect.ModEffects;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionalTrims implements ModInitializer {

    public static final String MOD_ID = "functional_trims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ConfigManager.load();

        registerContent();
        registerEffects();
        registerEventHandlers();
        registerTickHandlers();

        LOGGER.info("Functional Trims initialized.");
    }

    private static void registerContent() {
        ModEffects.register();
        ModCriteria.init();
    }

    private static void registerEffects() {
        ResinTrimEffect.register();
        AmethystTrimEffect.register();
        IronTrimEffect.register();
        DiamondTrimEffect.register();
    }

    private static void registerEventHandlers() {
        RedstoneTrimPowerTicker.register();
        TrimAdvancementHandler.register();
        ChargedAttackHandler.register();
    }

    private static void registerTickHandlers() {
        ServerTickEvents.END_LEVEL_TICK.register(new CopperTrimEffect());
        ServerTickEvents.END_LEVEL_TICK.register(AmethystVisionEffect::tick);
    }
}