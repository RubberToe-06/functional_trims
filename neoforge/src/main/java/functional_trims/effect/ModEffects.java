package functional_trims.effect;

import functional_trims.FunctionalTrims;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, FunctionalTrims.MOD_ID);

    public static final DeferredHolder<MobEffect, AmethystVisionEffect> AMETHYST_VISION =
            EFFECTS.register("amethyst_vision", AmethystVisionEffect::new);

    public static final DeferredHolder<MobEffect, ChargedEffect> CHARGED =
            EFFECTS.register("charged", ChargedEffect::new);

    private ModEffects() {
    }

    public static void register(IEventBus modEventBus) {
        EFFECTS.register(modEventBus);
        FunctionalTrims.LOGGER.info("Registered Functional Trims effects.");
    }
}