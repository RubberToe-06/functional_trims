package functional_trims.config;

import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class FunctionalTrimsConfig {
    // Toggles for trim effects
    public boolean enableAll = true;
    public boolean ironEnabled = true;
    public boolean goldEnabled = true;
    public boolean diamondEnabled = true;
    public boolean netheriteEnabled = true;
    public boolean redstoneEnabled = true;
    public boolean emeraldEnabled = true;
    public boolean lapisEnabled = true;
    public boolean quartzEnabled = true;
    public boolean amethystEnabled = true;
    public boolean copperEnabled = true;

    //-----------------------------
    // Trim Specific Settings
    //-----------------------------

    // Iron
    public float projectileReflectChance = 0.6f;
    public float shieldKnockbackStrengthMultiplier = 1.15f;
    public boolean axeAttackResistanceEnabled = true;

    // Gold
    public boolean distractPiglinBrutesEnabled = true;

    // Diamond
    public float percentHealthRegainedAfterBurst = 0.5f;
    public float percentArmorDurabilityLostAfterBurst = 1.0f;

    // Netherite
    // nothing to configure

    // Redstone
    public int blockPowerLevelWhenSteppedOn = 15;

    // Emerald
    public float percentChanceForExtraRoll1 = 1.0f;
    public float percentChanceForExtraRoll2 = 0.5f;

    // Lapis
    public float extraExpMultiplier = 0.5f;

    // Quartz
    public float hungerRestoredMultiplier = 1.25f;
    public float saturationRestoredMultiplier = 1.25f;
    public float potionEffectDurationMultiplier = 1.25f;

    // Amethyst
    public float motionlessSecondsBeforeEffectStanding = 3.0f;
    public float motionlessSecondsBeforeEffectSneaking = 1.5f;
    public float effectRangeMultiplier = 1.0f;

    // Copper
    public float lightningStrikeChanceMultiplier = 1.0f;
    public float chargedEffectDuration = 60.0f;
    public float chargedStrikeDamageMultiplier = 1.5f;

}