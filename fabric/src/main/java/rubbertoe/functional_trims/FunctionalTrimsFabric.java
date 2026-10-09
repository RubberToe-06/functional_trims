package rubbertoe.functional_trims;

import net.fabricmc.api.ModInitializer;
import rubbertoe.functional_trims.criteria.ModCriteria;

public class FunctionalTrimsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ModCriteria.register();
        FunctionalTrimsCommon.init();
    }
}
