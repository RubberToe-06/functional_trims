package rubbertoe.functional_trims.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public final class FunctionalTrimsNeoForgeConfigIntegration {
    private FunctionalTrimsNeoForgeConfigIntegration() {
    }

    public static void register(ModContainer container) {
        IConfigScreenFactory factory = (_, parent) -> FunctionalTrimsConfigScreen.create(parent);
        container.registerExtensionPoint(IConfigScreenFactory.class, factory);
    }
}
