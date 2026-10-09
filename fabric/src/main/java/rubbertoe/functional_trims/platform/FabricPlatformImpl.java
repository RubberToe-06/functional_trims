package rubbertoe.functional_trims.platform;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricPlatformImpl implements Platform {
    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
