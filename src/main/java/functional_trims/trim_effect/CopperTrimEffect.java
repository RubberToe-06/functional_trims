package functional_trims.trim_effect;

import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import functional_trims.trim_effect.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;
import java.util.WeakHashMap;

public class CopperTrimEffect implements ServerTickEvents.EndWorldTick {

    private static final int LIGHTNING_COOLDOWN_TICKS = 200; // 10s between possible strikes
    private final WeakHashMap<UUID, Integer> cooldowns = new WeakHashMap<>();

    @Override
    public void onEndTick(ServerWorld world) {
        if (!world.isThundering()) return;

        for (ServerPlayerEntity player : world.getPlayers()) {
            if (!TrimHelper.hasFullTrim(player, ArmorTrimMaterials.COPPER)) continue;
            if (!world.isSkyVisible(player.getBlockPos())) continue;

            UUID id = player.getUuid();
            cooldowns.putIfAbsent(id, 0);
            int cd = cooldowns.get(id);
            if (cd > 0) {
                cooldowns.put(id, cd - 1);
                continue;
            }

            // small random chance each tick (≈ once every 10s average)
            if (world.getRandom().nextInt(200) == 0) {
                summonLightning(world, player);
                cooldowns.put(id, LIGHTNING_COOLDOWN_TICKS);
            }
        }
    }

    private void summonLightning(ServerWorld world, ServerPlayerEntity player) {
        LightningEntity lightning = new LightningEntity(net.minecraft.entity.EntityType.LIGHTNING_BOLT, world);
        lightning.refreshPositionAfterTeleport(player.getX(), player.getY(), player.getZ());
        lightning.setCosmetic(true);
        world.spawnEntity(lightning);

        // --- Trigger advancement ---
        ModCriteria.TRIM_TRIGGER.trigger(player, "copper", "struck_by_lightning");

        // Schedule CHARGED effect next tick
        world.getServer().execute(() -> {
            player.addStatusEffect(ModEffects.CHARGED_60S);
            player.playSound(net.minecraft.sound.SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0F, 1.0F);
        });
    }
}
