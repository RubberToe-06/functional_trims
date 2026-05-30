package functional_trims.trim_effect;

import functional_trims.config.ConfigManager;
import functional_trims.effect.AmethystVisionEffect;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.phys.Vec3;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AmethystTrimEffect implements ServerTickEvents.EndTick {

    // Intentionally NOT static-final: reading from config at call time so runtime changes take effect.
    private static int standStillTicks() {
        return (int)(ConfigManager.get().amethyst.motionlessSecondsBeforeEffectStanding * 20.0f);
    }
    private static int crouchTicks() {
        return (int)(ConfigManager.get().amethyst.motionlessSecondsBeforeEffectSneaking * 20.0f);
    }

    private static final double MOVEMENT_THRESHOLD_SQ = 0.0001;
    private static final Map<UUID, PlayerData> PLAYER_DATA = new HashMap<>();

    private static class PlayerData {
        Vec3 lastPos = Vec3.ZERO;
        int stillTicks = 0;
        int crouchTicks = 0;
    }

    @Override
    public void onEndTick(net.minecraft.server.MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            UUID id = player.getUUID();

            if (!ConfigManager.get().modEnabled || !ConfigManager.get().amethyst.enabled) {
                AmethystVisionEffect.deactivate(id);
                PlayerData data = PLAYER_DATA.get(id);
                if (data != null) { data.stillTicks = 0; data.crouchTicks = 0; }
                continue;
            }

            boolean hasFullSet = TrimHelper.countTrim(player, TrimMaterials.AMETHYST) == 4;
            PlayerData data = PLAYER_DATA.computeIfAbsent(id, _ -> new PlayerData());
            Vec3 currentPos = player.position();
            boolean isMoving = currentPos.distanceToSqr(data.lastPos) > MOVEMENT_THRESHOLD_SQ;

            if (!hasFullSet) {
                AmethystVisionEffect.deactivate(id);
                data.stillTicks = 0;
                data.crouchTicks = 0;
                data.lastPos = currentPos;
                continue;
            }

            if (!isMoving) data.stillTicks++;
            else data.stillTicks = 0;

            if (player.isShiftKeyDown()) data.crouchTicks++;
            else data.crouchTicks = 0;

            if ((data.stillTicks >= standStillTicks() || data.crouchTicks >= crouchTicks())
                    && !AmethystVisionEffect.isActive(id)) {
                AmethystVisionEffect.activate(id);
                data.stillTicks = 0;
                data.crouchTicks = 0;
            }

            if (isMoving && AmethystVisionEffect.isActive(id)) {
                AmethystVisionEffect.deactivate(id);
            }

            data.lastPos = currentPos;
        }
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(new AmethystTrimEffect());
    }

    public static void cleanupPlayer(UUID id) {
        PLAYER_DATA.remove(id);
    }
}