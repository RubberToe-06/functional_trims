package functional_trims.trim_effect;

import functional_trims.config.ConfigManager;
import functional_trims.mixin.EntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AmethystVisionEffect extends StatusEffect {
    private static final byte GLOW_MASK = 0x40;
    private static final Map<UUID, Set<Integer>> glowingByPlayer = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> wasActive = new ConcurrentHashMap<>();
    private static final float RANGE_MULTIPLIER = ConfigManager.get().effectRangeMultiplier;

    public AmethystVisionEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xAA00FF);
        this.applySound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!(entity instanceof ServerPlayerEntity player)) return false;
        if (!(player.getEntityWorld() instanceof ServerWorld world)) return false;
        UUID id = player.getUuid();

        // mark as active this tick
        wasActive.put(id, true);

        glowingByPlayer.putIfAbsent(id, ConcurrentHashMap.newKeySet());
        Set<Integer> glowingIds = glowingByPlayer.get(id);

        double radius = 25.0 * RANGE_MULTIPLIER;
        var box = player.getBoundingBox().expand(radius);
        var nearby = world.getEntitiesByClass(LivingEntity.class, box, e -> e != player && e.isAlive());

        // Remove out-of-range entities (unglow them)
        glowingIds.removeIf(eid -> {
            var e = world.getEntityById(eid);
            if (!(e instanceof LivingEntity le) || !nearby.contains(le)) {
                sendGlowPacket(player, (LivingEntity) e, false);
                return true;
            }
            return false;
        });

        // Add new in-range ones
        for (var e : nearby) {
            if (glowingIds.add(e.getId())) {
                sendGlowPacket(player, e, true);

                // --- Advancement trigger: "I See You" ---
                if (e.isInvisible()) {
                    // Detected invisible entity
                    functional_trims.criteria.ModCriteria.TRIM_TRIGGER.trigger(player, "amethyst", "i_see_you");
                } else {
                    // First time seeing any entity via amethyst vision
                    functional_trims.criteria.ModCriteria.TRIM_TRIGGER.trigger(player, "amethyst", "wallhacks_enabled");
                }
            }
        }

        return true;
    }

    /** Runs when the effect is completely removed. */
    @Override
    public void onRemoved(AttributeContainer attributes) {
        // nothing: handled dynamically on tick below
    }

    /** Extra cleanup every tick to clear stuck glows for players who lost the effect. */
    public static void tick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            UUID id = player.getUuid();
            boolean active = player.hasStatusEffect(ModEffects.AMETHYST_VISION);

            if (!active && wasActive.getOrDefault(id, false)) {
                // just lost the effect → unglow everything
                Set<Integer> ids = glowingByPlayer.remove(id);
                if (ids != null) {
                    for (int eid : ids) {
                        var e = world.getEntityById(eid);
                        if (e instanceof LivingEntity le) {
                            sendGlowPacket(player, le, false);
                        }
                    }
                }
                wasActive.put(id, false);
            }
        }
    }

    private static void sendGlowPacket(ServerPlayerEntity player, LivingEntity target, boolean glow) {
        if (target == null || !target.isAlive()) return;

        var FLAGS = EntityAccessor.getFlags();
        byte serverFlags = target.getDataTracker().get(FLAGS);
        byte clientFlags = glow ? (byte) (serverFlags | GLOW_MASK) : serverFlags;

        DataTracker.SerializedEntry<Byte> entry = DataTracker.SerializedEntry.of(FLAGS, clientFlags);
        player.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(target.getId(),
                List.of(entry)));
    }
}
