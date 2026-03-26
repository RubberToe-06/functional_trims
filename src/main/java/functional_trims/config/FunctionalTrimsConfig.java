package functional_trims.config;

public final class FunctionalTrimsConfig {
    public boolean modEnabled = true;

    public final Iron iron = new Iron();
    public final Gold gold = new Gold();
    public final Diamond diamond = new Diamond();
    public final Netherite netherite = new Netherite();
    public final Redstone redstone = new Redstone();
    public final Emerald emerald = new Emerald();
    public final Lapis lapis = new Lapis();
    public final Quartz quartz = new Quartz();
    public final Amethyst amethyst = new Amethyst();
    public final Copper copper = new Copper();
    public final Resin resin = new Resin();

    public static class TrimSection {
        public boolean enabled = true;
    }

    public static final class Iron extends TrimSection {
        public float projectileReflectChance = 0.6f;
        public float shieldKnockbackStrengthMultiplier = 1.15f;
        public boolean axeAttackResistanceEnabled = true;
    }

    public static final class Gold extends TrimSection {
        public boolean distractPiglinBrutesEnabled = true;
    }

    public static final class Diamond extends TrimSection {
        public float percentHealthRegainedAfterBurst = 0.5f;
        public float percentArmorDurabilityLostAfterBurst = 1.0f;
    }

    public static final class Netherite extends TrimSection {
    }

    public static final class Redstone extends TrimSection {
        public int blockPowerLevelWhenSteppedOn = 15;
    }

    public static final class Emerald extends TrimSection {
        public float percentChanceForExtraRoll1 = 1.0f;
        public float percentChanceForExtraRoll2 = 0.5f;
    }

    public static final class Lapis extends TrimSection {
        public float extraExpMultiplier = 0.5f;
    }

    public static final class Quartz extends TrimSection {
        public float hungerRestoredMultiplier = 1.25f;
        public float saturationRestoredMultiplier = 1.25f;
        public float potionEffectDurationMultiplier = 1.25f;
    }

    public static final class Amethyst extends TrimSection {
        public float motionlessSecondsBeforeEffectStanding = 3.0f;
        public float motionlessSecondsBeforeEffectSneaking = 1.5f;
        public float effectRangeMultiplier = 1.0f;
    }

    public static final class Copper extends TrimSection {
        public float lightningStrikeChanceMultiplier = 1.0f;
        public float chargedEffectDuration = 60.0f;
        public float chargedStrikeDamageMultiplier = 1.5f;
    }

    public static final class Resin extends TrimSection {
        public float gripStrengthMultiplier = 1.0f;
    }
}