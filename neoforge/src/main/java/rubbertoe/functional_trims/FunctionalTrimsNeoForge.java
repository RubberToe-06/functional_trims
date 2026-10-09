package rubbertoe.functional_trims;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.RegisterEvent;
import rubbertoe.functional_trims.config.FunctionalTrimsNeoForgeConfigIntegration;
import rubbertoe.functional_trims.criteria.ModCriteria;

@Mod(FunctionalTrimsCommon.MOD_ID)
public class FunctionalTrimsNeoForge {
    public FunctionalTrimsNeoForge(IEventBus modEventBus, ModContainer container) {
        // BuiltInRegistries.TRIGGER_TYPES is already frozen by the time this constructor runs,
        // unlike on Fabric - register through RegisterEvent instead, which NeoForge fires for
        // every builtin registry (including vanilla ones) before it freezes.
        modEventBus.addListener(RegisterEvent.class, event ->
                event.register(Registries.TRIGGER_TYPE, helper ->
                        helper.register(ModCriteria.TRIM_TRIGGER_ID, ModCriteria.TRIM_TRIGGER))
        );

        FunctionalTrimsCommon.init();
        // The config screen touches client-only classes and Cloth Config, which a dedicated server
        // (or a client without Cloth Config) doesn't have, so only wire it up when both are available.
        if (FMLEnvironment.getDist() == Dist.CLIENT && ModList.get().isLoaded("cloth_config")) {
            FunctionalTrimsNeoForgeConfigIntegration.register(container);
        }
    }
}
