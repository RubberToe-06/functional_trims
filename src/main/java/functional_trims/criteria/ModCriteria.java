package functional_trims.criteria;

import functional_trims.event.GoldTrimAttackListener;
import net.minecraft.advancements.CriteriaTriggers;
import functional_trims.FunctionalTrims;

public class ModCriteria {
    public static final TrimTriggerCriterion TRIM_TRIGGER =
            CriteriaTriggers.register(
                    FunctionalTrims.MOD_ID + ":trim_trigger",
                    new TrimTriggerCriterion()
            );

    public static void init() {
        GoldTrimAdvancementTriggers.register();
        GoldTrimAttackListener.register();
    }
}