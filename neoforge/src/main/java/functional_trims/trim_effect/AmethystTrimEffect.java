package functional_trims.trim_effect;

import functional_trims.config.ConfigManager;
import functional_trims.effect.AmethystVisionEffect;
import functional_trims.effect.ModEffects;
import functional_trims.func.TrimHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AmethystTrimEffect {

    private static final int STAND_STILL_TICKS =
            (int) (ConfigManager.get()
                    .amethyst
                    .motionlessSecondsBeforeEffectStanding * 20.0F);

    private static final int CROUCH_TICKS =
            (int) (ConfigManager.get()
                    .amethyst
                    .motionlessSecondsBeforeEffectSneaking * 20.0F);

    private static final int EFFECT_DURATION = -1;

    private static final double MOVEMENT_THRESHOLD_SQ =
            0.0001;

    private static final Map<UUID, PlayerData>
            PLAYER_DATA = new HashMap<>();

    private static class PlayerData {
        Vec3 lastPos = Vec3.ZERO;
        int stillTicks = 0;
        int crouchTicks = 0;
    }

    private AmethystTrimEffect() {
    }

    public static void register() {

        NeoForge.EVENT_BUS.addListener(
                AmethystTrimEffect::onServerTick
        );

        NeoForge.EVENT_BUS.addListener(
                AmethystTrimEffect::onLevelTick
        );
    }

    private static void onServerTick(
            ServerTickEvent.Post event) {

        for (ServerPlayer player :
                event.getServer()
                        .getPlayerList()
                        .getPlayers()) {

            if (!ConfigManager.get().modEnabled
                    || !ConfigManager.get().amethyst.enabled) {

                player.removeEffect(
                        ModEffects.AMETHYST_VISION
                );

                PlayerData data =
                        PLAYER_DATA.get(player.getUUID());

                if (data != null) {
                    data.stillTicks = 0;
                    data.crouchTicks = 0;
                }

                continue;
            }

            boolean hasFullSet =
                    TrimHelper.countTrim(
                            player,
                            TrimMaterials.AMETHYST
                    ) == 4;

            PlayerData data =
                    PLAYER_DATA.computeIfAbsent(
                            player.getUUID(),
                            uuid -> new PlayerData()
                    );

            Vec3 currentPos =
                    player.position();

            double distSq =
                    currentPos.distanceToSqr(
                            data.lastPos
                    );

            boolean isMoving =
                    distSq > MOVEMENT_THRESHOLD_SQ;

            if (!hasFullSet) {

                player.removeEffect(
                        ModEffects.AMETHYST_VISION
                );

                data.stillTicks = 0;
                data.crouchTicks = 0;
                data.lastPos = currentPos;

                continue;
            }

            if (!isMoving) {
                data.stillTicks++;
            } else {
                data.stillTicks = 0;
            }

            if (player.isShiftKeyDown()) {
                data.crouchTicks++;
            } else {
                data.crouchTicks = 0;
            }

            if ((data.stillTicks >= STAND_STILL_TICKS
                    || data.crouchTicks >= CROUCH_TICKS)
                    && !player.hasEffect(
                    ModEffects.AMETHYST_VISION)) {

                player.addEffect(
                        new MobEffectInstance(
                                ModEffects.AMETHYST_VISION,
                                EFFECT_DURATION,
                                0,
                                true,
                                false,
                                true
                        )
                );

                data.stillTicks = 0;
                data.crouchTicks = 0;
            }

            if (isMoving
                    && player.hasEffect(
                    ModEffects.AMETHYST_VISION)) {

                player.removeEffect(
                        ModEffects.AMETHYST_VISION
                );
            }

            data.lastPos = currentPos;
        }
    }

    private static void onLevelTick(
            LevelTickEvent.Post event) {

        if (event.getLevel()
                instanceof ServerLevel level) {

            AmethystVisionEffect.tick(level);
        }
    }
}