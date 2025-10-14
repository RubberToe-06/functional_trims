package functional_trims.trim_effect;

import functional_trims.FunctionalTrims;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {

    public static RegistryEntry<StatusEffect> AMETHYST_VISION;
    public static RegistryEntry<StatusEffect> CHARGED;
    public static StatusEffect CHARGED_EFFECT; // <-- add this line
    public static StatusEffectInstance CHARGED_60S;

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

        CHARGED_EFFECT = CHARGED.value(); // <-- unwrap to plain StatusEffect

        CHARGED_60S = new StatusEffectInstance(CHARGED, 20 * 60, 0, true, false, true);


        FunctionalTrims.LOGGER.info("Registered Functional Trims effects.");
    }
}
