package functional_trims.mixin;

import functional_trims.func.TrimHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Adds the Gold trim bonus to piglins' hostility logic
@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {
    @Inject(
            method = "isWearingPiglinSafeArmor(Lnet/minecraft/entity/LivingEntity;)Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void checkForGoldTrim(LivingEntity entity,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            return; // already safe from vanilla gold armor
        }

        if (entity instanceof PlayerEntity player) {
            int goldTrims = TrimHelper.countTrim(player, ArmorTrimMaterials.GOLD);
            if (goldTrims == 4) {
                cir.setReturnValue(true);
            }
        }
    }
}
