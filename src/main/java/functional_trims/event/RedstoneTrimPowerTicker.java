package functional_trims.event;

import functional_trims.config.FTConfig;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

public class RedstoneTrimPowerTicker {

    private static final Map<UUID, BlockPos> lastPositions = new HashMap<>();
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
            if (!world.isChunkLoaded(below)) return;

            activeThisTick.add(below);
            lastPositions.put(player.getUuid(), below);
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
                refreshNeighbors(world, pos, false);
                toRemove.add(pos);
            }
        }

        // Remove expired entries
        toRemove.forEach(poweredBlocks::remove);

        // Turn on new blocks
        for (BlockPos pos : activeThisTick) {
            if (!poweredBlocks.containsKey(pos)) {
                poweredBlocks.put(pos, GRACE_TICKS);
                refreshNeighbors(world, pos, true);
            }
        }
    }

    private static void refreshNeighbors(ServerWorld world, BlockPos pos, boolean on) {
        var block = world.getBlockState(pos).getBlock();
        world.updateNeighborsAlways(pos, block);
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.offset(dir);
            if (world.isChunkLoaded(neighbor)) {
                world.updateNeighborsAlways(neighbor, block);
            }
        }
    }
}
