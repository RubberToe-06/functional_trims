package functional_trims.event;

import functional_trims.config.FTConfig;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RedstoneTrimPowerTicker {

    private static final Map<BlockPos, Integer> poweredBlocks = new HashMap<>();

    // How long (in ticks) a block stays powered after player leaves
    private static final int GRACE_TICKS = 3;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(RedstoneTrimPowerTicker::onWorldTick);
    }

    private static void onWorldTick(ServerWorld world) {
        if (!FTConfig.isTrimEnabled("redstone")) return;

        // Mark blocks that should remain powered this tick
        Set<BlockPos> activeThisTick = new HashSet<>();

        world.getPlayers().forEach(player -> {
            int trims = TrimHelper.countTrim(player, ArmorTrimMaterials.REDSTONE);
            if (trims <= 3) return;

            BlockPos below = player.getBlockPos().down();
            if (!world.getChunkManager().isChunkLoaded(below.getX() >> 4, below.getZ() >> 4)) return;

            activeThisTick.add(below);
        });

        // Decrease timers & collect blocks to turn off
        Set<BlockPos> toRemove = new HashSet<>();

        poweredBlocks.replaceAll((pos, timer) -> timer - 1);

        for (Map.Entry<BlockPos, Integer> entry : poweredBlocks.entrySet()) {
            BlockPos pos = entry.getKey();
            int timer = entry.getValue();

            // Refresh timer if still active
            if (activeThisTick.contains(pos)) {
                poweredBlocks.put(pos, GRACE_TICKS);
            }
            // Turn off if expired
            else if (timer <= 0) {
                refreshNeighbors(world, pos);
                toRemove.add(pos);
            }
        }

        // Remove expired entries
        toRemove.forEach(poweredBlocks::remove);

        // Turn on new blocks
        for (BlockPos pos : activeThisTick) {
            if (!poweredBlocks.containsKey(pos)) {
                poweredBlocks.put(pos, GRACE_TICKS);
                refreshNeighbors(world, pos);
            }
        }
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