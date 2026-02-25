package functional_trims.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import functional_trims.FunctionalTrims;
import functional_trims.func.TrimHelper;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;

import java.util.Map;

import static functional_trims.config.FTConfig.isTrimEnabled;

public class TrimAdvancementHandler {
    public static void register() {
        // Runs once every server tick
        ServerTickEvents.END_SERVER_TICK.register(TrimAdvancementHandler::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            checkFullSet(player);
        }
    }

    private static void checkFullSet(ServerPlayerEntity player) {
        Map<RegistryKey<ArmorTrimMaterial>, String> trimToAdvancement = Map.ofEntries(
                Map.entry(ArmorTrimMaterials.REDSTONE, "full_redstone_trim"),
                Map.entry(ArmorTrimMaterials.EMERALD, "full_emerald_trim"),
                Map.entry(ArmorTrimMaterials.LAPIS, "full_lapis_trim"),
                Map.entry(ArmorTrimMaterials.GOLD, "full_gold_trim"),
                Map.entry(ArmorTrimMaterials.DIAMOND, "full_diamond_trim"),
                Map.entry(ArmorTrimMaterials.NETHERITE, "full_netherite_trim"),
                Map.entry(ArmorTrimMaterials.IRON, "full_iron_trim"),
                Map.entry(ArmorTrimMaterials.COPPER, "full_copper_trim"),
                Map.entry(ArmorTrimMaterials.AMETHYST, "full_amethyst_trim"),
                Map.entry(ArmorTrimMaterials.QUARTZ, "full_quartz_trim"),
                Map.entry(ArmorTrimMaterials.RESIN, "full_resin_trim")
        );

        for (var entry : trimToAdvancement.entrySet()) {
            RegistryKey<ArmorTrimMaterial> materialKey = entry.getKey();
            String advancementId = entry.getValue();

            if (TrimHelper.countTrim(player, materialKey) == 4
                    && isTrimEnabled(materialKey)) {
                grantAdvancement(player, Identifier.of(FunctionalTrims.MOD_ID, advancementId));
            }
        }
    }

    private static boolean isTrimEnabled(RegistryKey<ArmorTrimMaterial> key) {
        var cfg = functional_trims.config.ConfigManager.get();

        if (!cfg.enableAll) return false;

        if (key == ArmorTrimMaterials.AMETHYST) return cfg.amethystEnabled;
        if (key == ArmorTrimMaterials.IRON) return cfg.ironEnabled;
        if (key == ArmorTrimMaterials.GOLD) return cfg.goldEnabled;
        if (key == ArmorTrimMaterials.DIAMOND) return cfg.diamondEnabled;
        if (key == ArmorTrimMaterials.NETHERITE) return cfg.netheriteEnabled;
        if (key == ArmorTrimMaterials.REDSTONE) return cfg.redstoneEnabled;
        if (key == ArmorTrimMaterials.EMERALD) return cfg.emeraldEnabled;
        if (key == ArmorTrimMaterials.LAPIS) return cfg.lapisEnabled;
        if (key == ArmorTrimMaterials.COPPER) return cfg.copperEnabled;
        if (key == ArmorTrimMaterials.QUARTZ) return cfg.quartzEnabled;
        if (key == ArmorTrimMaterials.RESIN) return cfg.resinEnabled;

        return true;
    }


    private static void grantAdvancement(ServerPlayerEntity player, Identifier id) {
        AdvancementEntry adv = player.getEntityWorld().getServer().getAdvancementLoader().get(id);
        if (adv != null && !player.getAdvancementTracker().getProgress(adv).isDone()) {
            player.getAdvancementTracker().grantCriterion(adv, "auto");
        }
    }
}
