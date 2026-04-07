package functional_trims.effect;

import functional_trims.config.ConfigManager;
import functional_trims.mixin.EntityAccessor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jspecify.annotations.NonNull;

public class AmethystVisionEffect extends MobEffect {
    private static final byte GLOW_MASK = 0x40;
    private static final Map<UUID, Set<Integer>> glowingByPlayer = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> wasActive = new ConcurrentHashMap<>();

    public AmethystVisionEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xAA00FF);
        this.withSoundOnAdded(SoundEvents.AMETHYST_BLOCK_CHIME);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(@NonNull ServerLevel world, @NonNull LivingEntity entity, int amplifier) {
        if (!(entity instanceof ServerPlayer player)) return false;
        UUID id = player.getUUID();

        // mark as active this tick
        wasActive.put(id, true);

        glowingByPlayer.putIfAbsent(id, ConcurrentHashMap.newKeySet());
        Set<Integer> glowingIds = glowingByPlayer.get(id);

        double radius = 25.0 * ConfigManager.get().amethyst.effectRangeMultiplier;
        var box = player.getBoundingBox().inflate(radius);
        var nearby = world.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive());

        // Remove out-of-range entities (unglow them)
        glowingIds.removeIf(eid -> {
            var e = world.getEntity(eid);
            // Entity despawned or left the dimension — remove from tracking, no packet needed
            if (!(e instanceof LivingEntity le)) return true;
            // Entity moved out of range — unglow then remove
            if (!nearby.contains(le)) {
                sendGlowPacket(player, le, false);
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
    public void removeAttributeModifiers(@NonNull AttributeMap attributes) {
        // nothing: handled dynamically on tick below
    }

    /** Extra cleanup every tick to clear stuck glows for players who lost the effect. */
    public static void tick(ServerLevel world) {
        for (ServerPlayer player : world.players()) {
            UUID id = player.getUUID();
            boolean active = player.hasEffect(ModEffects.AMETHYST_VISION);

            if (!active && wasActive.getOrDefault(id, false)) {
                // just lost the effect → unglow everything
                Set<Integer> ids = glowingByPlayer.remove(id);
                if (ids != null) {
                    for (int eid : ids) {
                        var e = world.getEntity(eid);
                        if (e instanceof LivingEntity le) {
                            sendGlowPacket(player, le, false);
                        }
                    }
                }
                wasActive.put(id, false);
            }
        }
    }

    /** Called on player disconnect — removes all glow tracking state for that player. */
    public static void cleanupPlayer(UUID id) {
        glowingByPlayer.remove(id);
        wasActive.remove(id);
    }

    private static void sendGlowPacket(ServerPlayer player, LivingEntity target, boolean glow) {
        if (target == null || !target.isAlive()) return;

        var FLAGS = EntityAccessor.getFlags();
        byte serverFlags = target.getEntityData().get(FLAGS);
        byte clientFlags = glow ? (byte) (serverFlags | GLOW_MASK) : serverFlags;

        SynchedEntityData.DataValue<Byte> entry = SynchedEntityData.DataValue.create(FLAGS, clientFlags);
        player.connection.send(new ClientboundSetEntityDataPacket(target.getId(),
                List.of(entry)));
    }
}