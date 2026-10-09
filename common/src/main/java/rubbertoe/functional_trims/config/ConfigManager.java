package rubbertoe.functional_trims.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rubbertoe.functional_trims.FunctionalTrimsCommon;
import rubbertoe.functional_trims.platform.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Path CONFIG_PATH = Services.PLATFORM
            .getConfigDir()
            .resolve("functionaltrims.json");

    private static FunctionalTrimsConfig config = new FunctionalTrimsConfig();

    private ConfigManager() {
    }

    public static FunctionalTrimsConfig get() {
        return config;
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            config = new FunctionalTrimsConfig();
            save();
            return;
        }

        try {
            String json = Files.readString(CONFIG_PATH);
            FunctionalTrimsConfig loaded = GSON.fromJson(json, FunctionalTrimsConfig.class);
            config = loaded != null ? loaded : new FunctionalTrimsConfig();
        } catch (Exception e) {
            FunctionalTrimsCommon.LOGGER.error("Failed to load config. Using defaults.", e);
            config = new FunctionalTrimsConfig();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(config));
        } catch (IOException e) {
            FunctionalTrimsCommon.LOGGER.error("Failed to save config.", e);
        }
    }
}