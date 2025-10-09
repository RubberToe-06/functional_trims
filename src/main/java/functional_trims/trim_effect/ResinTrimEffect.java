package functional_trims.trim_effect;

import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResinTrimEffect {

    private static final double STOP_THRESHOLD = 0.08;
    private static final double DECAY_RATE = 0.7; // slide-to-stop factor

    private static final double CONTACT_EPS = 0.06;   // how far to probe for nearby block collisions
    private static final double NUDGE = 0.002;        // tiny nudge to keep the player “touching”
    private static final int RELEASE_GRACE_TICKS = 6; // keep fallDistance=0 a few ticks after release

    private static final Map<UUID, GripData> GRIP = new HashMap<>();

    private static class GripData {
        boolean gripping = false;
        Direction normal = null;
        int sinceGrip = 0;
        int releaseGrace = 0;
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                applyResinGrip(player);
            }
        });
    }

    private static void applyResinGrip(ServerPlayerEntity player) {
        World world = player.getWorld();
        if (world.isClient) return;

        GripData gd = GRIP.computeIfAbsent(player.getUuid(), u -> new GripData());

        // If they just released grip recently, keep clearing fall distance briefly to avoid a one-tick dump.
        if (gd.releaseGrace > 0) {
            player.fallDistance = 0;
            gd.releaseGrace--;
        }

        // Require full resin-trimmed set and crouch
        boolean fullResin = (TrimHelper.countTrim(player, ArmorTrimMaterials.RESIN) == 4);
        if (!fullResin || !player.isSneaking()) {
            if (gd.gripping) release(player, gd);
            return;
        }

        // Check for solid contact
        Direction contact = findContactDirection(player);

        // ✅ Skip grip if only touching the ground
        if (contact == Direction.DOWN) {
            if (gd.gripping) release(player, gd);
            return;
        }

        if (!gd.gripping && contact == null) {
            // Not yet gripping and no contact — do nothing.
            return;
        }

        // Begin or maintain grip
        if (!gd.gripping) {
            gd.gripping = true;
            gd.normal = contact;
            gd.sinceGrip = 0;

            // 🎵 Sticky sound when grip starts
            world.playSound(null, player.getBlockPos(),
                    SoundEvents.BLOCK_HONEY_BLOCK_SLIDE,
                    SoundCategory.PLAYERS, 0.6F, 1.0F);
        } else if (contact != null) {
            gd.normal = contact;
        }

        // DECEL -> STUCK
        Vec3d vel = player.getVelocity();
        double speed2 = vel.lengthSquared();

        player.fallDistance = 0; // clear while gripping

        if (speed2 > STOP_THRESHOLD * STOP_THRESHOLD) {
            // Smooth slide-to-stop
            player.setVelocity(vel.multiply(DECAY_RATE));
            player.setNoGravity(true);
        } else {
            // Fully stuck
            player.setVelocity(Vec3d.ZERO);
            player.setNoGravity(true);

            if (gd.normal != null) {
                Vec3d n = new Vec3d(gd.normal.getOffsetX(), gd.normal.getOffsetY(), gd.normal.getOffsetZ());
                player.setPosition(
                        player.getX() - n.x * NUDGE,
                        player.getY() - n.y * NUDGE,
                        player.getZ() - n.z * NUDGE
                );
            }
        }

        player.velocityModified = true;
        gd.sinceGrip++;

        // Release if lost contact for a couple ticks
        boolean lostContact = (findContactDirection(player) == null);
        if (lostContact && gd.sinceGrip > 2) {
            release(player, gd);
        }
    }

    private static void release(ServerPlayerEntity player, GripData gd) {
        player.setNoGravity(false);
        player.fallDistance = 0; // safety: clear on release
        gd.gripping = false;
        gd.normal = null;
        gd.sinceGrip = 0;
        gd.releaseGrace = RELEASE_GRACE_TICKS; // keep clearing fallDistance briefly

        // 🎵 Soft pop sound on release
        player.getWorld().playSound(null, player.getBlockPos(),
                SoundEvents.BLOCK_SLIME_BLOCK_FALL,
                SoundCategory.PLAYERS, 0.4F, 1.1F);
    }

    /**
     * Returns a face direction we’re touching, or null if none.
     * We test the player’s bounding box offset slightly toward each face; if the space is NOT empty, we’re touching that face.
     */
    private static Direction findContactDirection(ServerPlayerEntity player) {
        World world = player.getWorld();
        Box box = player.getBoundingBox();

        Direction best = null;
        Vec3d v = player.getVelocity();
        Direction[] dirs = Direction.values();
        double bestDot = Double.NEGATIVE_INFINITY;

        for (Direction d : dirs) {
            Vec3d dv = new Vec3d(d.getOffsetX(), d.getOffsetY(), d.getOffsetZ());
            Box probe = box.offset(dv.multiply(CONTACT_EPS));
            boolean touching = !world.isSpaceEmpty(player, probe);
            if (!touching) continue;

            double dot = v.dotProduct(dv.multiply(-1));
            if (dot > bestDot) {
                bestDot = dot;
                best = d;
            }
        }
        return best;
    }
}
