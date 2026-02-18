package functional_trims.mixin;

import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Handles explosion knockback for Netherite-trimmed players safely.
 *
 * NOTE:
 * - Explosion knockback does NOT use LivingEntity#takeKnockback.
 * - The explosion pipeline checks Entity#isImmuneToExplosion(Explosion).
 * - Returning true here prevents explosion knockback (and also explosion damage).
 */
@Mixin(Entity.class)
public abstract class NetheriteImmovableMixin {

    @Inject(method = "isImmuneToExplosion", at = @At("HEAD"), cancellable = true)
    private void functional_trims$netheriteTrimExplosionImmunity(Explosion explosion, CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;

        if (!(self instanceof ServerPlayerEntity player)) return;
        if (TrimHelper.countTrim(player, ArmorTrimMaterials.NETHERITE) < 4) return;

        // Trigger advancement + feedback when an explosion tries to affect the player
        ModCriteria.TRIM_TRIGGER.trigger(player, "netherite", "resist_explosion");

        player.getWorld().playSound(
                null,
                player.getBlockPos(),
                SoundEvents.BLOCK_NETHERITE_BLOCK_STEP,
                SoundCategory.PLAYERS,
                0.7f,
                0.5f
        );

        // Immunity to explosion knockback (and damage)
        cir.setReturnValue(true);
    }
}
