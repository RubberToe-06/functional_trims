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

public class DiamondBurst {

    private static final double RADIUS = 6.0;
    private static final float MAX_DAMAGE = 20.0f;
    private static final double MAX_KNOCKBACK = 2.25;
    private static final double Y_BOOST = 0.35;
    private static final boolean HIT_ONLY_MOBS = true;

    private DiamondBurst() {}

    public static void doBurst(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;
        if (!(serverPlayer.getEntityWorld() instanceof ServerWorld world)) return;

        double px = serverPlayer.getX();
        double py = serverPlayer.getY() + serverPlayer.getStandingEyeHeight() * 0.5;
        double pz = serverPlayer.getZ();

        playBurstEffects(world, px, py, pz);
        ModCriteria.TRIM_TRIGGER.trigger(serverPlayer, "diamond", "armor_shatter");
        damageAndKnockbackTargets(world, serverPlayer, px, py, pz);
    }

    private static void playBurstEffects(ServerWorld world, double px, double py, double pz) {
        BlockPos center = BlockPos.ofFloored(px, py, pz);

        world.playSound(null, px, py, pz,
                SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.PLAYERS,
                0.25f,
                1.2f
        );
        world.playSound(null, center,
                SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                SoundCategory.PLAYERS,
                1.0f,
                0.7f
        );
        world.playSound(null, center,
                SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                SoundCategory.PLAYERS,
                0.6f,
                1.6f
        );

        world.spawnParticles(ParticleTypes.END_ROD, px, py, pz, 120, 0.75, 0.6, 0.75, 0.12);
        world.spawnParticles(ParticleTypes.GLOW, px, py, pz, 40, 0.40, 0.30, 0.40, 0.10);

        for (int i = 0; i < 48; i++) {
            double t = (i / 48.0) * Math.PI * 2.0;
            double rx = px + Math.cos(t) * 1.2;
            double rz = pz + Math.sin(t) * 1.2;
            world.spawnParticles(ParticleTypes.CRIT, rx, py + 0.2, rz, 1, 0.02, 0.02, 0.02, 0.0);
        }
    }

    private static void damageAndKnockbackTargets(ServerWorld world, ServerPlayerEntity serverPlayer, double px, double py, double pz) {
        Box area = new Box(
                px - RADIUS, py - RADIUS, pz - RADIUS,
                px + RADIUS, py + RADIUS, pz + RADIUS
        );

        var targets = world.getEntitiesByClass(
                LivingEntity.class,
                area,
                e -> e.isAlive() && e != serverPlayer && (!HIT_ONLY_MOBS || e instanceof MobEntity)
        );

        var sources = world.getDamageSources();

        for (LivingEntity target : targets) {
            double dx = target.getX() - px;
            double dz = target.getZ() - pz;
            double dist = Math.sqrt(dx * dx + dz * dz);

            if (dist > RADIUS || dist == 0.0) continue;

            double falloff = 1.0 - (dist / RADIUS);
            float damage = (float) (MAX_DAMAGE * falloff);

            if (damage > 0.0f) {
                target.damage(world, sources.playerAttack(serverPlayer), damage);
            }

            double nx = dx / dist;
            double nz = dz / dist;
            double kb = MAX_KNOCKBACK * (0.5 + 0.5 * falloff);

            target.takeKnockback(kb, -nx, -nz);
            target.addVelocity(0.0, Y_BOOST * falloff, 0.0);
            target.velocityDirty = true;
        }
    }
}