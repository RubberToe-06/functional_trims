package rubbertoe.functional_trims.platform;

import java.util.ServiceLoader;

public final class Services {
    public static final Platform PLATFORM = load();

    private Services() {
    }

    private static Platform load() {
        return ServiceLoader.load(Platform.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Platform implementation found on the classpath"));
    }
}
