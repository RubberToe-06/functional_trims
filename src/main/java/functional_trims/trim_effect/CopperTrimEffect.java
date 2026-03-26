package functional_trims.trim_effect;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.effect.ModEffects;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CopperTrimEffect implements ServerTickEvents.EndWorldTick {

    private static final int LIGHTNING_COOLDOWN_TICKS = 200; // 10s between possible strikes
    private static final int CHARGED_DURATION_TICKS = 20 * 60; // 60s

    private final Map<UUID, Integer> cooldowns = new HashMap<>();

    private static float lightningCooldownMultiplier() {
        return ConfigManager.get().lightningStrikeChanceMultiplier;
    }

    @Override
    public void onEndTick(ServerWorld world) {
        if (!world.isThundering()) return;
        if (!FTConfig.isTrimEnabled("copper")) return;

        for (ServerPlayerEntity player : world.getPlayers()) {
            if (!TrimHelper.hasFullTrim(player, ArmorTrimMaterials.COPPER)) continue;
            if (!world.isSkyVisible(player.getBlockPos())) continue;

            UUID id = player.getUuid();
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

    private void summonLightning(ServerWorld world, ServerPlayerEntity player) {
        if (!FTConfig.isTrimEnabled("copper")) return;

        LightningEntity lightning = new LightningEntity(net.minecraft.entity.EntityType.LIGHTNING_BOLT, world);
        lightning.refreshPositionAfterTeleport(player.getX(), player.getY(), player.getZ());
        lightning.setCosmetic(true);
        world.spawnEntity(lightning);

        ModCriteria.TRIM_TRIGGER.trigger(player, "copper", "struck_by_lightning");

        assert world.getServer() != null;
        world.getServer().execute(() -> {
            player.addStatusEffect(new StatusEffectInstance(
                    ModEffects.CHARGED,
                    CHARGED_DURATION_TICKS,
                    0,
                    false,
                    false,
                    true
            ));
            player.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0F, 1.0F);
        });
    }
}