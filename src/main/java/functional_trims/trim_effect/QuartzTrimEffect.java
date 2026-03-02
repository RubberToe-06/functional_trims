package functional_trims.trim_effect;


/**
 * Quartz Trim — "Pure Vitality"
 *
 * Full set bonus:
 *  - Positive potion effects last 25% longer.
 *  - Food restores 10% more hunger and 15% more saturation.
 *
 * Both behaviors are implemented through mixins:
 *  - LivingEntityMixin → extends beneficial potion durations.
 *  - HungerManagerMixin → boosts food & saturation values.
 */
public class QuartzTrimEffect {

    // Constants (shared if you ever want to reference these)
    public static final double POTION_DURATION_MULT = 1.25;
    public static final double FOOD_HUNGER_MULT = 1.10;
    public static final double FOOD_SAT_MULT = 1.15;

    public static void register() {
        // No event callbacks needed — behavior handled in mixins.
        // This method remains for consistency in your mod init pipeline.
    }
}
