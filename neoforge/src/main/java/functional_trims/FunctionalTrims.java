package functional_trims;

import functional_trims.config.ConfigManager;
import functional_trims.config.FunctionalTrimsConfigScreen;
import functional_trims.criteria.ModCriteria;
import functional_trims.effect.ModEffects;
import functional_trims.event.ChargedAttackHandler;
import functional_trims.event.GoldTrimAdvancementHandler;
import functional_trims.event.LapisTrimXpHandler;
import functional_trims.event.NetheriteExplosionHandler;
import functional_trims.event.NetheriteKnockbackHandler;
import functional_trims.event.QuartzTrimAdvancementHandler;
import functional_trims.event.RedstoneTrimPowerTicker;
import functional_trims.event.TrimAdvancementHandler;
import functional_trims.trim_effect.AmethystTrimEffect;
import functional_trims.trim_effect.CopperTrimEffect;
import functional_trims.trim_effect.DiamondTrimEffect;
import functional_trims.trim_effect.IronTrimEffect;
import functional_trims.trim_effect.ResinTrimEffect;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(FunctionalTrims.MOD_ID)
public class FunctionalTrims {

    public static final String MOD_ID = "functional_trims";

    public static final Logger LOGGER =
            LoggerFactory.getLogger(MOD_ID);

    public FunctionalTrims(
            IEventBus modEventBus,
            ModContainer modContainer,
            Dist dist) {

        ConfigManager.load();

        registerContent(modEventBus);

        registerEffects();

        registerEventHandlers();

        if (dist == Dist.CLIENT) {
            ClientOnly.registerConfigScreen(
                    modContainer
            );
        }

        LOGGER.info(
                "Functional Trims initialized."
        );
    }

    private static void registerContent(
            IEventBus modEventBus) {

        ModEffects.register(
                modEventBus
        );

        ModCriteria.register(
                modEventBus
        );
    }

    private static void registerEffects() {

        ResinTrimEffect.register();
        AmethystTrimEffect.register();
        CopperTrimEffect.register();
        IronTrimEffect.register();
        DiamondTrimEffect.register();
    }

    private static void registerEventHandlers() {

        RedstoneTrimPowerTicker.register();
        TrimAdvancementHandler.register();
        ChargedAttackHandler.register();
        LapisTrimXpHandler.register();
        NetheriteKnockbackHandler.register();
        NetheriteExplosionHandler.register();
        GoldTrimAdvancementHandler.register();
        QuartzTrimAdvancementHandler.register();
    }

    private static final class ClientOnly {

        private ClientOnly() {
        }

        private static void registerConfigScreen(
                ModContainer container) {

            container.registerExtensionPoint(
                    IConfigScreenFactory.class,
                    (ignoredContainer, parentScreen) ->
                            FunctionalTrimsConfigScreen.create(
                                    parentScreen
                            )
            );
        }
    }
}