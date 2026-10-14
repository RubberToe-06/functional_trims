package functional_trims.event;

import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RedstoneTrimPowerTicker {

    private static final Map<RegistryKey<World>, Set<BlockPos>> PREVIOUS_POWERED = new HashMap<>();

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(RedstoneTrimPowerTicker::onWorldTick);
    }

    public static Set<BlockPos> getPoweredBlocksUnderPlayer(PlayerEntity player) {
        Set<BlockPos> positions = new HashSet<>();

        if (TrimHelper.countTrim(player, ArmorTrimMaterials.REDSTONE) < 4) {
            return positions;
        }

        var box = player.getBoundingBox();
        int y = (int) Math.floor(box.minY - 0.05);

        int minX = (int) Math.floor(box.minX);
        int maxX = (int) Math.floor(box.maxX - 1.0E-6);
        int minZ = (int) Math.floor(box.minZ);
        int maxZ = (int) Math.floor(box.maxZ - 1.0E-6);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                positions.add(new BlockPos(x, y, z));
            }
        }

        return positions;
    }

    public static boolean isPlayerPoweringPos(PlayerEntity player, BlockPos pos) {
        if (TrimHelper.countTrim(player, ArmorTrimMaterials.REDSTONE) < 4) return false;

        var box = player.getBoundingBox();
        int y = (int) Math.floor(box.minY - 0.05);

        if (pos.getY() != y) return false;

        int minX = (int) Math.floor(box.minX);
        int maxX = (int) Math.floor(box.maxX - 1.0E-6);
        int minZ = (int) Math.floor(box.minZ);
        int maxZ = (int) Math.floor(box.maxZ - 1.0E-6);

        return pos.getX() >= minX && pos.getX() <= maxX
                && pos.getZ() >= minZ && pos.getZ() <= maxZ;
    }

    private static void onWorldTick(ServerWorld world) {
        if (!FTConfig.isTrimEnabled("redstone")) return;

        RegistryKey<World> worldKey = world.getRegistryKey();
        Set<BlockPos> currentPowered = new HashSet<>();
        Set<BlockPos> newlyPowered = new HashSet<>();

        for (ServerPlayerEntity player : world.getPlayers()) {
            if (TrimHelper.countTrim(player, ArmorTrimMaterials.REDSTONE) < 4) continue;

            for (BlockPos pos : getPoweredBlocksUnderPlayer(player)) {
                if (!world.getChunkManager().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) continue;

                boolean added = currentPowered.add(pos);
                if (added) {
                    newlyPowered.add(pos);
                }
            }
        }

        Set<BlockPos> previousPowered = PREVIOUS_POWERED.getOrDefault(worldKey, Set.of());

        Set<BlockPos> changed = new HashSet<>(previousPowered);
        changed.addAll(currentPowered);

        for (BlockPos pos : changed) {
            boolean wasPowered = previousPowered.contains(pos);
            boolean isPowered = currentPowered.contains(pos);

            if (wasPowered != isPowered) {
                refreshNeighbors(world, pos);
            }
        }

        for (ServerPlayerEntity player : world.getPlayers()) {
            if (TrimHelper.countTrim(player, ArmorTrimMaterials.REDSTONE) < 4) continue;

            for (BlockPos pos : getPoweredBlocksUnderPlayer(player)) {
                if (!world.getChunkManager().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) continue;

                if (newlyPowered.contains(pos)
                        && !previousPowered.contains(pos)
                        && world.getBlockState(pos).isOf(Blocks.REDSTONE_LAMP)) {
                    ModCriteria.TRIM_TRIGGER.trigger(player, "redstone", "activate_lamp");
                }
            }
        }

        PREVIOUS_POWERED.put(worldKey, currentPowered);
    }

    private static void refreshNeighbors(ServerWorld world, BlockPos pos) {
        var block = world.getBlockState(pos).getBlock();
        world.updateNeighborsAlways(pos, block, null);

        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.offset(dir);
            if (world.getChunkManager().isChunkLoaded(neighbor.getX() >> 4, neighbor.getZ() >> 4)) {
                world.updateNeighborsAlways(neighbor, block, null);
            }
        }
    }
}