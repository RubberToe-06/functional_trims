package functional_trims.trim_effect;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class IronTrimEffect implements ServerTickEvents.EndTick {

    private static final double KB_STRENGTH = ConfigManager.get().shieldKnockbackStrengthMultiplier;
    private static final double CHANCE_TO_DEFLECT = ConfigManager.get().projectileReflectChance;
    private static final double Y_BOOST = 0.15;
    private static final double MAX_RANGE = 4.0;
    private static final boolean COOLDOWN_NEGATION_ENABLED = ConfigManager.get().axeAttackResistanceEnabled;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(new IronTrimEffect());

        // --- Projectile & Falling Block Deflection (50% chance) ---
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!(entity instanceof ServerPlayerEntity player)) return true;
            if (!TrimHelper.hasFullTrim(player, ArmorTrimMaterials.IRON)) return true;
            if (!(player.getWorld() instanceof ServerWorld world)) return true;
            if (!FTConfig.isTrimEnabled("iron")) return true;

            Entity srcEntity = source.getSource();
            boolean isProjectile = srcEntity instanceof ProjectileEntity;
            boolean isFallingBlock = source.isOf(net.minecraft.entity.damage.DamageTypes.FALLING_BLOCK)
                    || srcEntity instanceof FallingBlockEntity;

            if (!isProjectile && !isFallingBlock) return true;

            // 50% chance to deflect
            if (player.getRandom().nextFloat() < 0.5f) {
                // Sound + particle
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS,
                        0.6f, 1.6f);

                world.spawnParticles(
                        net.minecraft.particle.ParticleTypes.CRIT,
                        player.getX(),
                        player.getBodyY(0.5),
                        player.getZ(),
                        8, 0.2, 0.2, 0.2, 0.03
                );

                // --- Projectile Reflection ---
                if (isProjectile) {
                    ProjectileEntity proj = (ProjectileEntity) srcEntity;
                    Vec3d vel = proj.getVelocity();
                    proj.setVelocity(-vel.x, vel.y * 0.75, -vel.z);
                    proj.velocityDirty = true;

                    // Trigger advancement (Reflect Projectile)
                    ModCriteria.TRIM_TRIGGER.trigger(player, "iron", "reflect_projectile");
                }

                // Cancel damage
                return false;
            }

            return true;
        });

        // --- Critical hit negation ---
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, amount, originalAmount, blocked) -> {
            if (!(entity instanceof ServerPlayerEntity player)) return;
            if (!TrimHelper.hasFullTrim(player, ArmorTrimMaterials.IRON)) return;
            if (!FTConfig.isTrimEnabled("iron")) return;

            Entity attacker = source.getAttacker();
            if (attacker instanceof ServerPlayerEntity pAttacker) {
                boolean isCrit = pAttacker.fallDistance > 0.0F
                        && !pAttacker.isOnGround()
                        && !pAttacker.isClimbing()
                        && !pAttacker.isTouchingWater();

                if (isCrit) {
                    float healBack = amount * 0.5f;
                    player.heal(healBack);

                    if (player.getWorld() instanceof ServerWorld world) {
                        world.playSound(null, player.getBlockPos(),
                                SoundEvents.BLOCK_ANVIL_PLACE,
                                SoundCategory.PLAYERS,
                                0.3f, 1.15f);
                    }
                }
            }
        });

        // --- Knockback on block ---
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, amount, originalAmount, blocked) -> {
            if (!(entity instanceof ServerPlayerEntity player)) return;
            if (!blocked) return;
            if (!player.isBlocking()) return;
            if (!TrimHelper.hasFullTrim(player, ArmorTrimMaterials.IRON)) return;
            if (!(player.getWorld() instanceof ServerWorld world)) return;
            if (!FTConfig.isTrimEnabled("iron")) return;

            Entity attackerEntity = source.getAttacker();
            if (!(attackerEntity instanceof LivingEntity attacker)) return;

            // Ignore projectiles
            if (!(source.isDirect()) || source.getSource() instanceof ProjectileEntity) return;

            double distanceSq = player.squaredDistanceTo(attacker);
            if (distanceSq > MAX_RANGE * MAX_RANGE) return;

            double dx = attacker.getX() - player.getX();
            double dz = attacker.getZ() - player.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist < 1.0e-6) dist = 1.0e-6;
            double nx = dx / dist;
            double nz = dz / dist;

            attacker.takeKnockback(KB_STRENGTH, -nx, -nz);
            attacker.addVelocity(0.0, Y_BOOST, 0.0);
            attacker.velocityDirty = true;

            world.playSound(null, player.getBlockPos(),
                    SoundEvents.BLOCK_ANVIL_PLACE,
                    SoundCategory.PLAYERS,
                    0.3f, 1.15f);

            // --- Advancement Trigger: Knockback Attacker ---
            ModCriteria.TRIM_TRIGGER.trigger(player, "iron", "knockback_attacker");
        });
    }

    @Override
    public void onEndTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (!TrimHelper.hasFullTrim(player, ArmorTrimMaterials.IRON)) continue;
            if (!FTConfig.isTrimEnabled("iron")) return;

            var cooldowns = player.getItemCooldownManager();

            if (player.getOffHandStack().isOf(Items.SHIELD)) {
                Identifier group = cooldowns.getGroup(player.getOffHandStack());
                if (group != null) cooldowns.remove(group);
            }
            if (player.getMainHandStack().isOf(Items.SHIELD)) {
                Identifier group = cooldowns.getGroup(player.getMainHandStack());
                if (group != null) cooldowns.remove(group);
            }
        }
    }
}
