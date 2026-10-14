package functional_trims.event;

import functional_trims.FunctionalTrims;
import functional_trims.func.TrimHelper;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Map;

public final class TrimAdvancementHandler {

    private TrimAdvancementHandler() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(
                TrimAdvancementHandler::onServerTick
        );
    }

    private static void onServerTick(
            ServerTickEvent.Post event) {

        MinecraftServer server =
                event.getServer();

        for (ServerPlayer player :
                server.getPlayerList().getPlayers()) {

            checkFullSet(player);
        }
    }

    private static void checkFullSet(
            ServerPlayer player) {

        Map<ResourceKey<TrimMaterial>, String>
                trimToAdvancement =
                Map.ofEntries(

                        Map.entry(
                                TrimMaterials.REDSTONE,
                                "full_redstone_trim"
                        ),

                        Map.entry(
                                TrimMaterials.EMERALD,
                                "full_emerald_trim"
                        ),

                        Map.entry(
                                TrimMaterials.LAPIS,
                                "full_lapis_trim"
                        ),

                        Map.entry(
                                TrimMaterials.GOLD,
                                "full_gold_trim"
                        ),

                        Map.entry(
                                TrimMaterials.DIAMOND,
                                "full_diamond_trim"
                        ),

                        Map.entry(
                                TrimMaterials.NETHERITE,
                                "full_netherite_trim"
                        ),

                        Map.entry(
                                TrimMaterials.IRON,
                                "full_iron_trim"
                        ),

                        Map.entry(
                                TrimMaterials.COPPER,
                                "full_copper_trim"
                        ),

                        Map.entry(
                                TrimMaterials.AMETHYST,
                                "full_amethyst_trim"
                        ),

                        Map.entry(
                                TrimMaterials.QUARTZ,
                                "full_quartz_trim"
                        ),

                        Map.entry(
                                TrimMaterials.RESIN,
                                "full_resin_trim"
                        )
                );

        for (var entry :
                trimToAdvancement.entrySet()) {

            ResourceKey<TrimMaterial> materialKey =
                    entry.getKey();

            String advancementId =
                    entry.getValue();

            if (TrimHelper.countTrim(
                    player,
                    materialKey) == 4
                    && isTrimEnabled(materialKey)) {

                grantAdvancement(
                        player,
                        Identifier.fromNamespaceAndPath(
                                FunctionalTrims.MOD_ID,
                                advancementId
                        )
                );
            }
        }
    }

    private static boolean isTrimEnabled(
            ResourceKey<TrimMaterial> key) {

        var cfg =
                functional_trims.config
                        .ConfigManager.get();

        if (!cfg.modEnabled) {
            return false;
        }

        if (key == TrimMaterials.AMETHYST) {
            return cfg.amethyst.enabled;
        }

        if (key == TrimMaterials.IRON) {
            return cfg.iron.enabled;
        }

        if (key == TrimMaterials.GOLD) {
            return cfg.gold.enabled;
        }

        if (key == TrimMaterials.DIAMOND) {
            return cfg.diamond.enabled;
        }

        if (key == TrimMaterials.NETHERITE) {
            return cfg.netherite.enabled;
        }

        if (key == TrimMaterials.REDSTONE) {
            return cfg.redstone.enabled;
        }

        if (key == TrimMaterials.EMERALD) {
            return cfg.emerald.enabled;
        }

        if (key == TrimMaterials.LAPIS) {
            return cfg.lapis.enabled;
        }

        if (key == TrimMaterials.COPPER) {
            return cfg.copper.enabled;
        }

        if (key == TrimMaterials.QUARTZ) {
            return cfg.quartz.enabled;
        }

        if (key == TrimMaterials.RESIN) {
            return cfg.resin.enabled;
        }

        return true;
    }

    private static void grantAdvancement(
            ServerPlayer player,
            Identifier id) {

        AdvancementHolder advancement =
                player.level()
                        .getServer()
                        .getAdvancements()
                        .get(id);

        if (advancement != null
                && !player.getAdvancements()
                .getOrStartProgress(advancement)
                .isDone()) {

            player.getAdvancements()
                    .award(
                            advancement,
                            "auto"
                    );
        }
    }
}