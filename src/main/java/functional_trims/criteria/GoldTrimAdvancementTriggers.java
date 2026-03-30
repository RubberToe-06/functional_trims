package functional_trims.criteria;

import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class GoldTrimAdvancementTriggers {
    public static void register() {
        registerBastionEntryTrigger();
        registerPiglinBruteHitTrigger();
    }

    /**
     * Fires when a player with 4× GOLD trims is inside a Bastion.
     */
    private static void registerBastionEntryTrigger() {
        ServerTickEvents.END_LEVEL_TICK.register((ServerLevel world) -> {
            for (ServerPlayer player : world.players()) {
                if (TrimHelper.countTrim(player, TrimMaterials.GOLD) != 4) continue;

                BlockPos pos = player.blockPosition();

                Registry<Structure> structureRegistry =
                        world.registryAccess().lookupOrThrow(Registries.STRUCTURE);

                Holder<Structure> bastionEntry =
                        structureRegistry.get(Identifier.fromNamespaceAndPath("minecraft", "bastion_remnant")).orElse(null);
                if (bastionEntry == null) continue;

                StructureStart start =
                        world.structureManager().getStructureAt(pos, bastionEntry.value());

                if (start.isValid()) {
                    ModCriteria.TRIM_TRIGGER.trigger(player, "gold", "enter_bastion");
                }
            }
        });
    }

    /**
     * Fires when a player with 4× GOLD trims attacks a Piglin Brute.
     */
    private static void registerPiglinBruteHitTrigger() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide()) return InteractionResult.PASS;

            if (player instanceof ServerPlayer serverPlayer) {
                if (TrimHelper.countTrim(serverPlayer, TrimMaterials.GOLD) == 4
                        && entity instanceof PiglinBrute) {
                    ModCriteria.TRIM_TRIGGER.trigger(serverPlayer, "gold", "hit_piglin_brute");
                }
            }
            return InteractionResult.PASS;
        });
    }
}