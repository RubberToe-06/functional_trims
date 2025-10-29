package functional_trims.criteria;

import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;

public class GoldTrimAdvancementTriggers {
    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(GoldTrimAdvancementTriggers::onWorldTick);
    }

    private static void onWorldTick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (TrimHelper.countTrim(player, ArmorTrimMaterials.GOLD) == 4) {

                BlockPos pos = player.getBlockPos();
                Registry<Structure> structureRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);
                RegistryEntry<Structure> bastionEntry = structureRegistry.getEntry(Identifier.ofVanilla("bastion_remnant")).orElse(null);

                if (bastionEntry == null) continue;

                var structureStart = world.getStructureAccessor().getStructureAt(pos, bastionEntry.value());

                if (structureStart != null && structureStart.hasChildren()) {
                    ModCriteria.TRIM_TRIGGER.trigger(player, "gold", "enter_bastion");
                }
            }
        }
    }
}
