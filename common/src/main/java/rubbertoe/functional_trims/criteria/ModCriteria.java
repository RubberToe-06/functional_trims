package rubbertoe.functional_trims.criteria;

import rubbertoe.functional_trims.event.GoldTrimAttackListener;
import rubbertoe.functional_trims.FunctionalTrimsCommon;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModCriteria {
    public static final Identifier TRIM_TRIGGER_ID =
            Identifier.fromNamespaceAndPath(FunctionalTrimsCommon.MOD_ID, "trim_trigger");

    public static final TrimTriggerCriterion TRIM_TRIGGER = new TrimTriggerCriterion();

    /**
     * On Fabric, {@code BuiltInRegistries.TRIGGER_TYPES} is still open when mods initialize, so this
     * can be called directly. NeoForge freezes it before mod construction runs - register there via
     * a {@code RegisterEvent} listener instead (see FunctionalTrimsNeoForge).
     */
    public static void register() {
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, TRIM_TRIGGER_ID, TRIM_TRIGGER);
    }

    public static void init() {
        GoldTrimAdvancementTriggers.register();
        GoldTrimAttackListener.register();
    }
}
