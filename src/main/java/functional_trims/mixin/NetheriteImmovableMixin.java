package functional_trims.mixin;

import functional_trims.criteria.ModCriteria;
import functional_trims.func.TrimHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels all external velocity changes (explosions, water currents, projectiles)
 * for players wearing a full Netherite-trimmed armor set.
 *
 * Also triggers the "That Was Cute..." advancement when a Netherite-trimmed
 * player resists an explosion without being moved.
 */
@Mixin(Entity.class)
public abstract class NetheriteImmovableMixin {

    @Inject(method = "addVelocity", at = @At("HEAD"), cancellable = true)
    private void functionalTrims$cancelExternalAddVelocity(Vec3d velocity, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        // Only apply to players
        if (!(self instanceof PlayerEntity player)) return;

        // Only if the player has full Netherite trims
        if (TrimHelper.countTrim(player, ArmorTrimMaterials.NETHERITE) < 4) return;

        // Ignore player's own movement logic (walking/jumping)
        for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
            String name = e.getClassName();
            // Don't block intentional motion
            if (name.contains("PlayerEntity") || name.contains("LivingEntity")) {
                return;
            }
        }

        // Detect if the velocity came from an explosion (by stack trace)
        boolean fromExplosion = false;
        for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
            if (e.getClassName().contains("Explosion")) {
                fromExplosion = true;
                break;
            }
        }

        // Trigger advancement when resisting explosion knockback
        if (fromExplosion && player instanceof ServerPlayerEntity serverPlayer) {
            ModCriteria.TRIM_TRIGGER.trigger(serverPlayer, "netherite", "resist_explosion");

            // Optional: metallic "clunk" feedback
            serverPlayer.getEntityWorld().playSound(
                    null,
                    serverPlayer.getBlockPos(),
                    SoundEvents.BLOCK_NETHERITE_BLOCK_STEP,
                    SoundCategory.PLAYERS,
                    0.7f,
                    0.5f
            );
        }

        // Cancel any external velocity additions (explosions, arrows, water, etc.)
        ci.cancel();
    }

    @Inject(method = "setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
            at = @At("HEAD"), cancellable = true)
    private void functionalTrims$cancelExternalSetVelocity(Vec3d velocity, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;

        if (!(self instanceof PlayerEntity player)) return;
        if (TrimHelper.countTrim(player, ArmorTrimMaterials.NETHERITE) < 4) return;

        // Ignore internal player movement
        for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
            String name = e.getClassName();
            if (name.contains("PlayerEntity") || name.contains("LivingEntity")) {
                return;
            }
        }

        ci.cancel();
    }
}
