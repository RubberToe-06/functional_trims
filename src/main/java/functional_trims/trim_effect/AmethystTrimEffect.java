package functional_trims.trim_effect;

import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AmethystTrimEffect implements ServerTickEvents.EndTick {

    private static final int STAND_STILL_TICKS = 60; // 3 seconds
    private static final int CROUCH_TICKS = 30;      // 1.5 seconds
    private static final int EFFECT_DURATION = -1;   // infinite
    private static final double MOVEMENT_THRESHOLD_SQ = 0.0001;

    private static final Map<UUID, PlayerData> PLAYER_DATA = new HashMap<>();

    private static class PlayerData {
        Vec3d lastPos = Vec3d.ZERO;
        int stillTicks = 0;
        int crouchTicks = 0;
    }

    @Override
    public void onEndTick(net.minecraft.server.MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            boolean hasFullSet = TrimHelper.countTrim(player, ArmorTrimMaterials.AMETHYST) == 4;

            PlayerData data = PLAYER_DATA.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());
            Vec3d currentPos = player.getEntityPos();
            double distSq = currentPos.squaredDistanceTo(data.lastPos);

            boolean isMoving = distSq > MOVEMENT_THRESHOLD_SQ;

            if (!hasFullSet) {
                // Remove effect if armor taken off
                player.removeStatusEffect(ModEffects.AMETHYST_VISION);
                data.stillTicks = 0;
                data.crouchTicks = 0;
                data.lastPos = currentPos;
                continue;
            }

            if (!isMoving) data.stillTicks++;
            else data.stillTicks = 0;

            if (player.isSneaking()) data.crouchTicks++;
            else data.crouchTicks = 0;

            // Apply effect if stationary or crouching long enough
            if ((data.stillTicks >= STAND_STILL_TICKS || data.crouchTicks >= CROUCH_TICKS)
                    && !player.hasStatusEffect(ModEffects.AMETHYST_VISION)) {

                player.addStatusEffect(new StatusEffectInstance(
                        ModEffects.AMETHYST_VISION,
                        EFFECT_DURATION,
                        0,
                        true,
                        false,
                        true
                ));

                data.stillTicks = 0;
                data.crouchTicks = 0;
            }

            // Remove effect once player starts moving
            if (isMoving && player.hasStatusEffect(ModEffects.AMETHYST_VISION)) {
                player.removeStatusEffect(ModEffects.AMETHYST_VISION);
            }

            data.lastPos = currentPos;
        }
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(new AmethystTrimEffect());
    }
}
