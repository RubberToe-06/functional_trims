package functional_trims.effect;

import functional_trims.FunctionalTrims;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

public class ModEffects {

    public static Holder<MobEffect> AMETHYST_VISION;
    public static Holder<MobEffect> CHARGED;

    public static void register() {

        AMETHYST_VISION = Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                Identifier.fromNamespaceAndPath(FunctionalTrims.MOD_ID, "amethyst_vision"),
                new AmethystVisionEffect()
        );

        CHARGED = Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                Identifier.fromNamespaceAndPath(FunctionalTrims.MOD_ID, "charged"),
                new ChargedEffect()
        );

        FunctionalTrims.LOGGER.info("Registered Functional Trims effects.");
    }
}