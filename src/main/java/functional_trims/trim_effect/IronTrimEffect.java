package functional_trims.trim_effect;

import functional_trims.config.ConfigManager;
import functional_trims.config.FTConfig;
import functional_trims.config.FunctionalTrimsConfig;
import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Items;
import net.minecraft.item.trim.ArmorTrimMaterials;
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
            if (!(player.getEntityWorld() instanceof ServerWorld world)) return true;
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
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!(entity instanceof ServerPlayerEntity player)) return true;
            if (!TrimHelper.hasFullTrim(player, ArmorTrimMaterials.IRON)) return true;
            if (!FTConfig.isTrimEnabled("iron")) return true;

            Entity attacker = source.getAttacker();
            if (!(attacker instanceof ServerPlayerEntity pAttacker)) return true;

            boolean isCrit = pAttacker.fallDistance > 0.0F
                    && !pAttacker.isOnGround()
                    && !pAttacker.isClimbing()
                    && !pAttacker.isTouchingWater();

            if (isCrit && player.getEntityWorld() instanceof ServerWorld world) {
                world.getServer().execute(() ->
                        entity.damage(source, amount * 0.5f)
                );
                world.playSound(null, player.getBlockPos(),
                        SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.3f, 1.15f);
                return false;
            }
            return true;
        });

        // --- Knockback on block ---
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!(entity instanceof ServerPlayerEntity player)) return true;
            if (!player.isBlocking()) return true;
            if (!TrimHelper.hasFullTrim(player, ArmorTrimMaterials.IRON)) return true;
            if (!(player.getEntityWorld() instanceof ServerWorld world)) return true;
            if (!FTConfig.isTrimEnabled("iron")) return true;

            Entity attackerEntity = source.getAttacker();
            if (!(attackerEntity instanceof LivingEntity attacker)) return true;
            if (!source.isDirect() || source.getSource() instanceof ProjectileEntity) return true;

            double distanceSq = player.squaredDistanceTo(attacker);
            if (distanceSq > MAX_RANGE * MAX_RANGE) return true;

            double dx = attacker.getX() - player.getX();
            double dz = attacker.getZ() - player.getZ();
            double dist = Math.max(Math.sqrt(dx * dx + dz * dz), 1.0e-6);

            attacker.takeKnockback(KB_STRENGTH, -(dx / dist), -(dz / dist));
            attacker.addVelocity(0.0, Y_BOOST, 0.0);
            attacker.velocityDirty = true;

            world.playSound(null, player.getBlockPos(),
                    SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.3f, 1.15f);

            // --- Advancement Trigger: Knockback Attacker ---
            ModCriteria.TRIM_TRIGGER.trigger(player, "iron", "knockback_attacker");
            return true;
        });
    }

    @Override
    public void onEndTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (!TrimHelper.hasFullTrim(player, ArmorTrimMaterials.IRON)) continue;
            if (!FTConfig.isTrimEnabled("iron")) return;

            player.getItemCooldownManager().remove(Items.SHIELD);
        }
    }
}
