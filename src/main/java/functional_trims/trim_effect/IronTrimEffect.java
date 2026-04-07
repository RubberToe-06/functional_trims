package functional_trims.trim_effect;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.phys.Vec3;

public class IronTrimEffect implements ServerTickEvents.EndTick {

    private static final double Y_BOOST = 0.15;
    private static final double MAX_RANGE = 4.0;

    private static double knockbackStrength() {
        return ConfigManager.get().iron.shieldKnockbackStrengthMultiplier;
    }

    private static double chanceToDeflect() {
        return ConfigManager.get().iron.projectileReflectChance;
    }

    private static boolean cooldownNegationEnabled() {
        return ConfigManager.get().iron.axeAttackResistanceEnabled;
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(new IronTrimEffect());

        // --- Projectile & Falling Block Deflection (50% chance) ---
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, _) -> {
            if (!(entity instanceof ServerPlayer player)) return true;
            if (!TrimHelper.hasFullTrim(player, TrimMaterials.IRON)) return true;
            ServerLevel world = player.level();
            if (!FTConfig.isTrimEnabled("iron")) return true;

            Entity srcEntity = source.getDirectEntity();
            boolean isProjectile = srcEntity instanceof Projectile;
            boolean isFallingBlock = source.is(net.minecraft.world.damagesource.DamageTypes.FALLING_BLOCK)
                    || srcEntity instanceof FallingBlockEntity;

            if (!isProjectile && !isFallingBlock) return true;

            // 50% chance to deflect
            if (player.getRandom().nextFloat() < chanceToDeflect()) {
                // Sound + particle
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS,
                        0.6f, 1.6f);

                world.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.CRIT,
                        player.getX(),
                        player.getY(0.5),
                        player.getZ(),
                        8, 0.2, 0.2, 0.2, 0.03
                );

                // --- Projectile Reflection ---
                if (isProjectile) {
                    Projectile proj = (Projectile) srcEntity;
                    Vec3 vel = proj.getDeltaMovement();
                    proj.setDeltaMovement(-vel.x, vel.y * 0.75, -vel.z);
                    proj.needsSync = true;

                    // Trigger advancement (Reflect Projectile)
                    ModCriteria.TRIM_TRIGGER.trigger(player, "iron", "reflect_projectile");
                }

                // Cancel damage
                return false;
            }

            return true;
        });

        // --- Critical hit negation ---
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, amount, _, _) -> {
            if (!(entity instanceof ServerPlayer player)) return;
            if (!TrimHelper.hasFullTrim(player, TrimMaterials.IRON)) return;
            if (!FTConfig.isTrimEnabled("iron")) return;

            Entity attacker = source.getEntity();
            if (attacker instanceof ServerPlayer pAttacker) {
                boolean isCrit = pAttacker.fallDistance > 0.0F
                        && !pAttacker.onGround()
                        && !pAttacker.onClimbable()
                        && !pAttacker.isInWater();

                if (isCrit) {
                    float healBack = amount * 0.5f;
                    player.heal(healBack);

                    ServerLevel world = player.level();
                    world.playSound(null, player.blockPosition(),
                            SoundEvents.ANVIL_PLACE,
                            SoundSource.PLAYERS,
                            0.3f, 1.15f);
                }
            }
        });

        // --- Knockback on block ---
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, _, _, blocked) -> {
            if (!(entity instanceof ServerPlayer player)) return;
            if (!blocked) return;
            if (!player.isBlocking()) return;
            if (!TrimHelper.hasFullTrim(player, TrimMaterials.IRON)) return;
            ServerLevel world = player.level();
            if (!FTConfig.isTrimEnabled("iron")) return;

            Entity attackerEntity = source.getEntity();
            if (!(attackerEntity instanceof LivingEntity attacker)) return;

            // Ignore projectiles
            if (!(source.isDirect()) || source.getDirectEntity() instanceof Projectile) return;

            double distanceSq = player.distanceToSqr(attacker);
            if (distanceSq > MAX_RANGE * MAX_RANGE) return;

            double dx = attacker.getX() - player.getX();
            double dz = attacker.getZ() - player.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist < 1.0e-6) dist = 1.0e-6;
            double nx = dx / dist;
            double nz = dz / dist;

            attacker.knockback(knockbackStrength(), -nx, -nz);
            attacker.push(0.0, Y_BOOST, 0.0);
            attacker.needsSync = true;

            world.playSound(null, player.blockPosition(),
                    SoundEvents.ANVIL_PLACE,
                    SoundSource.PLAYERS,
                    0.3f, 1.15f);

            // --- Advancement Trigger: Knockback Attacker ---
            ModCriteria.TRIM_TRIGGER.trigger(player, "iron", "knockback_attacker");
        });
    }

    @Override
    public void onEndTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!TrimHelper.hasFullTrim(player, TrimMaterials.IRON)) continue;
            if (!FTConfig.isTrimEnabled("iron")) return;
            if (!cooldownNegationEnabled()) return;

            var cooldowns = player.getCooldowns();

            if (player.getOffhandItem().is(Items.SHIELD)) {
                Identifier group = cooldowns.getCooldownGroup(player.getOffhandItem());
                cooldowns.removeCooldown(group);
            }
            if (player.getMainHandItem().is(Items.SHIELD)) {
                Identifier group = cooldowns.getCooldownGroup(player.getMainHandItem());
                cooldowns.removeCooldown(group);
            }
        }
    }
}