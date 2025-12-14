package functional_trims.criteria;

import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;

import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;

public class GoldTrimAdvancementTriggers {
    public static void register() {
        registerBastionEntryTrigger();
        registerPiglinBruteHitTrigger();
    }

    /**
     * Fires when a player with 4× GOLD trims is inside a Bastion.
     */
    private static void registerBastionEntryTrigger() {
        ServerTickEvents.END_WORLD_TICK.register((ServerWorld world) -> {
            for (ServerPlayerEntity player : world.getPlayers()) {
                if (TrimHelper.countTrim(player, ArmorTrimMaterials.GOLD) != 4) continue;

                BlockPos pos = player.getBlockPos();

                // Use getOrThrow with RegistryKeys.STRUCTURE
                Registry<Structure> structureRegistry =
                        world.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);

                RegistryEntry<Structure> bastionEntry =
                        structureRegistry.getEntry(Identifier.of("minecraft", "bastion_remnant")).orElse(null);
                if (bastionEntry == null) continue;

                StructureStart start =
                        world.getStructureAccessor().getStructureAt(pos, bastionEntry.value());

                if (start != null && start.hasChildren()) {
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
            if (world.isClient()) return ActionResult.PASS;

            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (TrimHelper.countTrim(serverPlayer, ArmorTrimMaterials.GOLD) == 4
                        && entity instanceof PiglinBruteEntity) {
                    ModCriteria.TRIM_TRIGGER.trigger(serverPlayer, "gold", "hit_piglin_brute");
                }
            }

            return ActionResult.PASS;
        });
    }
}
