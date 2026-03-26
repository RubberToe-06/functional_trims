package functional_trims.trim_effect;

import functional_trims.config.FTConfig;
import functional_trims.config.ConfigManager;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
        return Math.clamp(ConfigManager.get().gripStrengthMultiplier, 0.1, 3.0);
    }

    public static void register() {
        ServerTickEvents.START_WORLD_TICK.register((ServerWorld world) -> {
            if (!FTConfig.isTrimEnabled("resin")) return;

            for (ServerPlayerEntity player : world.getPlayers()) {
                apply(player);
            }
        });
    }

    private static void apply(ServerPlayerEntity player) {
        World world = player.getEntityWorld();
        if (world.isClient()) return;
        GripData gd = GRIP.computeIfAbsent(player.getUuid(), u -> new GripData());

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

    private static boolean canGrip(ServerPlayerEntity player) {
        return TrimHelper.countTrim(player, ArmorTrimMaterials.RESIN) == 4 && player.isSneaking();
    }

    private static void releaseIfNeeded(ServerPlayerEntity player, GripData gd) {
        if (gd.gripping) {
            release(player, gd);
        }
    }

    private static void beginGrip(ServerPlayerEntity player, GripData gd, Direction contact) {
        World world = player.getEntityWorld();

        gd.gripping = true;
        gd.normal = contact;
        gd.sinceGrip = 0;

        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.BLOCK_HONEY_BLOCK_SLIDE,
                SoundCategory.PLAYERS,
                0.6F,
                1.0F
        );

        ModCriteria.TRIM_TRIGGER.trigger(player, "resin", "stick_to_wall");
        if (player.fallDistance >= 100.0F) {
            ModCriteria.TRIM_TRIGGER.trigger(player, "resin", "long_fall");
        }
    }

    private static void applyGripPhysics(ServerPlayerEntity player, GripData gd) {
        Vec3d vel = player.getVelocity();

        player.fallDistance = 0;
        player.setNoGravity(true);

        Vec3d normal = directionToVec(gd.normal);

        double intoWall = vel.dotProduct(normal);
        if (intoWall < 0) {
            vel = vel.subtract(normal.multiply(intoWall));
        }

        double effectiveDecay = Math.pow(DECAY_RATE, gripStrength());
        Vec3d newVel = vel.multiply(effectiveDecay);

        if (newVel.lengthSquared() < STOP_THRESHOLD * STOP_THRESHOLD) {
            newVel = Vec3d.ZERO;
        }

        player.setVelocity(newVel);
        player.velocityDirty = true;
        player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player.getId(), newVel));

        if (gd.normal != null) {
            player.setPosition(
                    player.getX() - normal.x * NUDGE,
                    player.getY() - normal.y * NUDGE,
                    player.getZ() - normal.z * NUDGE
            );
        }
    }

    private static void release(ServerPlayerEntity player, GripData gd) {
        World world = player.getEntityWorld();

        player.setNoGravity(false);
        player.fallDistance = 0;

        gd.gripping = false;
        gd.normal = null;
        gd.sinceGrip = 0;
        gd.releaseGrace = RELEASE_GRACE_TICKS;

        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.BLOCK_SLIME_BLOCK_FALL,
                SoundCategory.PLAYERS,
                0.4F,
                1.1F
        );
    }

    private static Direction findContactDirection(ServerPlayerEntity player) {
        World world = player.getEntityWorld();
        Box box = player.getBoundingBox();
        Vec3d velocity = player.getVelocity();
        Direction best = null;
        double bestDot = Double.NEGATIVE_INFINITY;
        boolean touchingGround = false;

        for (Direction direction : Direction.values()) {
            Vec3d offset = directionToVec(direction);
            Box probe = box.offset(offset.multiply(CONTACT_EPS));

            if (!world.isSpaceEmpty(player, probe)) {
                if (direction == Direction.DOWN) {
                    touchingGround = true;
                    continue;
                }

                double dot = velocity.dotProduct(offset.multiply(-1));
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

    private static Vec3d directionToVec(Direction direction) {
        if (direction == null) {
            return Vec3d.ZERO;
        }

        return new Vec3d(
                direction.getOffsetX(),
                direction.getOffsetY(),
                direction.getOffsetZ()
        );
    }
}