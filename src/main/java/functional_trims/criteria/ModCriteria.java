package functional_trims.criteria;

import functional_trims.event.GoldTrimAttackListener;
import net.minecraft.advancement.criterion.Criteria;
import functional_trims.FunctionalTrims;

/**
 * Central registry for custom advancement criteria.
 *
 * IMPORTANT: Criteria.register takes a String in 1.21.x, not an Identifier.
 * Passing "functional_trims:trim_trigger" keeps your namespace. :contentReference[oaicite:23]{index=23}
 */
public class ModCriteria {
    public static final TrimTriggerCriterion TRIM_TRIGGER =
            Criteria.register(
                    FunctionalTrims.MOD_ID + ":trim_trigger",
                    new TrimTriggerCriterion()
            );

    public static void init() {
        GoldTrimAdvancementTriggers.register();
        GoldTrimAttackListener.register();
    }
}
