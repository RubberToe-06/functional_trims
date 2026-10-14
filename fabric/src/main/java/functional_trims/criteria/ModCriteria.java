package functional_trims.criteria;

import functional_trims.event.GoldTrimAttackListener;
import net.minecraft.advancement.criterion.Criteria;
import functional_trims.FunctionalTrims;

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