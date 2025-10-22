package functional_trims.trim_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public final class DiamondBurst {
    // Tweakables
    private static final double RADIUS = 6.0;          // AoE radius in blocks
    private static final float  MAX_DAMAGE = 20.0f;    // Max damage at point-blank
    private static final double MAX_KNOCKBACK = 2.25;   // Base horizontal knockback
    private static final double Y_BOOST = 0.35;        // Extra vertical pop
    private static final boolean HIT_ONLY_MOBS = true; // true: hit mobs only; false: hit any LivingEntity except the player

    private DiamondBurst() {}

    public static void doBurst(PlayerEntity player) {
        if (!(player.getEntityWorld() instanceof ServerWorld world)) return;

        final double px = player.getX();
        final double py = player.getY() + player.getStandingEyeHeight() * 0.5; // nice mid-body origin
        final double pz = player.getZ();

        // 1) VFX/SFX
        // Big “diamond energy” feel: glassy + crystalline + a low explosion thump
        world.playSound(null, px, py, pz, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.25f, 1.2f);
        world.playSound(null, BlockPos.ofFloored(px, py, pz), SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.PLAYERS, 1.0f, 0.7f);
        world.playSound(null, BlockPos.ofFloored(px, py, pz), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 0.6f, 1.6f);

        // Core burst
        world.spawnParticles(ParticleTypes.END_ROD, px, py, pz, 120, 0.75, 0.6, 0.75, 0.12);
        world.spawnParticles(ParticleTypes.GLOW,    px, py, pz,  40, 0.40, 0.30, 0.40, 0.10);
        // Expanding ring (purely visual)
        for (int i = 0; i < 48; i++) {
            double t = (i / 48.0) * Math.PI * 2.0;
            double rx = px + Math.cos(t) * 1.2;
            double rz = pz + Math.sin(t) * 1.2;
            world.spawnParticles(ParticleTypes.CRIT, rx, py + 0.2, rz, 1, 0.02, 0.02, 0.02, 0.0);
        }

        // 2) AoE query
        Box box = new Box(px - RADIUS, py - RADIUS, pz - RADIUS, px + RADIUS, py + RADIUS, pz + RADIUS);
        var targets = world.getEntitiesByClass(
                LivingEntity.class,
                box,
                e -> e.isAlive() && e != player && (!HIT_ONLY_MOBS || e instanceof MobEntity)
        );

        // 3) Apply damage + knockback with falloff (linear by distance)
        var sources = world.getDamageSources();
        for (LivingEntity e : targets) {
            double dx = e.getX() - px;
            double dz = e.getZ() - pz;
            double distSq = dx * dx + dz * dz;
            double dist = Math.sqrt(distSq);
            if (dist > RADIUS || dist == 0.0) continue;

            double falloff = 1.0 - (dist / RADIUS);              // 1 at center -> 0 at edge
            float damage = (float)(MAX_DAMAGE * falloff);
            if (damage > 0.0f) {
                // Count as player-dealt damage so loot/xp/aggro behave naturally
                if (e.getEntityWorld() instanceof ServerWorld sw) {
                    e.damage(sw, sources.playerAttack(player), damage);
                }
            }

            // Knockback direction (normalized horizontal)
            double nx = dx / dist;
            double nz = dz / dist;
            double kb = MAX_KNOCKBACK * (0.5 + 0.5 * falloff);

            // Flip direction: push *away* from player
            e.takeKnockback(kb, -nx, -nz);
            e.addVelocity(0.0, Y_BOOST * falloff, 0.0);
            e.velocityModified = true; // ensure client sync
        }
    }
}
