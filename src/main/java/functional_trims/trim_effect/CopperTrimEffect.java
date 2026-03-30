package functional_trims.trim_effect;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.effect.ModEffects;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CopperTrimEffect implements ServerTickEvents.EndWorldTick {

    private static final int LIGHTNING_COOLDOWN_TICKS = 200; // 10s between possible strikes
    private static final int CHARGED_DURATION_TICKS = 20 * 60; // 60s

    private final Map<UUID, Integer> cooldowns = new HashMap<>();

    private static float lightningCooldownMultiplier() {
        return ConfigManager.get().copper.lightningStrikeChanceMultiplier;
    }

    @Override
    public void onEndTick(ServerLevel world) {
        if (!world.isThundering()) return;
        if (!FTConfig.isTrimEnabled("copper")) return;

        for (ServerPlayer player : world.players()) {
            if (!TrimHelper.hasFullTrim(player, TrimMaterials.COPPER)) continue;
            if (!world.canSeeSky(player.blockPosition())) continue;

            UUID id = player.getUUID();
            cooldowns.putIfAbsent(id, 0);

            int cooldown = cooldowns.get(id);
            if (cooldown > 0) {
                cooldowns.put(id, cooldown - 1);
                continue;
            }

            int strikeInterval = Math.max(1, (int) (200 / lightningCooldownMultiplier()));
            if (world.getRandom().nextInt(strikeInterval) == 0) {
                summonLightning(world, player);
                cooldowns.put(id, LIGHTNING_COOLDOWN_TICKS);
            }
        }
    }

    private void summonLightning(ServerLevel world, ServerPlayer player) {
        if (!FTConfig.isTrimEnabled("copper")) return;

        LightningBolt lightning = new LightningBolt(net.minecraft.world.entity.EntityType.LIGHTNING_BOLT, world);
        lightning.snapTo(player.getX(), player.getY(), player.getZ());
        lightning.setVisualOnly(true);
        world.addFreshEntity(lightning);

        ModCriteria.TRIM_TRIGGER.trigger(player, "copper", "struck_by_lightning");

        world.getServer().execute(() -> {
            player.addEffect(new MobEffectInstance(
                    ModEffects.CHARGED,
                    CHARGED_DURATION_TICKS,
                    0,
                    false,
                    false,
                    true
            ));
            player.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 2.0F, 1.0F);
        });
    }
}