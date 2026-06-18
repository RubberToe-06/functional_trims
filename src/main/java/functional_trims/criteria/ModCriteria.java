package functional_trims.criteria;

import functional_trims.event.GoldTrimAttackListener;
import functional_trims.FunctionalTrims;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModCriteria {
    public static final TrimTriggerCriterion TRIM_TRIGGER =
            Registry.register(
                    BuiltInRegistries.TRIGGER_TYPES,
                    Identifier.fromNamespaceAndPath(FunctionalTrims.MOD_ID, "trim_trigger"),
                    new TrimTriggerCriterion()
            );

    public static void init() {
        GoldTrimAdvancementTriggers.register();
        GoldTrimAttackListener.register();
    }
}