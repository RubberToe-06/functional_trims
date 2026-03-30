package functional_trims.effect;

import functional_trims.FunctionalTrims;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {

    public static RegistryEntry<StatusEffect> AMETHYST_VISION;
    public static RegistryEntry<StatusEffect> CHARGED;

    public static void register() {

        AMETHYST_VISION = Registry.registerReference(
                Registries.STATUS_EFFECT,
                Identifier.of(FunctionalTrims.MOD_ID, "amethyst_vision"),
                new AmethystVisionEffect()
        );

        CHARGED = Registry.registerReference(
                Registries.STATUS_EFFECT,
                Identifier.of(FunctionalTrims.MOD_ID, "charged"),
                new ChargedEffect()
        );

        FunctionalTrims.LOGGER.info("Registered Functional Trims effects.");
    }
}