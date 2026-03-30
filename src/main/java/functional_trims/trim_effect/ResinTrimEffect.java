package functional_trims.trim_effect;

import functional_trims.config.FTConfig;
import functional_trims.config.ConfigManager;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResinTrimEffect {

    private static final double CONTACT_EPS = 0.06;
    private static final double NUDGE = 0.002;
    private static final double DECAY_RATE = 0.75;
    private static final double STOP_THRESHOLD = 0.08;
    private static final int RELEASE_GRACE_TICKS = 6;
    private static final Map<UUID, GripData> GRIP = new HashMap<>();
    private static class GripData {
        boolean gripping = false;
        Direction normal = null;
        int sinceGrip = 0;
        int releaseGrace = 0;
    }

    private static double gripStrength() {
        return Math.clamp(ConfigManager.get().resin.gripStrengthMultiplier, 0.1, 3.0);
    }

    public static void register() {
        ServerTickEvents.START_WORLD_TICK.register((ServerLevel world) -> {
            if (!FTConfig.isTrimEnabled("resin")) return;

            for (ServerPlayer player : world.players()) {
                apply(player);
            }
        });
    }

    private static void apply(ServerPlayer player) {
        Level world = player.level();
        if (world.isClientSide()) return;
        GripData gd = GRIP.computeIfAbsent(player.getUUID(), u -> new GripData());

        if (gd.releaseGrace > 0) {
            player.fallDistance = 0;
            gd.releaseGrace--;
        }

        if (!canGrip(player)) {
            releaseIfNeeded(player, gd);
            return;
        }

        Direction contact = findContactDirection(player);

        if (contact == Direction.DOWN) {
            releaseIfNeeded(player, gd);
            return;
        }

        if (!gd.gripping && contact == null) {
            return;
        }

        if (!gd.gripping) {
            beginGrip(player, gd, contact);
        } else if (contact != null) {
            gd.normal = contact;
        }

        applyGripPhysics(player, gd);
        gd.sinceGrip++;

        Direction currentContact = findContactDirection(player);
        if (currentContact == null && gd.sinceGrip > 2) {
            release(player, gd);
        }
    }

    private static boolean canGrip(ServerPlayer player) {
        return TrimHelper.countTrim(player, TrimMaterials.RESIN) == 4 && player.isShiftKeyDown();
    }

    private static void releaseIfNeeded(ServerPlayer player, GripData gd) {
        if (gd.gripping) {
            release(player, gd);
        }
    }

    private static void beginGrip(ServerPlayer player, GripData gd, Direction contact) {
        Level world = player.level();

        gd.gripping = true;
        gd.normal = contact;
        gd.sinceGrip = 0;

        world.playSound(
                null,
                player.blockPosition(),
                SoundEvents.HONEY_BLOCK_SLIDE,
                SoundSource.PLAYERS,
                0.6F,
                1.0F
        );

        ModCriteria.TRIM_TRIGGER.trigger(player, "resin", "stick_to_wall");
        if (player.fallDistance >= 100.0F) {
            ModCriteria.TRIM_TRIGGER.trigger(player, "resin", "long_fall");
        }
    }

    private static void applyGripPhysics(ServerPlayer player, GripData gd) {
        Vec3 vel = player.getDeltaMovement();

        player.fallDistance = 0;
        player.setNoGravity(true);

        Vec3 normal = directionToVec(gd.normal);

        double intoWall = vel.dot(normal);
        if (intoWall < 0) {
            vel = vel.subtract(normal.scale(intoWall));
        }

        double effectiveDecay = Math.pow(DECAY_RATE, gripStrength());
        Vec3 newVel = vel.scale(effectiveDecay);

        if (newVel.lengthSqr() < STOP_THRESHOLD * STOP_THRESHOLD) {
            newVel = Vec3.ZERO;
        }

        player.setDeltaMovement(newVel);
        player.needsSync = true;
        player.connection.send(new ClientboundSetEntityMotionPacket(player.getId(), newVel));

        if (gd.normal != null) {
            player.setPos(
                    player.getX() - normal.x * NUDGE,
                    player.getY() - normal.y * NUDGE,
                    player.getZ() - normal.z * NUDGE
            );
        }
    }

    private static void release(ServerPlayer player, GripData gd) {
        Level world = player.level();

        player.setNoGravity(false);
        player.fallDistance = 0;

        gd.gripping = false;
        gd.normal = null;
        gd.sinceGrip = 0;
        gd.releaseGrace = RELEASE_GRACE_TICKS;

        world.playSound(
                null,
                player.blockPosition(),
                SoundEvents.SLIME_BLOCK_FALL,
                SoundSource.PLAYERS,
                0.4F,
                1.1F
        );
    }

    private static Direction findContactDirection(ServerPlayer player) {
        Level world = player.level();
        AABB box = player.getBoundingBox();
        Vec3 velocity = player.getDeltaMovement();
        Direction best = null;
        double bestDot = Double.NEGATIVE_INFINITY;
        boolean touchingGround = false;

        for (Direction direction : Direction.values()) {
            Vec3 offset = directionToVec(direction);
            AABB probe = box.move(offset.scale(CONTACT_EPS));

            if (!world.noCollision(player, probe)) {
                if (direction == Direction.DOWN) {
                    touchingGround = true;
                    continue;
                }

                double dot = velocity.dot(offset.scale(-1));
                if (dot > bestDot) {
                    bestDot = dot;
                    best = direction;
                }
            }
        }

        if (best == null && touchingGround) {
            return Direction.DOWN;
        }

        return best;
    }

    private static Vec3 directionToVec(Direction direction) {
        if (direction == null) {
            return Vec3.ZERO;
        }

        return new Vec3(
                direction.getStepX(),
                direction.getStepY(),
                direction.getStepZ()
        );
    }
}