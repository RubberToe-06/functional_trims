package functional_trims.event;

import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class RedstoneTrimPowerTicker {

    private static final Map<ResourceKey<Level>, Set<BlockPos>>
            PREVIOUS_POWERED = new HashMap<>();

    private RedstoneTrimPowerTicker() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(
                RedstoneTrimPowerTicker::onLevelTick
        );
    }

    private static void onLevelTick(LevelTickEvent.Post event) {

        if (event.getLevel() instanceof ServerLevel level) {
            onWorldTick(level);
        }
    }

    public static Set<BlockPos> getPoweredBlocksUnderPlayer(
            Player player) {

        Set<BlockPos> positions = new HashSet<>();

        if (TrimHelper.countTrim(
                player,
                TrimMaterials.REDSTONE) < 4) {

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

                positions.add(
                        new BlockPos(x, y, z)
                );
            }
        }

        return positions;
    }

    public static boolean isPlayerPoweringPos(
            Player player,
            BlockPos pos) {

        if (TrimHelper.countTrim(
                player,
                TrimMaterials.REDSTONE) < 4) {

            return false;
        }

        var box = player.getBoundingBox();

        int y = (int) Math.floor(box.minY - 0.05);

        if (pos.getY() != y) {
            return false;
        }

        int minX = (int) Math.floor(box.minX);
        int maxX = (int) Math.floor(box.maxX - 1.0E-6);
        int minZ = (int) Math.floor(box.minZ);
        int maxZ = (int) Math.floor(box.maxZ - 1.0E-6);

        return pos.getX() >= minX
                && pos.getX() <= maxX
                && pos.getZ() >= minZ
                && pos.getZ() <= maxZ;
    }

    private static void onWorldTick(ServerLevel level) {

        if (!FTConfig.isTrimEnabled("redstone")) {
            return;
        }

        ResourceKey<Level> levelKey =
                level.dimension();

        Set<BlockPos> currentPowered =
                new HashSet<>();

        Set<BlockPos> newlyPowered =
                new HashSet<>();

        for (ServerPlayer player : level.players()) {

            if (TrimHelper.countTrim(
                    player,
                    TrimMaterials.REDSTONE) < 4) {

                continue;
            }

            for (BlockPos pos :
                    getPoweredBlocksUnderPlayer(player)) {

                if (!level.getChunkSource().hasChunk(
                        pos.getX() >> 4,
                        pos.getZ() >> 4)) {

                    continue;
                }

                boolean added =
                        currentPowered.add(pos);

                if (added) {
                    newlyPowered.add(pos);
                }
            }
        }

        Set<BlockPos> previousPowered =
                PREVIOUS_POWERED.getOrDefault(
                        levelKey,
                        Set.of()
                );

        Set<BlockPos> changed =
                new HashSet<>(previousPowered);

        changed.addAll(currentPowered);

        for (BlockPos pos : changed) {

            boolean wasPowered =
                    previousPowered.contains(pos);

            boolean isPowered =
                    currentPowered.contains(pos);

            if (wasPowered != isPowered) {
                refreshNeighbors(level, pos);
            }
        }

        for (ServerPlayer player : level.players()) {

            if (TrimHelper.countTrim(
                    player,
                    TrimMaterials.REDSTONE) < 4) {

                continue;
            }

            for (BlockPos pos :
                    getPoweredBlocksUnderPlayer(player)) {

                if (!level.getChunkSource().hasChunk(
                        pos.getX() >> 4,
                        pos.getZ() >> 4)) {

                    continue;
                }

                if (newlyPowered.contains(pos)
                        && !previousPowered.contains(pos)
                        && level.getBlockState(pos)
                        .is(Blocks.REDSTONE_LAMP)) {

                    ModCriteria.TRIM_TRIGGER
                            .get()
                            .trigger(
                                    player,
                                    "redstone",
                                    "activate_lamp"
                            );
                }
            }
        }

        PREVIOUS_POWERED.put(
                levelKey,
                currentPowered
        );
    }

    private static void refreshNeighbors(
            ServerLevel level,
            BlockPos pos) {

        var block =
                level.getBlockState(pos).getBlock();

        level.updateNeighborsAt(
                pos,
                block,
                null
        );

        for (Direction direction :
                Direction.values()) {

            BlockPos neighbor =
                    pos.relative(direction);

            if (level.getChunkSource().hasChunk(
                    neighbor.getX() >> 4,
                    neighbor.getZ() >> 4)) {

                level.updateNeighborsAt(
                        neighbor,
                        block,
                        null
                );
            }
        }
    }
}