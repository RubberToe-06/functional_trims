package functional_trims.effect;

import functional_trims.config.ConfigManager;
import functional_trims.mixin.EntityAccessor;

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AmethystVisionEffect extends MobEffect {

    private static final byte GLOW_MASK = 0x40;

    private static final Map<UUID, Set<Integer>> glowingByPlayer =
            new ConcurrentHashMap<>();

    private static final Map<UUID, Boolean> wasActive =
            new ConcurrentHashMap<>();

    private static final float RANGE_MULTIPLIER =
            ConfigManager.get().amethyst.effectRangeMultiplier;

    public AmethystVisionEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xAA00FF);
        this.withSoundOnAdded(SoundEvents.AMETHYST_BLOCK_CHIME);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(
            ServerLevel world,
            LivingEntity entity,
            int amplifier) {

        if (!(entity instanceof ServerPlayer player)) {
            return false;
        }

        UUID id = player.getUUID();
        wasActive.put(id, true);

        glowingByPlayer.putIfAbsent(
                id,
                ConcurrentHashMap.newKeySet()
        );

        Set<Integer> glowingIds = glowingByPlayer.get(id);

        double radius = 25.0 * RANGE_MULTIPLIER;

        var box = player.getBoundingBox().inflate(radius);

        var nearby = world.getEntitiesOfClass(
                LivingEntity.class,
                box,
                e -> e != player && e.isAlive()
        );

        glowingIds.removeIf(entityId -> {

            var existingEntity = world.getEntity(entityId);

            if (!(existingEntity instanceof LivingEntity livingEntity)
                    || !nearby.contains(livingEntity)) {

                if (existingEntity instanceof LivingEntity livingEntity) {
                    sendGlowPacket(player, livingEntity, false);
                }

                return true;
            }

            return false;
        });

        for (LivingEntity nearbyEntity : nearby) {

            if (glowingIds.add(nearbyEntity.getId())) {

                sendGlowPacket(player, nearbyEntity, true);

                if (nearbyEntity.isInvisible()) {

                    functional_trims.criteria.ModCriteria.TRIM_TRIGGER.get().trigger(
                            player,
                            "amethyst",
                            "i_see_you"
                    );

                } else {

                    functional_trims.criteria.ModCriteria.TRIM_TRIGGER.get().trigger(
                            player,
                            "amethyst",
                            "wallhacks_enabled"
                    );
                }
            }
        }

        return true;
    }

    public static void tick(ServerLevel world) {

        for (ServerPlayer player : world.players()) {

            UUID id = player.getUUID();

            boolean active =
                    player.hasEffect(ModEffects.AMETHYST_VISION);

            if (!active && wasActive.getOrDefault(id, false)) {

                Set<Integer> ids = glowingByPlayer.remove(id);

                if (ids != null) {

                    for (int entityId : ids) {

                        var entity = world.getEntity(entityId);

                        if (entity instanceof LivingEntity livingEntity) {
                            sendGlowPacket(
                                    player,
                                    livingEntity,
                                    false
                            );
                        }
                    }
                }

                wasActive.put(id, false);
            }
        }
    }

    private static void sendGlowPacket(
            ServerPlayer player,
            LivingEntity target,
            boolean glow) {

        if (target == null || !target.isAlive()) {
            return;
        }

        var flagsAccessor = EntityAccessor.getFlags();

        byte serverFlags =
                target.getEntityData().get(flagsAccessor);

        byte clientFlags =
                glow
                        ? (byte) (serverFlags | GLOW_MASK)
                        : serverFlags;

        SynchedEntityData.DataValue<Byte> entry =
                SynchedEntityData.DataValue.create(
                        flagsAccessor,
                        clientFlags
                );

        player.connection.send(
                new ClientboundSetEntityDataPacket(
                        target.getId(),
                        List.of(entry)
                )
        );
    }
}