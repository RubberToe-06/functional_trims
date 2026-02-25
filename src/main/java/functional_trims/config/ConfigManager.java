package functional_trims.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("functionaltrims.json");

    private static FunctionalTrimsConfig config = new FunctionalTrimsConfig();

    public static FunctionalTrimsConfig get() {
        return config;
    }

    public static void load() {
        if (!Files.exists(PATH)) {
            save(); // write defaults once
            return;
        }
        try {
            String json = Files.readString(PATH);
            FunctionalTrimsConfig loaded = GSON.fromJson(json, FunctionalTrimsConfig.class);
            if (loaded != null) config = loaded;
        } catch (Exception e) {
            // If it fails, keep defaults but don't crash the game
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(config));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
