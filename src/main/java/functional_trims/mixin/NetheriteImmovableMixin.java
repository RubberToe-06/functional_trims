package functional_trims.mixin;

import functional_trims.func.TrimHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prevents all external velocity changes (explosions, water currents, entity push)
 * for players wearing a full Netherite-trimmed armor set.
 *
 * Does NOT interfere with piston logic or normal player input movement.
 */
@Mixin(Entity.class)
public abstract class NetheriteImmovableMixin {

    @Inject(method = "addVelocity", at = @At("HEAD"), cancellable = true)
    private void functionaltrims$cancelExternalAddVelocity(Vec3d velocity, CallbackInfo ci) {
        Entity self = (Entity)(Object)this;

        // Only apply to players
        if (!(self instanceof PlayerEntity player)) return;

        // Only if the player has full Netherite trims
        if (TrimHelper.countTrim(player, ArmorTrimMaterials.NETHERITE) < 4) return;

        // Ignore player’s own intentional movement updates
        for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
            String name = e.getClassName();
            if (name.contains("PlayerEntity") || name.contains("LivingEntity")) {
                return; // allow self-applied motion (walking, jumping, etc.)
            }
        }

        // Cancel any external velocity additions (explosions, arrows, etc.)
        ci.cancel();
    }

    @Inject(method = "setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
            at = @At("HEAD"), cancellable = true)
    private void functionaltrims$cancelExternalSetVelocity(Vec3d velocity, CallbackInfo ci) {
        Entity self = (Entity)(Object)this;

        // Only apply to players
        if (!(self instanceof PlayerEntity player)) return;

        // Only if the player has full Netherite trims
        if (TrimHelper.countTrim(player, ArmorTrimMaterials.NETHERITE) < 4) return;

        // Ignore player’s own movement logic
        for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
            String name = e.getClassName();
            if (name.contains("PlayerEntity") || name.contains("LivingEntity")) {
                return;
            }
        }

        // Cancel external setVelocity (explosions, physics)
        ci.cancel();
    }
}
