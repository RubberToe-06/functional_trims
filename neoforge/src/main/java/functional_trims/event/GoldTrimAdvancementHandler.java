package functional_trims.event;

import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class GoldTrimAdvancementHandler {

    private static final ResourceKey<Structure> BASTION_KEY =
            ResourceKey.create(
                    Registries.STRUCTURE,
                    Identifier.fromNamespaceAndPath(
                            "minecraft",
                            "bastion_remnant"
                    )
            );

    private GoldTrimAdvancementHandler() {
    }

    public static void register() {

        NeoForge.EVENT_BUS.addListener(
                GoldTrimAdvancementHandler::onLevelTick
        );

        NeoForge.EVENT_BUS.addListener(
                GoldTrimAdvancementHandler::onAttackEntity
        );
    }

    private static void onLevelTick(
            LevelTickEvent.Post event) {

        if (!(event.getLevel()
                instanceof ServerLevel world)) {
            return;
        }

        Registry<Structure> structureRegistry =
                world.registryAccess()
                        .lookupOrThrow(
                                Registries.STRUCTURE
                        );

        Structure bastion =
                structureRegistry.getValueOrThrow(
                        BASTION_KEY
                );

        for (ServerPlayer player :
                world.players()) {

            if (TrimHelper.countTrim(
                    player,
                    TrimMaterials.GOLD) != 4) {
                continue;
            }

            StructureStart start =
                    world.structureManager()
                            .getStructureAt(
                                    player.blockPosition(),
                                    bastion
                            );

            if (start.isValid()) {

                ModCriteria.TRIM_TRIGGER
                        .get()
                        .trigger(
                                player,
                                "gold",
                                "enter_bastion"
                        );
            }
        }
    }

    private static void onAttackEntity(
            AttackEntityEvent event) {

        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        if (TrimHelper.countTrim(
                player,
                TrimMaterials.GOLD) != 4) {
            return;
        }

        if (!(event.getTarget()
                instanceof PiglinBrute)) {
            return;
        }

        ModCriteria.TRIM_TRIGGER
                .get()
                .trigger(
                        player,
                        "gold",
                        "attack_piglin_brute"
                );
    }
}