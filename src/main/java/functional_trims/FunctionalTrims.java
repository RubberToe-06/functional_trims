package functional_trims;

import functional_trims.config.ConfigManager;
import functional_trims.criteria.ModCriteria;
import functional_trims.event.ChargedAttackHandler;
import functional_trims.event.DiamondTrimGuard;
import functional_trims.event.RedstoneTrimPowerTicker;
import functional_trims.event.TrimAdvancementHandler;
import functional_trims.trim_effect.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionalTrims implements ModInitializer {
	public static final String MOD_ID = "functional_trims";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
        ModEffects.register();
        RedstoneTrimPowerTicker.register();
        TrimAdvancementHandler.register();
        DiamondTrimGuard.register();
        AmethystTrimEffect.register();
        ServerTickEvents.END_WORLD_TICK.register(new CopperTrimEffect());
        ChargedAttackHandler.register();
        ServerTickEvents.END_SERVER_TICK.register(new IronTrimEffect());
        IronTrimEffect.register();
        ServerTickEvents.END_WORLD_TICK.register(AmethystVisionEffect::tick);
        ModCriteria.init();
        ConfigManager.load();
    }
}