package functional_trims.trim_effect;

import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
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

    private static final double SLIDE_DECAY = 0.75;     // smooth slide → stop
    private static final double STOP_Y_EPS = 0.03;

    private static final int RELEASE_GRACE_TICKS = 6;

    private static final Map<UUID, GripData> GRIP = new HashMap<>();

    private static class GripData {
        boolean gripping = false;
        boolean justStarted = false;
        Direction normal = null;
        int sinceGrip = 0;
        int releaseGrace = 0;
    }

    public static void register() {
        ServerTickEvents.START_WORLD_TICK.register((ServerWorld world) -> {
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

        boolean fullResin = TrimHelper.countTrim(player, ArmorTrimMaterials.RESIN) == 4;
        if (!fullResin || !player.isSneaking()) {
            if (gd.gripping) release(player, gd);
            return;
        }

        Direction contact = findContactDirection(player);
        if (contact == Direction.DOWN) {
            if (gd.gripping) release(player, gd);
            return;
        }

        if (!gd.gripping && contact == null) return;

        // BEGIN GRIP
        if (!gd.gripping) {
            gd.gripping = true;
            gd.justStarted = true;
            gd.normal = contact;
            gd.sinceGrip = 0;

            world.playSound(null, player.getBlockPos(),
                    SoundEvents.BLOCK_HONEY_BLOCK_SLIDE,
                    SoundCategory.PLAYERS, 0.6F, 1.0F);

            ModCriteria.TRIM_TRIGGER.trigger(player, "resin", "stick_to_wall");
        } else if (contact != null) {
            gd.normal = contact;
        }

        player.setNoGravity(true);
        player.fallDistance = 0;

        Vec3d v = player.getVelocity();

        // 🔑 One-time momentum kill (THIS fixes preserved velocity)
        if (gd.justStarted) {
            v = Vec3d.ZERO;
            gd.justStarted = false;
        }

        // Project velocity onto wall plane
        Vec3d n = new Vec3d(
                gd.normal.getOffsetX(),
                gd.normal.getOffsetY(),
                gd.normal.getOffsetZ()
        );

        double intoWall = v.dotProduct(n);
        Vec3d vParallel = v.subtract(n.multiply(intoWall));

        // Smooth vertical slide → stop
        double newY = Math.abs(vParallel.y) < STOP_Y_EPS
                ? 0.0
                : vParallel.y * SLIDE_DECAY;

        Vec3d newVel = new Vec3d(
                vParallel.x,
                newY,
                vParallel.z
        );

        player.setVelocity(newVel);
        player.velocityDirty = true;

        // Maintain wall contact without snapping
        player.setPosition(
                player.getX() - n.x * NUDGE,
                player.getY() - n.y * NUDGE,
                player.getZ() - n.z * NUDGE
        );

        gd.sinceGrip++;

        if (findContactDirection(player) == null && gd.sinceGrip > 2) {
            release(player, gd);
        }
    }

    private static void release(ServerPlayerEntity player, GripData gd) {
        player.setNoGravity(false);
        player.fallDistance = 0;

        gd.gripping = false;
        gd.normal = null;
        gd.sinceGrip = 0;
        gd.releaseGrace = RELEASE_GRACE_TICKS;

        player.getEntityWorld().playSound(null, player.getBlockPos(),
                SoundEvents.BLOCK_SLIME_BLOCK_FALL,
                SoundCategory.PLAYERS, 0.4F, 1.1F);
    }

    private static Direction findContactDirection(ServerPlayerEntity player) {
        World world = player.getEntityWorld();
        Box box = player.getBoundingBox();

        Direction best = null;
        double bestDot = Double.NEGATIVE_INFINITY;
        boolean touchingGround = false;

        Vec3d v = player.getVelocity();

        for (Direction d : Direction.values()) {
            Vec3d dv = new Vec3d(d.getOffsetX(), d.getOffsetY(), d.getOffsetZ());
            Box probe = box.offset(dv.multiply(CONTACT_EPS));

            if (!world.isSpaceEmpty(player, probe)) {
                if (d == Direction.DOWN) {
                    touchingGround = true;
                    continue;
                }

                double dot = v.dotProduct(dv.multiply(-1));
                if (dot > bestDot) {
                    bestDot = dot;
                    best = d;
                }
            }
        }

        if (best == null && touchingGround) return Direction.DOWN;
        return best;
    }
}
