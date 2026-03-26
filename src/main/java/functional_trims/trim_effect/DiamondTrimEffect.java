package functional_trims.trim_effect;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class DiamondTrimEffect {

    private static final double BURST_RADIUS = 6.0;
    private static final float MAX_BURST_DAMAGE = 20.0f;
    private static final double MAX_KNOCKBACK = 2.25;
    private static final double Y_BOOST = 0.35;
    private static final boolean HIT_ONLY_MOBS = true;

    private DiamondTrimEffect() {}

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((LivingEntity living, net.minecraft.entity.damage.DamageSource source, float amount) -> {
            if (!(living instanceof ServerPlayerEntity player)) return true;
            if (!FTConfig.isTrimEnabled("diamond")) return true;
            if (TrimHelper.countTrim(player, ArmorTrimMaterials.DIAMOND) != 4) return true;

            savePlayerFromDeath(player);
            damageWornArmor(player);
            doBurst(player);

            return false;
        });
    }

    private static void savePlayerFromDeath(ServerPlayerEntity player) {
        float regainedHealth = Math.max(1.0f, player.getMaxHealth() * percentHealthRegained());
        player.setHealth(regainedHealth);

        // tiny grace so the same-tick multi-hit doesn’t finish them off immediately
        player.timeUntilRegen = Math.max(player.timeUntilRegen, 20);
    }

    private static void damageWornArmor(ServerPlayerEntity player) {
        float percent = Math.clamp(percentArmorDurabilityLost(), 0.0f, 1.0f);

        for (EquipmentSlot slot : new EquipmentSlot[] {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        }) {
            ItemStack stack = player.getEquippedStack(slot);
            if (stack.isEmpty() || !stack.isDamageable()) continue;

            int durabilityLoss = Math.round(stack.getMaxDamage() * percent);
            if (durabilityLoss <= 0) continue;

            stack.damage(durabilityLoss, player, slot);
        }
    }

    private static void doBurst(ServerPlayerEntity player) {
        if (!(player.getEntityWorld() instanceof ServerWorld world)) return;

        double px = player.getX();
        double py = player.getY() + player.getStandingEyeHeight() * 0.5;
        double pz = player.getZ();

        playBurstEffects(world, px, py, pz);
        ModCriteria.TRIM_TRIGGER.trigger(player, "diamond", "armor_shatter");
        damageAndKnockbackTargets(world, player, px, py, pz);
    }

    private static void playBurstEffects(ServerWorld world, double px, double py, double pz) {
        BlockPos center = BlockPos.ofFloored(px, py, pz);

        world.playSound(
                null,
                px, py, pz,
                SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.PLAYERS,
                0.25f,
                1.2f
        );
        world.playSound(
                null,
                center,
                SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                SoundCategory.PLAYERS,
                1.0f,
                0.7f
        );
        world.playSound(
                null,
                center,
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

    private static void damageAndKnockbackTargets(ServerWorld world, ServerPlayerEntity player, double px, double py, double pz) {
        Box area = new Box(
                px - BURST_RADIUS, py - BURST_RADIUS, pz - BURST_RADIUS,
                px + BURST_RADIUS, py + BURST_RADIUS, pz + BURST_RADIUS
        );

        var targets = world.getEntitiesByClass(
                LivingEntity.class,
                area,
                entity -> entity.isAlive()
                        && entity != player
                        && (!HIT_ONLY_MOBS || entity instanceof MobEntity)
        );

        var sources = world.getDamageSources();

        for (LivingEntity target : targets) {
            double dx = target.getX() - px;
            double dz = target.getZ() - pz;
            double dist = Math.sqrt(dx * dx + dz * dz);

            if (dist > BURST_RADIUS || dist == 0.0) continue;

            double falloff = 1.0 - (dist / BURST_RADIUS);
            float damage = (float) (MAX_BURST_DAMAGE * falloff);

            if (damage > 0.0f) {
                target.damage(world, sources.playerAttack(player), damage);
            }

            double nx = dx / dist;
            double nz = dz / dist;
            double knockback = MAX_KNOCKBACK * (0.5 + 0.5 * falloff);

            target.takeKnockback(knockback, -nx, -nz);
            target.addVelocity(0.0, Y_BOOST * falloff, 0.0);
            target.velocityDirty = true;
        }
    }

    private static float percentHealthRegained() {
        return ConfigManager.get().percentHealthRegainedAfterBurst;
    }

    private static float percentArmorDurabilityLost() {
        return ConfigManager.get().percentArmorDurabilityLostAfterBurst;
    }
}