package functional_trims.trim_effect;

import functional_trims.criteria.ModCriteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public final class DiamondBurst {
    // Tunables
    private static final double RADIUS = 6.0;
    private static final float  MAX_DAMAGE = 20.0f;
    private static final double MAX_KNOCKBACK = 2.25;
    private static final double Y_BOOST = 0.35;
    private static final boolean HIT_ONLY_MOBS = true;

    private DiamondBurst() {}

    public static void doBurst(PlayerEntity player) {
        // Require server player + server world
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;
        if (!(serverPlayer.getEntityWorld() instanceof ServerWorld world)) return;

        final double px = serverPlayer.getX();
        final double py = serverPlayer.getY() + serverPlayer.getStandingEyeHeight() * 0.5;
        final double pz = serverPlayer.getZ();

        // 1) SFX/VFX
        world.playSound(null, px, py, pz, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.25f, 1.2f);
        world.playSound(null, BlockPos.ofFloored(px, py, pz), SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.PLAYERS, 1.0f, 0.7f);
        world.playSound(null, BlockPos.ofFloored(px, py, pz), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 0.6f, 1.6f);

        world.spawnParticles(ParticleTypes.END_ROD, px, py, pz, 120, 0.75, 0.6, 0.75, 0.12);
        world.spawnParticles(ParticleTypes.GLOW,    px, py, pz,  40, 0.40, 0.30, 0.40, 0.10);
        for (int i = 0; i < 48; i++) {
            double t = (i / 48.0) * Math.PI * 2.0;
            double rx = px + Math.cos(t) * 1.2;
            double rz = pz + Math.sin(t) * 1.2;
            world.spawnParticles(ParticleTypes.CRIT, rx, py + 0.2, rz, 1, 0.02, 0.02, 0.02, 0.0);
        }

        // 2) Trigger advancement ON BURST (not dependent on hits)
        ModCriteria.TRIM_TRIGGER.trigger(serverPlayer, "diamond", "armor_shatter");

        // 3) AoE
        Box area = new Box(px - RADIUS, py - RADIUS, pz - RADIUS, px + RADIUS, py + RADIUS, pz + RADIUS);
        var targets = world.getEntitiesByClass(
                LivingEntity.class,
                area,
                e -> e.isAlive() && e != serverPlayer && (!HIT_ONLY_MOBS || e instanceof MobEntity)
        );

        var sources = world.getDamageSources();
        for (LivingEntity e : targets) {
            double dx = e.getX() - px;
            double dz = e.getZ() - pz;
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > RADIUS || dist == 0.0) continue;

            double falloff = 1.0 - (dist / RADIUS);
            float damage = (float)(MAX_DAMAGE * falloff);
            if (damage > 0.0f) {
                // damage(ServerWorld, DamageSource, float)
                e.damage(world, sources.playerAttack(serverPlayer), damage);
            }

            double nx = dx / dist, nz = dz / dist;
            double kb = MAX_KNOCKBACK * (0.5 + 0.5 * falloff);

            e.takeKnockback(kb, -nx, -nz);
            e.addVelocity(0.0, Y_BOOST * falloff, 0.0);
            e.velocityDirty = true;
        }
    }
}
