package functional_trims.criteria;

import functional_trims.FunctionalTrims;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModCriteria {

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES =
            DeferredRegister.create(
                    Registries.TRIGGER_TYPE,
                    FunctionalTrims.MOD_ID
            );

    public static final Supplier<TrimTriggerCriterion> TRIM_TRIGGER =
            TRIGGER_TYPES.register(
                    "trim_trigger",
                    TrimTriggerCriterion::new
            );

    private ModCriteria() {
    }

    public static void register(IEventBus modEventBus) {
        TRIGGER_TYPES.register(modEventBus);
    }
}