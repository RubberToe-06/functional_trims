package functional_trims.mixin;

import functional_trims.func.TrimHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class NetheriteKnockbackMixin {

    @Inject(method = "takeKnockback", at = @At("HEAD"), cancellable = true)
    private void functionaltrims$cancelKnockback(double strength, double x, double z, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;

        if (entity instanceof PlayerEntity player &&
                TrimHelper.countTrim(player, ArmorTrimMaterials.NETHERITE) == 4) {
            ci.cancel(); // No knockback from attacks or projectiles
        }
    }
}
