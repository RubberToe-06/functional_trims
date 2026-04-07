package functional_trims.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import functional_trims.FunctionalTrims;
import functional_trims.config.FTConfig;
import functional_trims.func.TrimHelper;
import java.util.Map;
import java.util.Objects;

public class TrimAdvancementHandler {

    // Built once at class-load; was previously re-allocated every tick per player.
    private static final Map<ResourceKey<TrimMaterial>, String> TRIM_TO_ADVANCEMENT = Map.ofEntries(
            Map.entry(TrimMaterials.REDSTONE,  "full_redstone_trim"),
            Map.entry(TrimMaterials.EMERALD,   "full_emerald_trim"),
            Map.entry(TrimMaterials.LAPIS,     "full_lapis_trim"),
            Map.entry(TrimMaterials.GOLD,      "full_gold_trim"),
            Map.entry(TrimMaterials.DIAMOND,   "full_diamond_trim"),
            Map.entry(TrimMaterials.NETHERITE, "full_netherite_trim"),
            Map.entry(TrimMaterials.IRON,      "full_iron_trim"),
            Map.entry(TrimMaterials.COPPER,    "full_copper_trim"),
            Map.entry(TrimMaterials.AMETHYST,  "full_amethyst_trim"),
            Map.entry(TrimMaterials.QUARTZ,    "full_quartz_trim"),
            Map.entry(TrimMaterials.RESIN,     "full_resin_trim")
    );

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(TrimAdvancementHandler::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            checkFullSet(player);
        }
    }

    private static void checkFullSet(ServerPlayer player) {
        for (var entry : TRIM_TO_ADVANCEMENT.entrySet()) {
            ResourceKey<TrimMaterial> materialKey = entry.getKey();
            // Delegate to FTConfig using the key's path ("iron", "gold", …) as the canonical check.
            if (TrimHelper.countTrim(player, materialKey) == 4
                    && FTConfig.isTrimEnabled(materialKey.identifier().getPath())) {
                grantAdvancement(player, Identifier.fromNamespaceAndPath(FunctionalTrims.MOD_ID, entry.getValue()));
            }
        }
    }


    private static void grantAdvancement(ServerPlayer player, Identifier id) {
        AdvancementHolder adv = Objects.requireNonNull(player.level().getServer()).getAdvancements().get(id);
        if (adv != null && !player.getAdvancements().getOrStartProgress(adv).isDone()) {
            player.getAdvancements().award(adv, "auto");
        }
    }
}