package functional_trims.mixin;

import functional_trims.config.FTConfig;
import functional_trims.func.TrimHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels standard knockback from attacks/projectiles for Netherite-trimmed players.
 * This does NOT cover explosions (explosions are handled via Entity#isImmuneToExplosion).
 */
@Mixin(LivingEntity.class)
public class NetheriteKnockbackMixin {

    @Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
    private void functional_trims$cancelKnockback(double d, double e, double f, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!FTConfig.isTrimEnabled("netherite")) return;

        if (entity instanceof Player player
                && TrimHelper.countTrim(player, TrimMaterials.NETHERITE) >= 4) {
            ci.cancel();
        }
    }
}