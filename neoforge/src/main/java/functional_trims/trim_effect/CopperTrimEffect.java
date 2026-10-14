package functional_trims.trim_effect;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.effect.ModEffects;
import functional_trims.func.TrimHelper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.equipment.trim.TrimMaterials;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CopperTrimEffect {

    private static final int LIGHTNING_COOLDOWN_TICKS =
            200;

    private static int chargedDurationTicks() {

        return Math.max(
                0,
                Math.round(
                        ConfigManager.get()
                                .copper
                                .chargedEffectDuration
                                * 20.0F
                )
        );
    }

    private static final Map<UUID, Integer>
            COOLDOWNS = new HashMap<>();

    private CopperTrimEffect() {
    }

    private static float lightningCooldownMultiplier() {

        return ConfigManager.get()
                .copper
                .lightningStrikeChanceMultiplier;
    }

    public static void register() {

        NeoForge.EVENT_BUS.addListener(
                CopperTrimEffect::onLevelTick
        );
    }

    private static void onLevelTick(
            LevelTickEvent.Post event) {

        if (event.getLevel()
                instanceof ServerLevel level) {

            tick(level);
        }
    }

    private static void tick(
            ServerLevel level) {

        if (!level.isThundering()) {
            return;
        }

        if (!FTConfig.isTrimEnabled("copper")) {
            return;
        }

        for (ServerPlayer player :
                level.players()) {

            if (!TrimHelper.hasFullTrim(
                    player,
                    TrimMaterials.COPPER)) {

                continue;
            }

            if (!level.canSeeSky(
                    player.blockPosition())) {

                continue;
            }

            UUID id =
                    player.getUUID();

            COOLDOWNS.putIfAbsent(id, 0);

            int cooldown =
                    COOLDOWNS.get(id);

            if (cooldown > 0) {

                COOLDOWNS.put(
                        id,
                        cooldown - 1
                );

                continue;
            }

            int strikeInterval =
                    Math.max(
                            1,
                            (int) (
                                    200
                                            / lightningCooldownMultiplier()
                            )
                    );

            if (level.getRandom()
                    .nextInt(strikeInterval) == 0) {

                summonLightning(
                        level,
                        player
                );

                COOLDOWNS.put(
                        id,
                        LIGHTNING_COOLDOWN_TICKS
                );
            }
        }
    }

    private static void summonLightning(
            ServerLevel level,
            ServerPlayer player) {

        if (!FTConfig.isTrimEnabled("copper")) {
            return;
        }

        LightningBolt lightning =
                new LightningBolt(
                        EntityType.LIGHTNING_BOLT,
                        level
                );

        lightning.setPos(
                player.getX(),
                player.getY(),
                player.getZ()
        );

        lightning.setVisualOnly(true);

        level.addFreshEntity(lightning);

        ModCriteria.TRIM_TRIGGER
                .get()
                .trigger(
                        player,
                        "copper",
                        "struck_by_lightning"
                );

        level.getServer().execute(() -> {

            player.addEffect(
                    new MobEffectInstance(
                            ModEffects.CHARGED,
                            chargedDurationTicks(),
                            0,
                            false,
                            false,
                            true
                    )
            );

            level.playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER,
                    SoundSource.PLAYERS,
                    2.0F,
                    1.0F
            );
        });
    }
}