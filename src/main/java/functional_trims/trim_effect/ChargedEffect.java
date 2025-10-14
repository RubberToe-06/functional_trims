package functional_trims.trim_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class ChargedEffect extends StatusEffect {

    private static final UUID SPEED_MODIFIER_ID = UUID.fromString("1b853c68-0d23-4b41-a1a4-8b3cb1f2028e");
    private static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("dc9822e4-4b86-4db5-a6cf-f8e58a20c9ac");

    public ChargedEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xE88032); // copper orange

        // Passive speed bonus
        this.addAttributeModifier(
                EntityAttributes.MOVEMENT_SPEED,
                Identifier.of(SPEED_MODIFIER_ID.toString()),
        0.20, // +20% movement speed
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );

        // Passive attack speed bonus
        this.addAttributeModifier(
                EntityAttributes.ATTACK_SPEED,
                Identifier.of(ATTACK_SPEED_MODIFIER_ID.toString()),
                0.25, // +25% attack speed
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    @Override
    public void onEntityDamage(ServerWorld world, LivingEntity entity, int amplifier, DamageSource source, float amount) {
        if (!entity.hasStatusEffect(ModEffects.CHARGED)) return;
        if (!(source.getAttacker() instanceof LivingEntity target) || target == entity) return;

        // --- Lightning strike ---
        LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightning.refreshPositionAfterTeleport(
                target.getX() + (world.getRandom().nextDouble() - 0.5) * 0.5, // small random offset
                target.getY(),
                target.getZ() + (world.getRandom().nextDouble() - 0.5) * 0.5
        );
        world.spawnEntity(lightning);


        // --- Explosion particles ---
        world.spawnParticles(
                ParticleTypes.EXPLOSION,
                target.getX(),
                target.getBodyY(0.5),
                target.getZ(),
                1,
                0.0, 0.0, 0.0,
                0.0
        );

        // --- Knockback ---
        Vec3d knockback = target.getPos().subtract(entity.getPos()).normalize().multiply(1.5);
        target.addVelocity(knockback.x, 0.6, knockback.z);
        target.velocityModified = true;

        // --- Damage + Fire ---
        float boostedDamage = amount * 1.25F;
        target.damage(world, world.getDamageSources().mobAttack(entity), boostedDamage);
        target.setFireTicks(80);

        // --- Remove Charged effect safely next tick ---
        world.getServer().execute(() -> entity.removeStatusEffect(ModEffects.CHARGED));

        // --- Sounds ---
        world.playSound(
                null,
                target.getBlockPos(),
                SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT,
                SoundCategory.PLAYERS,
                3.0F,
                0.8F
        );
        world.playSound(
                null,
                target.getBlockPos(),
                SoundEvents.ENTITY_GENERIC_EXPLODE.value(),
                SoundCategory.PLAYERS,
                2.5F,
                1.0F
        );
    }
}
