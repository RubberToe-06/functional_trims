package functional_trims.trim_effect;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.phys.AABB;

public class DiamondTrimEffect {

    private static final double BURST_RADIUS = 6.0;
    private static final float MAX_BURST_DAMAGE = 20.0f;
    private static final double MAX_KNOCKBACK = 2.25;
    private static final double Y_BOOST = 0.35;
    private static final boolean HIT_ONLY_MOBS = true;

    private DiamondTrimEffect() {}

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((LivingEntity living, net.minecraft.world.damagesource.DamageSource _, float _) -> {
            if (!(living instanceof ServerPlayer player)) return true;
            if (!FTConfig.isTrimEnabled("diamond")) return true;
            if (TrimHelper.countTrim(player, TrimMaterials.DIAMOND) != 4) return true;

            savePlayerFromDeath(player);
            damageWornArmor(player);
            doBurst(player);

            return false;
        });
    }

    private static void savePlayerFromDeath(ServerPlayer player) {
        float regainedHealth = Math.max(1.0f, player.getMaxHealth() * percentHealthRegained());
        player.setHealth(regainedHealth);

        // tiny grace so the same-tick multi-hit doesn’t finish them off immediately
        player.invulnerableTime = Math.max(player.invulnerableTime, 20);
    }

    private static void damageWornArmor(ServerPlayer player) {
        float percent = Math.clamp(percentArmorDurabilityLost(), 0.0f, 1.0f);

        for (EquipmentSlot slot : new EquipmentSlot[] {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        }) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty() || !stack.isDamageableItem()) continue;

            int durabilityLoss = Math.round(stack.getMaxDamage() * percent);
            if (durabilityLoss <= 0) continue;

            stack.hurtAndBreak(durabilityLoss, player, slot);
        }
    }

    private static void doBurst(ServerPlayer player) {
        ServerLevel world = player.level();

        double px = player.getX();
        double py = player.getY() + player.getEyeHeight() * 0.5;
        double pz = player.getZ();

        playBurstEffects(world, px, py, pz);
        ModCriteria.TRIM_TRIGGER.trigger(player, "diamond", "armor_shatter");
        damageAndKnockbackTargets(world, player, px, py, pz);
    }

    private static void playBurstEffects(ServerLevel world, double px, double py, double pz) {
        BlockPos center = BlockPos.containing(px, py, pz);

        world.playSound(
                null,
                px, py, pz,
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.PLAYERS,
                0.25f,
                1.2f
        );
        world.playSound(
                null,
                center,
                SoundEvents.AMETHYST_BLOCK_RESONATE,
                SoundSource.PLAYERS,
                1.0f,
                0.7f
        );
        world.playSound(
                null,
                center,
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS,
                0.6f,
                1.6f
        );

        world.sendParticles(ParticleTypes.END_ROD, px, py, pz, 120, 0.75, 0.6, 0.75, 0.12);
        world.sendParticles(ParticleTypes.GLOW, px, py, pz, 40, 0.40, 0.30, 0.40, 0.10);

        for (int i = 0; i < 48; i++) {
            double t = (i / 48.0) * Math.PI * 2.0;
            double rx = px + Math.cos(t) * 1.2;
            double rz = pz + Math.sin(t) * 1.2;
            world.sendParticles(ParticleTypes.CRIT, rx, py + 0.2, rz, 1, 0.02, 0.02, 0.02, 0.0);
        }
    }

    private static void damageAndKnockbackTargets(ServerLevel world, ServerPlayer player, double px, double py, double pz) {
        AABB area = new AABB(
                px - BURST_RADIUS, py - BURST_RADIUS, pz - BURST_RADIUS,
                px + BURST_RADIUS, py + BURST_RADIUS, pz + BURST_RADIUS
        );

        var targets = world.getEntitiesOfClass(
                LivingEntity.class,
                area,
                entity -> entity.isAlive()
                        && entity != player
                        && (!HIT_ONLY_MOBS || entity instanceof Mob)
        );

        var sources = world.damageSources();

        for (LivingEntity target : targets) {
            double dx = target.getX() - px;
            double dz = target.getZ() - pz;
            double dist = Math.sqrt(dx * dx + dz * dz);

            if (dist > BURST_RADIUS || dist == 0.0) continue;

            double falloff = 1.0 - (dist / BURST_RADIUS);
            float damage = (float) (MAX_BURST_DAMAGE * falloff);

            if (damage > 0.0f) {
                target.hurtServer(world, sources.playerAttack(player), damage);
            }

            double nx = dx / dist;
            double nz = dz / dist;
            double knockback = MAX_KNOCKBACK * (0.5 + 0.5 * falloff);

            target.knockback(knockback, -nx, -nz);
            target.push(0.0, Y_BOOST * falloff, 0.0);
            target.needsSync = true;
        }
    }

    private static float percentHealthRegained() {
        return ConfigManager.get().diamond.percentHealthRegainedAfterBurst;
    }

    private static float percentArmorDurabilityLost() {
        return ConfigManager.get().diamond.percentArmorDurabilityLostAfterBurst;
    }
}